package com.news.data.data_api

import com.news.data.data_api.model.Article
import com.news.data.data_api.model.Filters
import com.news.data.data_api.model.Source
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable

interface Repository {

    fun getHeadlinesNews(category: String, pageSize: Int, page: Int): Observable<ArrayList<Article>>

    fun getHeadlinesNewsByQuery(category: String, query: String): Flowable<ArrayList<Article>>

    fun getHeadlinesNewsWithSource(source: String, pageSize: Int, page: Int): Observable<ArrayList<Article>>

    fun getFilteredNews(filters: Filters, isInternetEnabled: Boolean): Observable<ArrayList<Article>>

    fun getFilteredNewsInCache(filters: Filters): Observable<ArrayList<Article>>

    fun getSources(): Observable<ArrayList<Source>>

    fun getSavedList(): Flowable<ArrayList<Article?>>

    suspend fun saveArticle(article: Article, savedDate: String)

    suspend fun deleteArticle(article: Article)
    fun saveToCache(articlesList: java.util.ArrayList<Article>, category: String, page: Int)
    fun getAllCachedHeadlinesByPage(category: String, page: Int): Observable<ArrayList<Article>>
}