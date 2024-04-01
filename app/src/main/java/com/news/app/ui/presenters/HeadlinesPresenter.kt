package com.news.app.ui.presenters

import android.annotation.SuppressLint
import com.news.app.domain.Repository
import com.news.app.ui.di.common.DaggerRepositoryComponent
import com.news.app.ui.moxy.views.HeadLinesView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import javax.inject.Inject

@InjectViewState
class HeadlinesPresenter : MvpPresenter<HeadLinesView>() {
    @Inject lateinit var dataRepository: Repository
    private var category = "general"

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

    @SuppressLint("CheckResult")
    fun tabSelected(selectedCategory: String) {
        viewState.showLoading()
        category = selectedCategory
        dataRepository.getHeadlinesNews(selectedCategory)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { response ->
                viewState.displayNewsList(response.articles)
                viewState.hideLoading()
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