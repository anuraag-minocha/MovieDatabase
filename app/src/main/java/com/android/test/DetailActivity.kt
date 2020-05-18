package com.android.test

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_main.progressBar

class DetailActivity : AppCompatActivity() {

    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.getMovieDetails(intent.getStringExtra("id")!!)
        setUpObserver()
    }

    private fun setUpObserver() {

        viewModel.movieDetail.observe(this, Observer {
            it?.let {
                Picasso.with(this@DetailActivity).load(it.poster).into(imageView)
                name.text = it.title
                year.text = getString(R.string.year, it.year)
                type.text = it.type + " | " + it.genre
            }
        })

        viewModel.loading.observe(this, Observer {
            if (it) {
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.GONE
            }
        })

    }


}