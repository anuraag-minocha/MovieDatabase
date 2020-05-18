package com.android.test

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class MainViewModel : ViewModel() {

    private val repository = Repository()
    private val compositeDisposable = CompositeDisposable()
    val moviesList = MutableLiveData<ArrayList<Movie>>()
    val movieDetail = MutableLiveData<MovieDetail>()
    val loading = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()

    fun getMoviesList(searchWord: String, page: Int) {
        loading.postValue(true)
        val disposable = object : DisposableSingleObserver<MovieList>() {
            override fun onSuccess(t: MovieList) {
                loading.postValue(false)
                if (!t.movieList.isNullOrEmpty())
                    moviesList.postValue(t.movieList as ArrayList<Movie>)
                else {
                    moviesList.postValue(arrayListOf())
                    errorMessage.postValue(t.error)
                }
            }

            override fun onError(e: Throwable) {
                loading.postValue(false)
                errorMessage.postValue(e.message)
            }
        }
        repository.getMoviesBySearch(searchWord, page).subscribeOn(Schedulers.io())
            .subscribe(disposable)
        compositeDisposable.add(disposable)

    }

    fun getMovieDetails(id: String) {
        loading.postValue(true)
        val disposable = object : DisposableSingleObserver<MovieDetail>() {
            override fun onSuccess(t: MovieDetail) {
                loading.postValue(false)
                movieDetail.postValue(t)
            }

            override fun onError(e: Throwable) {
                loading.postValue(false)
                errorMessage.postValue(e.message)
            }
        }
        repository.getMovieDetails(id).subscribeOn(Schedulers.io())
            .subscribe(disposable)
        compositeDisposable.add(disposable)
    }


    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}