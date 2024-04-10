package com.news.app.data.retrofit

import com.news.app.data.model.Response
import com.news.app.data.model.Source
import io.reactivex.rxjava3.core.Observable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiNewsService {

    @GET("/v2/top-headlines")
    fun getHeadlinesNews(@Query("category") category: String, @Query("pageSize") pageSize: Int, @Query("page") page: Int): Observable<Response>

    @GET("/v2/everything")
    fun getFilteredNews(@Query("q") q: String = "all", @Query("from") from: String, @Query("to") to: String, @Query("language") language: String, @Query("sortBy") sortBy: String, @Query("pageSize") pageSize: Int, @Query("page") page: Int): Observable<Response>

    @GET("/v2/top-headlines/sources")
    fun getSources(): Call<ArrayList<Source>>
}