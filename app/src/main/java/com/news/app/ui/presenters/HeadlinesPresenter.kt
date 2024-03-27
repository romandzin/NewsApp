package com.news.app.ui.presenters

import android.annotation.SuppressLint
import com.news.app.data.RepositoryImpl
import com.news.app.domain.Repository
import com.news.app.ui.moxy.views.HeadLinesView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter

@InjectViewState
class HeadlinesPresenter : MvpPresenter<HeadLinesView>() {
    private val dataRepository: Repository = RepositoryImpl()
    private var category = "general"

    override fun attachView(view: HeadLinesView?) {
        super.attachView(view)
    }

    fun initView() {
        when (category) {
            "general" -> viewState.setSelectedTab(0)
            "business" -> viewState.setSelectedTab(1)
            "technology" -> viewState.setSelectedTab(2)
        }
    }

    @SuppressLint("CheckResult")
    fun tabSelected(selectedCategory: String) {
        category = selectedCategory
        dataRepository.getHeadlinesNews(selectedCategory)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { response ->
                viewState.displayNewsList(response.articles)
            }
    }

}