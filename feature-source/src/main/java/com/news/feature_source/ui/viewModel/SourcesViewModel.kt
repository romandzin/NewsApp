package com.news.feature_source.ui.viewModel

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.news.feature_source.domain.interactors.SourcesInteractor
import com.news.data.data_api.model.Article
import com.news.data.data_api.model.InAppError
import com.news.data.data_api.model.Source
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.util.Locale

class SourcesViewModel : ViewModel() {

    private lateinit var sourcesInteractor: SourcesInteractor
    private lateinit var context: Context
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

    private val compositeDisposable = CompositeDisposable()

    fun init(appDependencies: com.news.core.AppDependenciesProvider) {
        isShowingArticles = false
        sourcesInteractor = SourcesInteractor(appDependencies.provideRepository())
        context = appDependencies.provideApplicationContext()
        getSourcesList()
    }

    fun pulledToRefresh() {
        if (isShowingArticles) sourceClicked(lastSource)
        else getSourcesList()
    }

    private fun observeInternetConnection(): Boolean {
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

    private fun getSourcesList() {
        val function = { getSourcesListForErrorScreen() }
        val disposable = sourcesInteractor.sourcesUseCase()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ sourcesList ->
                showSourcesListAndSave(sourcesList)
            },
                {
                    showError(function)
                })
        compositeDisposable.add(disposable)
    }

    private fun getSourcesListForErrorScreen() {
        val disposable = sourcesInteractor.sourcesUseCase()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ sourcesList ->
                postValueAndNotSaveInCache(_unshowError, true)
                showSourcesListAndSave(sourcesList)
            },
                {
                    it.printStackTrace()
                })
        compositeDisposable.add(disposable)
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

    private fun getHeadlinesNewsWithSource(
        sourceCategory: String,
        subscribeAction: (ArrayList<Article>) -> Unit
    ) {
        val function = { getHeadlinesNewsForErrorScreen(sourceCategory, subscribeAction) }
        val disposable = sourcesInteractor.headlinesBySourceUseCase(sourceCategory, pageSize, page)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError { e -> e.printStackTrace() }
            .subscribe({ articlesArrayList ->
                Log.d("tag", articlesArrayList.toString())
                subscribeAction(articlesArrayList)
            },
                {
                    showError(function)
                })
        compositeDisposable.add(disposable)
    }

    private fun showError(function: () -> Unit) {
        if (observeInternetConnection()) {
            val error = InAppError(com.news.core.ANOTHER_ERROR, function)
            postValueAndNotSaveInCache(_errorState, error)
        } else {
            val error = InAppError(com.news.core.NO_INTERNET_ERROR, function)
            postValueAndNotSaveInCache(_errorState, error)
        }
    }

    private fun getHeadlinesNewsForErrorScreen(
        sourceCategory: String,
        subscribeAction: (ArrayList<Article>) -> Unit
    ) {
        val disposable = sourcesInteractor.headlinesBySourceUseCase(sourceCategory, pageSize, page)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                postValueAndNotSaveInCache(_unshowError, true)
                subscribeAction(response)
            },
                {
                    it.printStackTrace()
                })
        compositeDisposable.add(disposable)
    }

    fun sourceClicked(source: String) {
        isShowingArticles = true
        lastSource = source
        getHeadlinesNewsWithSource(source) { articlesList ->
            postValueAndNotSaveInCache(_articlesList, articlesList)
            listOfSavedArticles.addAll(articlesList)
            listOfSavedArticles.distinct()
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}