package com.news.app.ui.presenters

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import com.news.app.common.MainApp
import com.news.app.common.NetworkConnectivityObserver
import com.news.app.core.AppDependenciesProvider
import com.news.app.data.model.Article
import com.news.app.data.model.ArticleDbEntity
import com.news.app.domain.Repository
import com.news.app.ui.di.common.DaggerRepositoryComponent
import com.news.app.ui.fragments.ANOTHER_ERROR
import com.news.app.ui.fragments.NO_INTERNET_ERROR
import com.news.app.ui.model.Filters
import com.news.app.ui.moxy.views.HeadLinesView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.onEach
import moxy.InjectViewState
import moxy.MvpPresenter
import java.util.Locale
import javax.inject.Inject

@InjectViewState
class HeadlinesPresenter : MvpPresenter<HeadLinesView>() {

    lateinit var dataRepository: Repository
    //Заинджектить сюда контекст
    private var category = "general"
    private var isNeedToPaginate = false
    private var isNeedToRefresh = true
    private var page = 1
    private var pageSize = 8
    private var articles = arrayListOf<Article>()


    fun init(appDependencies: AppDependenciesProvider) {
        dataRepository = appDependencies.provideRepository()
    }

    fun observeInternetConnection(applicationContext: Context) {
        val connectivityObserver = NetworkConnectivityObserver(applicationContext)
        connectivityObserver.observe().onEach {
            if (!it) //moveToFragment(ErrorFragment.newInstance(NO_INTERNET_ERROR), "errorFragment")
                viewState.showError(NO_INTERNET_ERROR)
        }
    }

    fun refreshView() {
        if (isNeedToRefresh) {
            when (category) {
                "general" -> viewState.setSelectedTab(0)
                "business" -> viewState.setSelectedTab(1)
                "technology" -> viewState.setSelectedTab(2)
            }
        }
    }

    fun filter(text: String) {
        val filteredlist: ArrayList<Article> = ArrayList()
        for (item in articles) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.newsTitle?.lowercase()
                    ?.contains(text.lowercase(Locale.getDefault())) == true || item.source.name?.lowercase()
                    ?.contains(text.lowercase(Locale.getDefault())) == true
            ) {
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
            if (isNeedToRefresh) {
                viewState.setDefaultMode()
                viewState.displayNewsList(response.articles)
                articles = response.articles
                viewState.hideLoading()
            }
        }
    }

    fun scrolledToEnd() {
        if (isNeedToPaginate) {
            ++page
            getHeadlinesNews { response ->
                articles.addAll(response.articles)
                viewState.displayNewsList(articles)
                viewState.hideLoading()
            }
        }
    }

    fun searchModeEnabled() {
        isNeedToPaginate = false
        isNeedToRefresh = false
    }

    fun searchModeDisabled() {
        isNeedToPaginate = true
        isNeedToRefresh = true
    }

    @SuppressLint("CheckResult")
    private fun getHeadlinesNews(subscribeAction: (com.news.app.data.model.Response) -> Unit) {
        dataRepository.getHeadlinesNews(category, pageSize, page)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError { e -> e.printStackTrace() }
            .subscribe({ response ->
                subscribeAction(response)
            },
                {
                viewState.showError(ANOTHER_ERROR)
                })
    }

    @SuppressLint("CheckResult")
    private fun getFilteredNews(
        filters: Filters,
        subscribeAction: (com.news.app.data.model.Response) -> Unit
    ) {
        dataRepository.getFilteredNews(
            filters.dateFrom,
            filters.dateTo,
            filters.language,
            filters.sortByParam,
            pageSize,
            page
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError { e -> e.printStackTrace() }
            .subscribe({ response ->
                response.status
                subscribeAction(response)
            },
                {
                    viewState.showError(ANOTHER_ERROR)
                })
    }

    fun enableFilters(filters: Filters) {
        getFilteredNews(filters) { response ->
            viewState.displayNewsList(response.articles)
            viewState.hideLoading()
        }
    }

}