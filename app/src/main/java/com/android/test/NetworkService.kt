package com.android.test

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query


interface NetworkService {

    @GET("/")
    fun getMoviesBySearch(@Query("s") keyword: String, @Query("page") page: Int, @Query("apikey") apikey: String): Single<MovieList>

    @GET("/")
    fun getMovieDetails(@Query("i") id: String, @Query("apikey") apikey: String): Single<MovieDetail>

}