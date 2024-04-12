package com.news.app.data

import android.annotation.SuppressLint
import android.util.Log
import com.news.app.data.db.CachedDao
import com.news.app.data.db.SavedDao
import com.news.app.data.mappers.ArticlesMapper
import com.news.app.data.model.Article
import com.news.app.data.model.ArticlesResponse
import com.news.app.data.model.Source
import com.news.app.data.retrofit.ApiNewsService
import com.news.app.domain.Repository
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    var newsServiceApi: ApiNewsService,
    var savedDao: SavedDao,
    var cachedDao: CachedDao,
    var articlesMapper: ArticlesMapper
) : Repository {

    @SuppressLint("CheckResult")
    override fun getHeadlinesNews(
        category: String,
        pageSize: Int,
        page: Int
    ): Observable<ArrayList<Article>> {
        return cachedDao.getCachedArticlesByPage(page)
            .take(1)
            .flatMap {
                Log.d("tag", it.toString())
                if (it.isEmpty()) {
                    return@flatMap newsServiceApi.getHeadlinesNews(category, pageSize, page)
                        .flatMap { response ->
                            saveToCache(response.articles, page)
                            Observable.fromArray(response.articles)
                        }
                } else {
                    val articlesList: ArrayList<Article> =
                        it.map { articlesMapper.transform(it) } as ArrayList<Article>
                    return@flatMap Observable.fromArray(articlesList)
                }
            }
    }

    override fun getHeadlinesNewsWithSource(
        sourceCategory: String,
        pageSize: Int,
        page: Int
    ): Observable<ArticlesResponse> {
        return newsServiceApi.getHeadlinesNews(sourceCategory, pageSize, page)
    }

    override fun getFilteredNews(
        from: String,
        to: String,
        language: String,
        sortBy: String,
        pageSize: Int,
        page: Int
    ): Observable<ArticlesResponse> {
        return newsServiceApi.getFilteredNews(
            from = from,
            to = to,
            language = language,
            sortBy = sortBy,
            pageSize = pageSize,
            page = page
        )
    }

    override fun getSources(): Observable<ArrayList<Source>> {
        return newsServiceApi.getSources().flatMap { sourceResponse ->
            Observable.fromArray(sourceResponse.sourcesList)
        }
    }

    @SuppressLint("CheckResult")
    override fun getSavedList(): Flowable<ArrayList<Article?>> {
        return savedDao.getAllArticles()
            .subscribeOn(Schedulers.io())
            .flatMap { dbEntityList ->
                val articlesList = dbEntityList.map { dbElement ->
                    if (checkIfDateIsOld(dbElement.savedDate)) articlesMapper.transform(dbElement)
                    else null
                } as java.util.ArrayList
                Flowable.fromArray(articlesList)
            }
    }

    private fun checkIfDateIsOld(articleSavedDateString: String?): Boolean {
        val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val articleSavedData = sdf.parse(articleSavedDateString)
        return if (articleSavedData != null) {
            val timeDifference = Calendar.getInstance().time.time - articleSavedData.time
            val daysDiff = TimeUnit.MILLISECONDS.toDays(timeDifference)
            Log.d("tag", daysDiff.toString())
            daysDiff < 14L
        } else false
    }

    override suspend fun saveArticle(article: Article, savedDate: String) {
        savedDao.insertNewArticle(articlesMapper.transform(article, savedDate))
    }

    override suspend fun deleteArticle(article: Article) {
        savedDao.deleteArticleByTitle(article.newsTitle!!)
    }

    override fun saveToCache(articlesList: java.util.ArrayList<Article>, page: Int){
        for (i in articlesList)
            cachedDao.insertCacheArticle(articlesMapper.transform(i, page))
    }
}