package com.android.test

import com.google.gson.annotations.SerializedName

data class MovieList(
    @SerializedName("Search")
    var movieList: List<Movie>,
    @SerializedName("Error")
    var error: String
)