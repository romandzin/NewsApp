package com.news.app.ui.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.news.app.core.AppDependenciesProvider
import com.news.app.domain.Repository
import com.news.app.domain.model.Article
import com.news.app.domain.model.Source
import com.news.app.ui.fragments.ANOTHER_ERROR
import com.news.app.ui.fragments.NO_INTERNET_ERROR
import com.news.app.ui.model.InAppError
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.Locale

class SourcesViewModel : ViewModel() {

    lateinit var dataRepository: Repository
    private var page = 2
    private var pageSize = 8
    private var isShowingArticles = false
    private var lastSource = ""

    private val _sourcesList = MutableLiveData<ArrayList<Source>>()
    val sourcesList: LiveData<ArrayList<Source>> = _sourcesList

    private var _errorState = MutableLiveData<InAppError?>()
    val errorState = _errorState

    private var _unshowError = MutableLiveData<Boolean?>()
    val unshowError = _unshowError

    private val _articlesList = MutableLiveData<ArrayList<Article>?>()
    val articlesList: LiveData<ArrayList<Article>?> = _articlesList

    private var listOfSavedSources: ArrayList<Source> = arrayListOf()
    private var listOfSavedArticles: ArrayList<Article> = arrayListOf()

    fun init(appDependencies: AppDependenciesProvider, context: Context) {
        isShowingArticles = false
        dataRepository = appDependencies.provideRepository()
        getSourcesList(context)
    }

    fun pulledToRefresh(context: Context) {
        if (isShowingArticles) sourceClicked(lastSource, context)
        else getSourcesList(context)
    }

    private fun observeInternetConnection(context: Context): Boolean {
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

    private fun <T> postValueAndNotSaveInCache(liveData: MutableLiveData<T>, value: T) {
        liveData.value = value
        liveData.value = null
    }

    @SuppressLint("CheckResult")
    private fun getSourcesList(context: Context) {
        val function = { getSourcesListForErrorScreen() }
        dataRepository.getSources()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ sourcesList ->
                showSourcesListAndSave(sourcesList)
            },
                {
                    showError(context, function)
                })
    }

    @SuppressLint("CheckResult")
    fun getSourcesListForErrorScreen() {
        dataRepository.getSources()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ sourcesList ->
                postValueAndNotSaveInCache(_unshowError, true)
                showSourcesListAndSave(sourcesList)
            },
                {
                    it.printStackTrace()
                })
    }

    private fun showSourcesListAndSave(sourcesList: ArrayList<Source>) {
        _sourcesList.value = sourcesList
        listOfSavedSources.addAll(sourcesList)
        listOfSavedSources.distinct()
    }

    fun filter(text: String) {
        if (isShowingArticles) {
            findArticlesThatContainsText(text)
        } else {
            findSourcesThatContainsText(text)
        }
    }

    private fun findSourcesThatContainsText(text: String) {
        val filteredList: ArrayList<Source> = ArrayList()
        for (item in listOfSavedSources) {
            if (item.name?.lowercase()
                    ?.contains(text.lowercase(Locale.getDefault())) == true
            ) {
                filteredList.add(item)
            }
        }
        _sourcesList.value = filteredList
    }

    private fun findArticlesThatContainsText(text: String) {
        val filteredList: ArrayList<Article> = ArrayList()
        for (item in listOfSavedArticles) {
            if (item.newsTitle?.lowercase()
                    ?.contains(text.lowercase(Locale.getDefault())) == true || item.source.name?.lowercase()
                    ?.contains(text.lowercase(Locale.getDefault())) == true
            ) {
                filteredList.add(item)
            }
        }
        postValueAndNotSaveInCache(_articlesList, filteredList)
    }

    @SuppressLint("CheckResult")
    private fun getHeadlinesNewsWithSource(
        sourceCategory: String,
        context: Context,
        subscribeAction: (ArrayList<Article>) -> Unit
    ) {
        val function = { getHeadlinesNewsForErrorScreen(sourceCategory, subscribeAction) }
        dataRepository.getHeadlinesNewsWithSource(sourceCategory, pageSize, page)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError { e -> e.printStackTrace() }
            .subscribe({ response ->
                subscribeAction(response)
            },
                {
                    showError(context, function)
                })
    }

    private fun showError(context: Context, function: () -> Unit) {
        if (observeInternetConnection(context)) {
            val error = InAppError(ANOTHER_ERROR, function)
            postValueAndNotSaveInCache(_errorState, error)
        } else {
            val error = InAppError(NO_INTERNET_ERROR, function)
            postValueAndNotSaveInCache(_errorState, error)
        }
    }

    @SuppressLint("CheckResult")
    private fun getHeadlinesNewsForErrorScreen(
        sourceCategory: String,
        subscribeAction: (ArrayList<Article>) -> Unit
    ) {
        dataRepository.getHeadlinesNewsWithSource(sourceCategory, pageSize, page)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                postValueAndNotSaveInCache(_unshowError, true)
                subscribeAction(response)
            },
                {
                    it.printStackTrace()
                })
    }

    fun sourceClicked(source: String, context: Context) {
        isShowingArticles = true
        lastSource = source
        getHeadlinesNewsWithSource(source, context) { articlesList ->
            postValueAndNotSaveInCache(_articlesList, articlesList)
            listOfSavedArticles.addAll(articlesList)
            listOfSavedArticles.distinct()
        }
    }

}