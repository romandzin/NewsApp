package com.news.app.ui.presenters

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.news.app.core.AppDependenciesProvider
import com.news.app.domain.Repository
import com.news.app.domain.model.Article
import com.news.app.ui.fragments.ANOTHER_ERROR
import com.news.app.ui.fragments.NO_INTERNET_ERROR
import com.news.app.ui.model.Filters
import com.news.app.ui.moxy.views.HeadLinesView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import java.util.Locale

@InjectViewState
class HeadlinesPresenter : MvpPresenter<HeadLinesView>() {

    private lateinit var dataRepository: Repository
    private var category = "general"
    private var isNeedToPaginate = false
    private var isNeedToRefresh = true
    private var isFiltersEnabled = false
    private var page = 1
    private var pageSize = 8
    private var headlinesArticles = arrayListOf<Article>()
    private var filteredArticles = arrayListOf<Article>()


    fun init(appDependencies: AppDependenciesProvider) {
        dataRepository = appDependencies.provideRepository()
    }

    private fun observerInternetConnection(context: Context): Boolean {
        val result: Boolean
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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
        page = 1
        if (isNeedToRefresh) {
            when (category) {
                "general" -> viewState.setSelectedTab(0)
                "business" -> viewState.setSelectedTab(1)
                "technology" -> viewState.setSelectedTab(2)
            }
        }
    }

    @SuppressLint("CheckResult")
    fun searchInArrayByText(text: String, context: Context) {
        if (observerInternetConnection(context)) {
            dataRepository.getHeadlinesNewsByQuery(category, text)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { newsList ->
                    viewState.displayNewsList(newsList)
                }
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

    fun tabSelected(selectedCategory: String, context: Context) {
        viewState.showLoading()
        page = 1
        category = selectedCategory
        getHeadlinesNews(context) { articlesList ->
            if (isNeedToRefresh) {
                isFiltersEnabled = false
                viewState.setDefaultMode()
                viewState.displayNewsList(articlesList)
                if (articlesList.size > 8) page = articlesList.size / 8
                headlinesArticles = articlesList
                viewState.hideLoading()
            }
        }
    }

    fun scrolledToEnd(context: Context) {
        if (isNeedToPaginate) {
            ++page
            getHeadlinesNews(context) { articlesList ->
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

    @SuppressLint("CheckResult")
    private fun getHeadlinesNewsWithErrorScreen(subscribeAction: (ArrayList<Article>) -> Unit) {
        dataRepository.getHeadlinesNews(category, pageSize, page)
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
    private fun getHeadlinesNews(context: Context, subscribeAction: (ArrayList<Article>) -> Unit) {
        val function = { getHeadlinesNewsWithErrorScreen(subscribeAction) }
        dataRepository.getHeadlinesNews(category, pageSize, page)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ arrayList ->
                subscribeAction(arrayList)
            },
                {
                    showError(context, it, function)
                })
    }

    private fun showError(
        context: Context,
        it: Throwable,
        function: () -> Unit
    ) {
        if (observerInternetConnection(context)) {
            it.printStackTrace()
            viewState.showError(ANOTHER_ERROR, function)
        } else viewState.showError(NO_INTERNET_ERROR, function)
    }

    @SuppressLint("CheckResult")
    private fun getFilteredNews(
        filters: Filters,
        context: Context,
        subscribeAction: (ArrayList<Article>) -> Unit
    ) {
        isFiltersEnabled = true
        val function = { getFilteredNewsWithError(filters, subscribeAction) }
        dataRepository.getFilteredNews(
            filters,
            pageSize,
            page
        )
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError { e -> e.printStackTrace() }
            .subscribe({ articleList ->
                subscribeAction(articleList)
            },
                {
                    showError(context, it, function)
                })
    }

    @SuppressLint("CheckResult")
    private fun getFilteredNewsWithError(
        filters: Filters,
        subscribeAction: (ArrayList<Article>) -> Unit
    ) {
        dataRepository.getFilteredNews(
            filters,
            pageSize,
            page
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

    @SuppressLint("CheckResult")
    private fun getFilteredNewsFromCache(
        filters: Filters,
        subscribeAction: (ArrayList<Article>) -> Unit
    ) {
        isFiltersEnabled = true
        val function = { getFilteredNewsFromCacheForErrorScreen(filters, subscribeAction) }
        dataRepository.getFilteredNewsInCache(
            filters
        )
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError { e -> e.printStackTrace() }
            .subscribe({ articleList ->
                subscribeAction(articleList)
            },
                {
                    viewState.showError(ANOTHER_ERROR, function)
                })
    }

    @SuppressLint("CheckResult")
    private fun getFilteredNewsFromCacheForErrorScreen(
        filters: Filters,
        subscribeAction: (ArrayList<Article>) -> Unit
    ) {
        dataRepository.getFilteredNewsInCache(
            filters
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

    fun enableFilters(filters: Filters, context: Context) {
        isFiltersEnabled = true
        if (!observerInternetConnection(context)) {
            getFilteredNewsFromCache(filters) { articleArrayList ->
                filteredArticles.addAll(articleArrayList)
                viewState.displayNewsList(articleArrayList)
                viewState.hideLoading()
            }
        } else {
            getFilteredNews(filters, context) { articleArrayList ->
                filteredArticles.addAll(articleArrayList)
                viewState.displayNewsList(articleArrayList)
                viewState.hideLoading()
            }
        }
    }

    fun searchEnabledResultGet() {
        viewState.setSearchModeToFragment()
    }

    fun searchDisabledResultGet() {
        viewState.disableSearchModeInFragment()
    }

}