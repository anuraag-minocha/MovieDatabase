package com.android.test

import com.google.gson.annotations.SerializedName

data class Movie(
    @SerializedName("Title")
    var title: String,
    @SerializedName("Year")
    var year: String,
    @SerializedName("Poster")
    var poster: String,
    @SerializedName("imdbID")
    var id: String,
    var isBookmarked: Boolean = false
)