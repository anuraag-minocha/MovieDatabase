package com.android.test

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Networking {

    var networkService: NetworkService? = null

    fun create(): NetworkService {
        if (networkService == null) {
            networkService = Retrofit.Builder()
                .baseUrl(Endpoints.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(NetworkService::class.java)
        }
        return networkService!!
    }
}