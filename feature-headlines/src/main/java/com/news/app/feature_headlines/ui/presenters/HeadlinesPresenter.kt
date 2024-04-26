package com.news.app.feature_headlines.ui.presenters

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.news.app.feature_headlines.domain.interactors.HeadlinesInteractor
import com.news.app.feature_headlines.ui.moxy.views.HeadLinesView
import com.news.core.ANOTHER_ERROR
import com.news.core.AppDependenciesProvider
import com.news.core.NO_INTERNET_ERROR
import com.news.data.data_api.model.Article
import com.news.data.data_api.model.Filters
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import java.util.Locale

@InjectViewState
class HeadlinesPresenter : MvpPresenter<HeadLinesView>() {

    private lateinit var headlinesInteractor: HeadlinesInteractor
    private lateinit var currentAppDependencies: AppDependenciesProvider
    private var category = "general"
    private var isNeedToPaginate = false
    private var isNeedToRefresh = true
    private var isFiltersEnabled = false
    private var page = 1
    private var pageSize = 8
    private var headlinesArticles = arrayListOf<Article>()
    private var filteredArticles = arrayListOf<Article>()


    fun init(appDependencies: AppDependenciesProvider) {
        headlinesInteractor = HeadlinesInteractor(appDependencies.provideRepository())
        currentAppDependencies = appDependencies
    }

    private fun observerInternetConnection(): Boolean {
        val result: Boolean
        val connectivityManager =
            currentAppDependencies.provideApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        result = when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
        return result
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

    @SuppressLint("CheckResult")
    fun searchInArrayByText(text: String) {
        if (observerInternetConnection()) {
            headlinesInteractor.headlinesByQueryUseCase(category, text)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ newsList ->
                    viewState.displayNewsList(newsList)
                    viewState.removeError()
                }, {
                    viewState.displayNewsList(arrayListOf())
                })
        } else {
            searchInCacheByText(text)
        }
    }

    private fun searchInCacheByText(text: String) {
        val filteredList: ArrayList<Article> = ArrayList()
        val arrayListToSearch: ArrayList<Article> = if (isFiltersEnabled) filteredArticles
        else headlinesArticles
        for (item in arrayListToSearch) {
            if (item.newsTitle?.lowercase()
                    ?.contains(text.lowercase(Locale.getDefault())) == true || item.source.name?.lowercase()
                    ?.contains(text.lowercase(Locale.getDefault())) == true
            ) {
                filteredList.add(item)
            }
        }
        viewState.displayNewsList(filteredList)
    }

    fun tabSelected(selectedCategory: String) {
        viewState.showLoading()
        if (category != selectedCategory || page == 1) {
            page = 1
            headlinesArticles = arrayListOf()
            category = selectedCategory
            getHeadlinesNews { articlesList ->
                if (isNeedToRefresh) {
                    isFiltersEnabled = false
                    viewState.setDefaultMode()
                    headlinesArticles.addAll(articlesList)
                    headlinesArticles.distinct()
                    if (articlesList.size > 8) page = headlinesArticles.size / 8
                    viewState.displayNewsList(headlinesArticles)
                    viewState.hideLoading()
                }
            }
        } else {
            getAllCachedArticlesByPage { articlesList ->
                if (isNeedToRefresh) {
                    headlinesArticles.clear()
                    isFiltersEnabled = false
                    viewState.setDefaultMode()
                    headlinesArticles.addAll(articlesList)
                    viewState.displayNewsList(headlinesArticles)
                    viewState.hideLoading()
                }
            }

        }

    }

    fun scrolledToEnd() {
        if (isNeedToPaginate) {
            page++
            getHeadlinesNews { articlesList ->
                headlinesArticles.addAll(articlesList)
                viewState.displayNewsList(headlinesArticles)
                viewState.hideLoading()
            }
        }
    }

    fun anotherModeEnabled() {
        isNeedToPaginate = false
        isNeedToRefresh = false
    }

    fun defaultModeIsSet() {
        isNeedToPaginate = true
        isNeedToRefresh = true
    }

    fun enableFilters(filters: Filters) {
        viewState.showLoading()
        isFiltersEnabled = true
        val isInternetEnabled = observerInternetConnection()
        getFilteredNews(filters, isInternetEnabled) { articleArrayList ->
            filteredArticles.addAll(articleArrayList)
            viewState.displayNewsList(articleArrayList)
            viewState.hideLoading()
        }
    }

    @SuppressLint("CheckResult")
    private fun getHeadlinesNewsWithErrorScreen(subscribeAction: (ArrayList<Article>) -> Unit) {
        headlinesInteractor.headlinesUseCase(category, pageSize, page)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ arrayList ->
                subscribeAction(arrayList)
                this.viewState.removeError()
            },
                {
                    it.printStackTrace()
                })
    }

    @SuppressLint("CheckResult")
    private fun getHeadlinesNews(subscribeAction: (ArrayList<Article>) -> Unit) {
        val function = { getHeadlinesNewsWithErrorScreen(subscribeAction) }
        headlinesInteractor.headlinesUseCase(category, pageSize, page)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ arrayList ->
                subscribeAction(arrayList)
            },
                {
                    showError(it, function)
                })
    }

    @SuppressLint("CheckResult")
    private fun getAllCachedArticlesByPage(subscribeAction: (ArrayList<Article>) -> Unit) {
        val function = { getAllCachedArticlesByPageForErrorScreen(subscribeAction) }
        headlinesInteractor.allCachedHeadlinesUseCase(category, page)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ arrayList ->
                subscribeAction(arrayList)
            },
                {
                    showError(it, function)
                })
    }

    @SuppressLint("CheckResult")
    private fun getAllCachedArticlesByPageForErrorScreen(subscribeAction: (ArrayList<Article>) -> Unit) {
        headlinesInteractor.allCachedHeadlinesUseCase(category, page)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ arrayList ->
                subscribeAction(arrayList)
                this.viewState.removeError()
            },
                {
                    it.printStackTrace()
                })
    }

    private fun showError(
        it: Throwable,
        function: () -> Unit
    ) {
        if (observerInternetConnection()) {
            it.printStackTrace()
            viewState.showError(ANOTHER_ERROR, function)
        } else viewState.showError(NO_INTERNET_ERROR, function)
    }

    @SuppressLint("CheckResult")
    private fun getFilteredNews(
        filters: Filters,
        isInternetEnabled: Boolean,
        subscribeAction: (ArrayList<Article>) -> Unit
    ) {
        isFiltersEnabled = true
        val function = { getFilteredNewsWithError(filters, isInternetEnabled, subscribeAction) }
        headlinesInteractor.filteredNewsUseCase(
            filters,
            isInternetEnabled
        )
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError { e -> e.printStackTrace() }
            .subscribe({ articleList ->
                subscribeAction(articleList)
            },
                {
                    showError(it, function)
                })
    }

    @SuppressLint("CheckResult")
    private fun getFilteredNewsWithError(
        filters: Filters,
        isInternetEnabled: Boolean,
        subscribeAction: (ArrayList<Article>) -> Unit
    ) {
        headlinesInteractor.filteredNewsUseCase(
            filters,
            isInternetEnabled
        )
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError { e -> e.printStackTrace() }
            .subscribe({ response ->
                subscribeAction(response)
                this.viewState.removeError()
            },
                {
                    it.printStackTrace()
                })
    }

    fun searchEnabledResultGet() {
        viewState.setSearchModeToFragment()
    }

    fun searchDisabledResultGet() {
        viewState.disableSearchModeInFragment()
    }

}