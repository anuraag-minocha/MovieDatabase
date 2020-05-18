package com.android.test

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), SearchAdapter.BookmarkListener,
    BookmarkAdapter.BookmarkRemoveListener {

    lateinit var viewModel: MainViewModel
    lateinit var searchAdapter: SearchAdapter
    lateinit var bookmarkAdapter: BookmarkAdapter
    lateinit var mLayoutManager: LinearLayoutManager
    var isLastPage = false
    var isLoading = false
    var page = 1
    var searchWord = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        searchAdapter = SearchAdapter(arrayListOf(), this)
        bookmarkAdapter = BookmarkAdapter(arrayListOf(), this)
        searchAdapter.mBookmarkListener = this
        bookmarkAdapter.mBookmarkRemoveListener = this
        mLayoutManager = LinearLayoutManager(this)

        setupView()
        setUpObserver()
        checkBookmarks()

        searchWord = "new" //default search word to load initial list
        viewModel.getMoviesList(searchWord, page++)
    }

    private fun setupView() {
        rvSearch.layoutManager = mLayoutManager
        rvSearch.adapter = searchAdapter
        rvBookmark.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvBookmark.adapter = bookmarkAdapter

        ivSearch.setOnClickListener {
            if (etSearch.text.toString().trim() != "") {
                searchWord = etSearch.text.toString()
                page = 1
                isLastPage = false
                searchAdapter.clearList()
                viewModel.getMoviesList(searchWord, page++)
                hideKeyboard()
            }
        }

        etSearch.setOnKeyListener { v, keyCode, event ->
            if ((event.action == KeyEvent.ACTION_DOWN) &&
                (keyCode == KeyEvent.KEYCODE_ENTER)
            ) {
                if (etSearch.text.toString().trim() != "") {
                    searchWord = etSearch.text.toString()
                    page = 1
                    isLastPage = false
                    searchAdapter.clearList()
                    viewModel.getMoviesList(searchWord, page++)
                    hideKeyboard()
                }
                true
            } else
                false
        }


        val recyclerViewOnScrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val visibleItemCount = mLayoutManager.childCount
                val totalItemCount = mLayoutManager.itemCount
                val firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition()

                if (!isLoading && !isLastPage) {
                    if (visibleItemCount + firstVisibleItemPosition + 1 >= totalItemCount) {
                        viewModel.getMoviesList(searchWord, page++)
                    }
                }
            }
        }

        rvSearch.addOnScrollListener(recyclerViewOnScrollListener)
    }

    private fun setUpObserver() {

        viewModel.moviesList.observe(this, Observer {
            if (it.isNotEmpty())
                searchAdapter.updateList(it)
            else
                isLastPage = true
        })

        viewModel.errorMessage.observe(this, Observer {
            if (page < 3)
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })

        viewModel.loading.observe(this, Observer {
            if (it) {
                isLoading = true
                progressBar.visibility = View.VISIBLE
            } else {
                isLoading = false
                progressBar.visibility = View.GONE
            }
        })

    }

    fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun checkBookmarks() {
        val preferences =
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)
        val string = preferences.getString("bookmarks", "")
        if (string != "") {
            val gson = Gson()
            val bookmarks = gson.fromJson<MovieList>(string, MovieList::class.java)
            bookmarkAdapter.updateList(bookmarks.movieList as ArrayList<Movie>)
            if (bookmarks.movieList.isNotEmpty())
                rvBookmark.visibility = View.VISIBLE
            else
                rvBookmark.visibility = View.GONE
        }
    }

    override fun onBookmarked() {
        checkBookmarks()
    }

    override fun onBookmarkRemoved(movie: Movie) {
        val index = searchAdapter.list.indexOfFirst { it.id == movie.id }
        if (index >= 0) {
            searchAdapter.list[index].isBookmarked = false
            searchAdapter.notifyItemChanged(index)
        }
    }

}
