package com.news.app.data

import android.annotation.SuppressLint
import android.util.Log
import com.news.app.data.db.SavedDao
import com.news.app.data.mappers.ArticlesMapper
import com.news.app.data.model.Article
import com.news.app.data.model.Response
import com.news.app.data.model.Source
import com.news.app.data.retrofit.ApiNewsService
import com.news.app.domain.Repository
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.Call
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    var newsServiceApi: ApiNewsService,
    var savedDao: SavedDao,
    var articlesMapper: ArticlesMapper
) : Repository {

    override fun getHeadlinesNews(
        category: String,
        pageSize: Int,
        page: Int
    ): Observable<Response> {
        return newsServiceApi.getHeadlinesNews(category, pageSize, page)
    }

    override fun getFilteredNews(
        from: String,
        to: String,
        language: String,
        sortBy: String,
        pageSize: Int,
        page: Int
    ): Observable<Response> {
        return newsServiceApi.getFilteredNews(
            from = from,
            to = to,
            language = language,
            sortBy = sortBy,
            pageSize = pageSize,
            page = page
        )
    }

    override fun getSources(): Call<ArrayList<Source>> {
        return newsServiceApi.getSources()
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
}