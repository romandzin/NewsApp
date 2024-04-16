package com.news.app.ui.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.news.app.core.AppDependenciesProvider
import com.news.app.domain.model.Article
import com.news.app.domain.model.Source
import com.news.app.data.model.network_reponses.ArticlesResponse
import com.news.app.domain.Repository
import com.news.app.ui.fragments.ANOTHER_ERROR
import com.news.app.ui.fragments.NO_INTERNET_ERROR
import com.news.app.ui.model.InAppError
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.Locale

class SourcesViewModel: ViewModel() {

    lateinit var dataRepository: Repository
    private var page = 2
    private var pageSize = 8
    private var isShowingArticles = false

    private val _sourcesList = MutableLiveData<ArrayList<Source>>()
    private var listOfSavedSources: ArrayList<Source> = arrayListOf()
    val sourcesList: LiveData<ArrayList<Source>> = _sourcesList

    private var _errorState = MutableLiveData<InAppError?>()
    val errorState = _errorState

    private var _unshowError = MutableLiveData<Boolean?>()
    val unshowError = _unshowError

    private val _articlesList = MutableLiveData<ArrayList<Article>>()
    private var listOfSavedArticles: ArrayList<Article> = arrayListOf()
    val articlesList: LiveData<ArrayList<Article>> = _articlesList

    fun init(appDependencies: AppDependenciesProvider, context: Context) {
        isShowingArticles = false
        dataRepository = appDependencies.provideRepository()
        getSourcesList(context)
    }

    private fun observeInternetConnection(context: Context): Boolean {
        var result: Boolean
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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

    @SuppressLint("CheckResult")
    fun getSourcesList(context: Context) {
        val function = {getSourcesListWithError()}
        dataRepository.getSources()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ sourcesList ->
                _sourcesList.value = sourcesList
                listOfSavedSources.addAll(sourcesList)
                listOfSavedSources.distinct()
                Log.d("tag", sourcesList.toString())
            },
                {
                    if (observeInternetConnection(context)) {
                        it.printStackTrace()
                        val error = InAppError(ANOTHER_ERROR, function)
                        _errorState.value = error
                        _errorState.value = null
                    }
                    else {
                        val error = InAppError(NO_INTERNET_ERROR, function)
                        _errorState.value = error
                        _errorState.value = null
                    }
                })
    }

    @SuppressLint("CheckResult")
    fun getSourcesListWithError() {
        dataRepository.getSources()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({ sourcesList ->
                _unshowError.value = true
                _unshowError.value = null
                _sourcesList.value = sourcesList
                listOfSavedSources.addAll(sourcesList)
                listOfSavedSources.distinct()
            },
        {
            it.printStackTrace()
        })
    }

    fun filter(text: String) {
        if (isShowingArticles) {
            val filteredList: ArrayList<Article> = ArrayList()
            for (item in listOfSavedArticles) {
                if (item.newsTitle?.lowercase()
                        ?.contains(text.lowercase(Locale.getDefault())) == true || item.source.name?.lowercase()
                        ?.contains(text.lowercase(Locale.getDefault())) == true
                ) {
                    filteredList.add(item)
                }
            }
            _articlesList.value = filteredList
        }
        else {
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
    }

    @SuppressLint("CheckResult")
    private fun getHeadlinesNewsWithSource(sourceCategory: String, context: Context, subscribeAction: (ArrayList<Article>) -> Unit) {
        val function = { getHeadlinesNewsWithSourceWithError(sourceCategory, subscribeAction)}
        dataRepository.getHeadlinesNewsWithSource(sourceCategory, pageSize, page)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError { e -> e.printStackTrace() }
            .subscribe({ response ->
                subscribeAction(response)
            },
                {
                    if (observeInternetConnection(context)) {
                        it.printStackTrace()
                        val error = InAppError(ANOTHER_ERROR, function)
                        _errorState.value = error
                        _errorState.value = null
                    }
                    else {
                        val error = InAppError(NO_INTERNET_ERROR, function)
                        _errorState.value = error
                        _errorState.value = null
                    }
                })
    }

    @SuppressLint("CheckResult")
    private fun getHeadlinesNewsWithSourceWithError(sourceCategory: String, subscribeAction: (ArrayList<Article>) -> Unit) {
        dataRepository.getHeadlinesNewsWithSource(sourceCategory, pageSize, page)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError { e -> e.printStackTrace() }
            .subscribe({ response ->
                _unshowError.value = true
                _unshowError.value = null
                subscribeAction(response)
            },
                {
                    it.printStackTrace()
                })
    }

    fun sourceClicked(source: String, context: Context) {
        isShowingArticles = true
        getHeadlinesNewsWithSource(source, context) { articlesList ->
            _articlesList.value = articlesList
            listOfSavedArticles.addAll(articlesList)
            listOfSavedArticles.distinct()
        }
    }

}