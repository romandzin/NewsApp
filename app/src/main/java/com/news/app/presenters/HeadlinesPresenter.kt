package com.news.app.presenters

import android.annotation.SuppressLint
import android.util.Log
import com.news.app.model.data_classes.News
import com.news.app.model.retrofit.ApiNewsService
import com.news.app.model.retrofit.RetrofitObj
import com.news.app.moxy.views.HeadLinesView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import moxy.InjectViewState
import moxy.MvpPresenter

@InjectViewState
class HeadlinesPresenter : MvpPresenter<HeadLinesView>() {
    private val retrofitService: ApiNewsService = RetrofitObj.service

    override fun attachView(view: HeadLinesView?) {
        super.attachView(view)
    }

    @SuppressLint("CheckResult")
    fun viewShowed() {
        retrofitService.getHeadlinesNews("business")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { response ->
                viewState.displayNewsList(response.articles)
            }
    }

}