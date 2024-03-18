package com.news.app.model.retrofit

import com.news.app.model.data_classes.News
import com.news.app.model.data_classes.Response
import com.news.app.model.data_classes.Source
import io.reactivex.rxjava3.core.Observable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiNewsService {

    @GET("/v2/top-headlines")
    fun getHeadlinesNews(@Query("category") category: String, @Query("apiKey") apiKey: String = API_KEY): Observable<Response>

    @GET("/v2/top-headlines/sources?apiKey=${API_KEY}")
    fun getSources(): Call<ArrayList<Source>>
}