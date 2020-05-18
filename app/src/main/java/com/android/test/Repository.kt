package com.android.test

import io.reactivex.Single

class Repository {

    private val networkService = Networking.create()

    fun getMoviesBySearch(keyword: String, page: Int): Single<MovieList> {
        return networkService.getMoviesBySearch(keyword, page, Endpoints.apiKey)
    }

    fun getMovieDetails(id: String): Single<MovieDetail> {
        return networkService.getMovieDetails(id, Endpoints.apiKey)
    }

}