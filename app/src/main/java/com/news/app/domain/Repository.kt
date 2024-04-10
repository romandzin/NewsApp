package com.news.app.domain

import com.news.app.data.model.Article
import com.news.app.data.model.Response
import com.news.app.data.model.Source
import com.news.app.data.retrofit.API_KEY
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import retrofit2.Call

interface Repository {

    fun getHeadlinesNews(category: String, pageSize: Int, page: Int): Observable<Response>

    fun getFilteredNews(from: String, to: String, language: String, sortBy: String, pageSize: Int, page: Int): Observable<Response>

    fun getSources(): Call<ArrayList<Source>>

    fun getSavedList(): Flowable<ArrayList<Article?>>

    suspend fun saveArticle(article: Article, savedDate: String)

    suspend fun deleteArticle(article: Article)

}