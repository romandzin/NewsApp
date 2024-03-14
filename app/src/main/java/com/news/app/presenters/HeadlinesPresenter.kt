package com.news.app.presenters

import android.util.Log
import com.news.app.model.data_classes.News
import com.news.app.model.retrofit.ApiNewsService
import com.news.app.model.retrofit.RetrofitObj
import com.news.app.moxy.views.HeadLinesView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import moxy.InjectViewState
import moxy.MvpPresenter

@InjectViewState
class HeadlinesPresenter: MvpPresenter<HeadLinesView>() {
    private val retrofitService: ApiNewsService = RetrofitObj.service

    override fun attachView(view: HeadLinesView?) {
        super.attachView(view)
    }

    suspend fun viewShowed() {
        withContext(Dispatchers.IO) {
            val articles = retrofitService.getHeadlinesNews("business").execute().body()?.articles
            if (articles != null) {
                this@HeadlinesPresenter.viewState.displayNewsList(articles)
            }
        }
    }

}