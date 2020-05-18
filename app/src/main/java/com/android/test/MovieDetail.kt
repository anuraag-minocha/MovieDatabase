package com.android.test

import com.google.gson.annotations.SerializedName

data class MovieDetail(
    @SerializedName("Title")
    var title: String,
    @SerializedName("Year")
    var year: String,
    @SerializedName("Poster")
    var poster: String,
    @SerializedName("imdbID")
    var id: String,
    @SerializedName("Type")
    var type: String,
    @SerializedName("Genre")
    var genre: String
)