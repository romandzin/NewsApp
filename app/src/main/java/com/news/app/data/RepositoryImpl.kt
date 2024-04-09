package com.news.app.data

import android.annotation.SuppressLint
import android.content.Context
import com.news.app.data.db.DbObject
import com.news.app.data.db.SavedDao
import com.news.app.data.mappers.ArticleDbToArticleMapper
import com.news.app.data.model.Article
import com.news.app.data.model.ArticleDbEntity
import com.news.app.data.model.Response
import com.news.app.data.model.Source
import com.news.app.data.retrofit.ApiNewsService
import com.news.app.data.retrofit.RetrofitObj
import com.news.app.domain.Repository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import java.lang.Exception
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    var newsServiceApi: ApiNewsService,
    var savedDao: SavedDao,
    var articleDbToArticleMapper: ArticleDbToArticleMapper
): Repository {

    override fun getHeadlinesNews(category: String, pageSize: Int, page: Int, apiKey: String): Observable<Response> {
        return newsServiceApi.getHeadlinesNews(category, pageSize, page, apiKey)
    }

    override fun getFilteredNews(
        from: String,
        to: String,
        language: String,
        sortBy: String,
        pageSize: Int,
        page: Int,
        apiKey: String
    ): Observable<Response> {
        return newsServiceApi.getFilteredNews(from = from, to = to, language = language, sortBy = sortBy, pageSize = pageSize, page = page, apiKey = apiKey)
    }

    override fun getSources(): Call<ArrayList<Source>> {
        return newsServiceApi.getSources()
    }

    @SuppressLint("CheckResult")
    override fun getSavedList(): Flowable<ArrayList<Article>> {
       return savedDao.getAllArticles()
            .subscribeOn(Schedulers.io())
            .flatMap {
                dbEntityList ->
                    val articlesList = dbEntityList.map { dbElement -> articleDbToArticleMapper.transform(dbElement)} as java.util.ArrayList
                    Flowable.fromArray(articlesList)
            }
    }
}