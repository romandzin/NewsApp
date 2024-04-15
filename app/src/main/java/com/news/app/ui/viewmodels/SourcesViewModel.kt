package com.news.app.ui.viewmodels

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.news.app.core.AppDependenciesProvider
import com.news.app.data.model.Article
import com.news.app.data.model.Source
import com.news.app.domain.Repository
import com.news.app.ui.fragments.ANOTHER_ERROR
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.Locale

class SourcesViewModel: ViewModel() {

    lateinit var dataRepository: Repository
    private var page = 1
    private var pageSize = 8
    private var isShowingArticles = false

    private val _sourcesList = MutableLiveData<ArrayList<Source>>()
    private var listOfSavedSources: ArrayList<Source> = arrayListOf()
    val sourcesList: LiveData<ArrayList<Source>> = _sourcesList

    private val _articlesList = MutableLiveData<ArrayList<Article>>()
    private var listOfSavedArticles: ArrayList<Article> = arrayListOf()
    val articlesList: LiveData<ArrayList<Article>> = _articlesList

    fun init(appDependencies: AppDependenciesProvider) {
        isShowingArticles = false
        dataRepository = appDependencies.provideRepository()
        getSourcesList()
    }

    @SuppressLint("CheckResult")
    fun getSourcesList() {
        dataRepository.getSources()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { sourcesList ->
                _sourcesList.value = sourcesList
                listOfSavedSources.addAll(sourcesList)
                Log.d("tag", sourcesList.toString())
            }
    }

    //TODO добавить кэш

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
    private fun getHeadlinesNewsWithSource(sourceCategory: String, subscribeAction: (com.news.app.data.model.ArticlesResponse) -> Unit) {
        dataRepository.getHeadlinesNewsWithSource(sourceCategory, pageSize, page)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError { e -> e.printStackTrace() }
            .subscribe({ response ->
                subscribeAction(response)
            },
                {

                })
    }

    fun sourceClicked(source: String) {
        isShowingArticles = true
        getHeadlinesNewsWithSource(source) { response ->
            _articlesList.value = response.articles
            listOfSavedArticles.addAll(response.articles)
            Log.d("tag", response.articles.toString())
        }
    }

}