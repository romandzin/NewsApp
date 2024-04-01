package com.news.app.ui.presenters

import android.annotation.SuppressLint
import android.util.Log
import com.news.app.data.model.Article
import com.news.app.domain.Repository
import com.news.app.ui.di.common.DaggerRepositoryComponent
import com.news.app.ui.moxy.views.HeadLinesView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import okhttp3.Response
import javax.inject.Inject

@InjectViewState
class HeadlinesPresenter : MvpPresenter<HeadLinesView>() {
    @Inject lateinit var dataRepository: Repository
    private var category = "general"
    private var page = 0
    private var pageSize = 8
    private var articles = arrayListOf<Article>()

    override fun attachView(view: HeadLinesView?) {
        DaggerRepositoryComponent
            .builder()
            .build()
            .inject(this)
        super.attachView(view)
    }

    fun refreshView() {
        when (category) {
            "general" -> viewState.setSelectedTab(0)
            "business" -> viewState.setSelectedTab(1)
            "technology" -> viewState.setSelectedTab(2)
        }
    }

    fun tabSelected(selectedCategory: String) {
        viewState.showLoading()
        category = selectedCategory
        getHeadlinesNews { response ->
            viewState.displayNewsList(response.articles)
            articles = response.articles
            viewState.hideLoading()
        }
    }

    fun scrolledToEnd() {
        page++
        getHeadlinesNews { response ->
            Log.d("tag", response.articles.toString())
            articles.addAll(response.articles)
            viewState.displayNewsList(articles)
            viewState.hideLoading()
        }
    }

    @SuppressLint("CheckResult")
    private fun getHeadlinesNews(subscribeAction: (com.news.app.data.model.Response) -> Unit) {
        dataRepository.getHeadlinesNews(category, pageSize, page)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError { e -> e.printStackTrace() }
            .subscribe { response ->
                subscribeAction(response)
            }
    }

    @SuppressLint("CheckResult")
    fun getList() {
        dataRepository.getSavedList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { savedList ->
                viewState.showError(savedList.toString())
            }
    }

}