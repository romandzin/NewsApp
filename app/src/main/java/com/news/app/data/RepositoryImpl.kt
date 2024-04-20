package com.news.app.data

import android.annotation.SuppressLint
import android.util.Log
import com.news.app.data.db.CachedDao
import com.news.app.data.db.SavedDao
import com.news.app.data.mappers.DatabaseObjectsMapper
import com.news.app.data.model.db_entities.ArticleCacheDbEntity
import com.news.app.domain.model.Article
import com.news.app.data.model.db_entities.ArticleSavedDbEntity
import com.news.app.data.model.network_reponses.ArticlesResponse
import com.news.app.domain.model.Source
import com.news.app.data.retrofit.ApiNewsService
import com.news.app.domain.Repository
import com.news.app.ui.model.Filters
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    var newsServiceApi: ApiNewsService,
    var savedDao: SavedDao,
    var cachedDao: CachedDao,
    var databaseObjectsMapper: DatabaseObjectsMapper
) : Repository {

    private val oldSavedArticles = mutableListOf<ArticleSavedDbEntity>()

    @SuppressLint("CheckResult")
    override fun getHeadlinesNews(
        category: String,
        pageSize: Int,
        page: Int
    ): Observable<ArrayList<Article>> {
        return cachedDao.getCachedArticlesByCategory(category)
            .take(1)
            .flatMap {
                if (it.size >= page * pageSize) {
                    val articlesList: ArrayList<Article> =
                        it.map { databaseObjectsMapper.transform(it) } as ArrayList<Article>
                    return@flatMap Observable.fromArray(articlesList).subscribeOn(Schedulers.io())
                } else {
                    return@flatMap newsServiceApi.getHeadlinesNews(category, pageSize, page)
                        .flatMap { response ->
                            saveToCache(response.articles, category)
                            Observable.fromArray(response.articles).subscribeOn(Schedulers.io())
                        }
                }
            }
    }

    override fun getHeadlinesNewsByQuery(
        category: String,
        query: String
    ): Flowable<ArrayList<Article>> {
        return newsServiceApi.getHeadlinesNewsByQuery(category, query)
            .subscribeOn(Schedulers.io())
            .onBackpressureDrop()
            .flatMap {
                Flowable.fromArray(it.articles).subscribeOn(Schedulers.io())
            }
    }

    override fun getHeadlinesNewsWithSource(
        source: String,
        pageSize: Int,
        page: Int
    ): Observable<ArrayList<Article>> {
        return cachedDao.getCachedArticlesBySource(source)
            .take(1)
            .subscribeOn(Schedulers.io())
            .flatMap { articleBySourceList ->
                if (articleBySourceList.isEmpty()) {
                    return@flatMap newsServiceApi.getHeadlinesNewsWithSource(source, pageSize, page)
                        .flatMap { sourceResponse ->
                            saveArticlesWithSourceToCache(sourceResponse.articles)
                            Observable.fromArray(sourceResponse.articles)
                                .subscribeOn(Schedulers.io())
                        }
                } else {
                    val articlesList: ArrayList<Article> =
                        articleBySourceList.map { databaseObjectsMapper.transform(it) } as ArrayList<Article>
                    return@flatMap Observable.fromArray(articlesList).subscribeOn(Schedulers.io())
                }
            }
    }

    override fun getFilteredNews(
        filters: Filters,
        pageSize: Int,
        page: Int
    ): Observable<ArrayList<Article>> {
        return newsServiceApi.getFilteredNews(
            from = filters.dateFrom,
            to = filters.dateTo,
            language = filters.language,
            sortBy = filters.sortByParam,
            pageSize = pageSize,
            page = page
        )
            .flatMap { response ->
                Observable.fromArray(response.articles).subscribeOn(Schedulers.io())
            }
    }

    override fun getFilteredNewsInCache(filters: Filters): Observable<ArrayList<Article>> {
        return cachedDao.getAllCachedArticles()
            .subscribeOn(Schedulers.io())
            .flatMap { arrayDbArticlesList ->
                filterLocalArticles(filters.dateFrom, filters.dateTo, arrayDbArticlesList)
            }
    }

    private fun filterLocalArticles(
        from: String,
        to: String,
        arrayDbArticlesList: List<ArticleCacheDbEntity>
    ): Observable<ArrayList<Article>> {
        val filteredArticles = arrayListOf<Article>()
        val publishedAtDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.getDefault())
        val filtersDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fromDate = filtersDateFormat.parse(from)
        val toDate = filtersDateFormat.parse(to)
        for (i in arrayDbArticlesList) {
            val articleSavedData = i.publishedAt?.let { publishedAtDateFormat.parse(it) }
            if (isWithinRange(fromDate!!, toDate!!, articleSavedData!!)) filteredArticles.add(
                databaseObjectsMapper.transform(i)
            )
        }
        return Observable.fromArray(filteredArticles).subscribeOn(Schedulers.io())
    }

    private fun isWithinRange(fromDate: Date, toDate: Date, articleDate: Date): Boolean {
        return !(articleDate.before(fromDate) || articleDate.after(toDate))
    }

    @SuppressLint("CheckResult")
    override fun getSources(): Observable<ArrayList<Source>> {
        return cachedDao.getCachedSources()
            .take(1)
            .subscribeOn(Schedulers.io())
            .flatMap { sourceDbList ->
                if (sourceDbList.isEmpty()) {
                    return@flatMap newsServiceApi.getSources()
                        .flatMap { sourceResponse ->
                            saveSourcesToCache(sourceResponse.sourcesList)
                            Observable.fromArray(sourceResponse.sourcesList)
                                .subscribeOn(Schedulers.io())
                        }
                } else {
                    val sourcesList: ArrayList<Source> =
                        sourceDbList.map { databaseObjectsMapper.transformSource(it) } as ArrayList<Source>
                    return@flatMap Observable.fromArray(sourcesList).subscribeOn(Schedulers.io())
                }
            }
    }

    @SuppressLint("CheckResult")
    override fun getSavedList(): Flowable<ArrayList<Article?>> {
        return savedDao.getAllArticles()
            .take(1)
            .subscribeOn(Schedulers.io())
            .flatMap { dbEntityList ->
                val articlesList = dbEntityList.map { dbElement ->
                    if (checkIfDateIsOld(dbElement.savedDate))
                        databaseObjectsMapper.transform(
                            dbElement
                        )
                    else {
                        oldSavedArticles.add(dbElement)
                        null
                    }
                } as java.util.ArrayList
                Flowable.fromArray(articlesList).subscribeOn(Schedulers.io())
            }
            .doOnComplete {
                for (i in oldSavedArticles) {
                    savedDao.deleteArticleByTitle(i.newsTitle)
                }
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
        savedDao.insertNewArticle(databaseObjectsMapper.transform(article, savedDate))
    }

    override suspend fun deleteArticle(article: Article) {
        savedDao.deleteArticleByTitle(article.newsTitle!!)
    }

    override fun saveToCache(articlesList: java.util.ArrayList<Article>, category: String) {
        for (i in articlesList)
            cachedDao.insertCacheArticle(databaseObjectsMapper.transformToCache(i, category))
    }

    private fun saveSourcesToCache(sourcesList: java.util.ArrayList<Source>) {
        for (i in sourcesList)
            cachedDao.insertSource(databaseObjectsMapper.transformSource(i))
    }

    private fun saveArticlesWithSourceToCache(articlesList: java.util.ArrayList<Article>) {
        for (i in articlesList)
            cachedDao.insertCacheArticleBySource(
                databaseObjectsMapper.transformToArticleSourceCache(
                    i
                )
            )
    }
}