package com.news.dat.data_impl.retrofit

import com.news.dat.data_impl.interceptors.AuthInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


const val API_KEY = "116051844f2445fd8ce075f328b2ac79"

object RetrofitObj {

    private const val BASE_URL = "https://newsapi.org/"

    private val okHttpClient = OkHttpClient().newBuilder().addInterceptor(AuthInterceptor()).build()

    private val retrofit: Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val service: ApiNewsService = retrofit.create(ApiNewsService::class.java)

}