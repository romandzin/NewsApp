package com.news.app.model.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val API_KEY = "116051844f2445fd8ce075f328b2ac79"

object RetrofitObj {

    private const val BASE_URL = "https://newsapi.org/"
    private val retrofit: Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val service: ApiNewsService = retrofit.create(ApiNewsService::class.java)

}