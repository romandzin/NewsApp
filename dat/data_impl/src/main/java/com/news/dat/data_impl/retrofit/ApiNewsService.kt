package com.news.dat.data_impl.retrofit

import com.news.dat.data_impl.model.network_reponses.ArticlesResponse
import com.news.dat.data_impl.model.network_reponses.SourcesResponse
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiNewsService {

    @GET("/v2/top-headlines")
    fun getHeadlinesNews(@Query("category") category: String, @Query("pageSize") pageSize: Int, @Query("page") page: Int): Observable<ArticlesResponse>

    @GET("/v2/top-headlines")
    fun getHeadlinesNewsByQuery(@Query("category") category: String, @Query("q") query: String): Flowable<ArticlesResponse>

    @GET("/v2/top-headlines")
    fun getHeadlinesNewsWithSource(@Query("sources") source: String, @Query("pageSize") pageSize: Int, @Query("page") page: Int): Observable<ArticlesResponse>

    @GET("/v2/everything")
    fun getFilteredNews(@Query("q") q: String = "everything", @Query("from") from: String, @Query("to") to: String, @Query("language") language: String, @Query("sortBy") sortBy: String): Observable<ArticlesResponse>

    @GET("/v2/top-headlines/sources")
    fun getSources(): Observable<SourcesResponse>
}