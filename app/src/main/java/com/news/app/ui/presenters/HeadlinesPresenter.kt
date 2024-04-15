package com.news.app.ui.presenters

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.news.app.core.AppDependenciesProvider
import com.news.app.data.model.Article
import com.news.app.domain.Repository
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

    lateinit var dataRepository: Repository
    private var category = "general"
    private var isNeedToPaginate = false
    private var isNeedToRefresh = true
    private var page = 1
    private var pageSize = 8
    private var articles = arrayListOf<Article>()


    fun init(appDependencies: AppDependenciesProvider) {
        dataRepository = appDependencies.provideRepository()
    }

    private fun observerInternetConnection(context: Context): Boolean {
        var result = false
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            result = when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.run {
                connectivityManager.activeNetworkInfo?.run {
                    result = when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }
                }
            }
        }
        return result
    }

    fun refreshView() {
        page = 1
        if (isNeedToRefresh) {
            when (category) {
                "general" -> viewState.setSelectedTab(0)
                "business" -> viewState.setSelectedTab(1)
                "health" -> viewState.setSelectedTab(2)
            }
        }
    }

    fun filter(text: String) {
        val filteredlist: ArrayList<Article> = ArrayList()
        for (item in articles) {
            if (item.newsTitle?.lowercase()
                    ?.contains(text.lowercase(Locale.getDefault())) == true || item.source.name?.lowercase()
                    ?.contains(text.lowercase(Locale.getDefault())) == true
            ) {
                filteredlist.add(item)
            }
        }
        viewState.displayNewsList(filteredlist)
    }

    fun tabSelected(selectedCategory: String, context: Context) {
        viewState.showLoading()
        page = 1
        category = selectedCategory
        getHeadlinesNews(context) { articlesList ->
            if (isNeedToRefresh) {
                viewState.setDefaultMode()
                viewState.displayNewsList(articlesList)
                if (articlesList.size > 8) page = articlesList.size / 8
                articles = articlesList
                viewState.hideLoading()
            }
        }
    }

    fun scrolledToEnd(context: Context) {
        if (isNeedToPaginate) {
            ++page
            getHeadlinesNews(context) { articlesList ->
                articles.addAll(articlesList)
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
    private fun getHeadlinesNewsWithErrorScreen(subscribeAction: (ArrayList<Article>) -> Unit) {
        dataRepository.getHeadlinesNews(category, pageSize, page)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError { e -> e.printStackTrace() }
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
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError { e -> e.printStackTrace() }
            .subscribe({ arrayList ->
                subscribeAction(arrayList)
            },
                {
                    if (observerInternetConnection(context)) {
                        it.printStackTrace()
                        viewState.showError(ANOTHER_ERROR, function)
                    }
                    else viewState.showError(NO_INTERNET_ERROR, function)
                })
    }

    @SuppressLint("CheckResult")
    private fun getFilteredNews(
        filters: Filters,
        context: Context,
        subscribeAction: (com.news.app.data.model.ArticlesResponse) -> Unit
    ) {
        val function = { getFilteredNewsWithError(filters, subscribeAction) }
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
                subscribeAction(response)
            },
                {
                    if (observerInternetConnection(context)) {
                        it.printStackTrace()
                        viewState.showError(ANOTHER_ERROR, function)
                    }
                    else viewState.showError(NO_INTERNET_ERROR, function)
                })
    }

    @SuppressLint("CheckResult")
    private fun getFilteredNewsWithError(
        filters: Filters,
        subscribeAction: (com.news.app.data.model.ArticlesResponse) -> Unit
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
                subscribeAction(response)
                this.viewState.removeError()
            },
                {
                    it.printStackTrace()
                })
    }

    fun enableFilters(filters: Filters, context: Context) {
        getFilteredNews(filters, context) { response ->
            viewState.displayNewsList(response.articles)
            viewState.hideLoading()
        }
    }

}