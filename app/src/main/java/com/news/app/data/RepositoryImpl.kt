package com.news.app.data

import android.content.Context
import com.news.app.data.db.DbObject
import com.news.app.data.db.SavedDao
import com.news.app.data.model.Response
import com.news.app.data.model.Source
import com.news.app.data.retrofit.RetrofitObj
import com.news.app.domain.Repository
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call

class RepositoryImpl : Repository {
    private val newsServiceApi = RetrofitObj.service
    private lateinit var savedDao: SavedDao

    override fun getHeadlinesNews(category: String, apiKey: String): Observable<Response> {
        return newsServiceApi.getHeadlinesNews(category, apiKey)
    }

    override fun getSources(): Call<ArrayList<Source>> {
        return newsServiceApi.getSources()
    }

}