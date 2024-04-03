package com.news.app.ui.presenters

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import com.news.app.data.model.Article
import com.news.app.domain.Repository
import com.news.app.ui.di.common.DaggerRepositoryComponent
import com.news.app.ui.moxy.views.HeadLinesView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import okhttp3.Response
import java.util.Locale
import javax.inject.Inject

@InjectViewState
class HeadlinesPresenter : MvpPresenter<HeadLinesView>() {
    @Inject lateinit var dataRepository: Repository
    private var category = "general"
    private var searchMode = false
    private var page = 1
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

    fun filter(text: String) {
        val filteredlist: ArrayList<Article> = ArrayList()
        for (item in articles) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.newsTitle?.lowercase()?.contains(text.lowercase(Locale.getDefault())) == true || item.source.name?.lowercase()?.contains(text.lowercase(Locale.getDefault())) == true) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredlist.add(item)
            }
        }
        viewState.displayNewsList(filteredlist)
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
        if (!searchMode) {
            ++page
            getHeadlinesNews { response ->
                Log.d("tag", response.articles.toString())
                articles.addAll(response.articles)
                viewState.displayNewsList(articles)
                viewState.hideLoading()
            }
        }
    }

    fun searchModeEnabled() {
        searchMode = true
    }

    fun searchModeDisabled() {
        searchMode = false
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