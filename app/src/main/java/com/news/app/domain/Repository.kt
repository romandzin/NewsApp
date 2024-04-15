package com.news.app.domain

import com.news.app.data.model.Article
import com.news.app.data.model.ArticlesResponse
import com.news.app.data.model.Source
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable

interface Repository {

    fun getHeadlinesNews(category: String, pageSize: Int, page: Int): Observable<ArrayList<Article>>

    fun getHeadlinesNewsWithSource(sourceCategory: String, pageSize: Int, page: Int): Observable<ArticlesResponse>

    fun getFilteredNews(from: String, to: String, language: String, sortBy: String, pageSize: Int, page: Int): Observable<ArticlesResponse>

    fun getSources(): Observable<ArrayList<Source>>

    fun getSavedList(): Flowable<ArrayList<Article?>>

    suspend fun saveArticle(article: Article, savedDate: String)

    suspend fun deleteArticle(article: Article)
    fun saveToCache(articlesList: java.util.ArrayList<Article>, category: String)
}