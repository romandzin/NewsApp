package com.news.app.ui.viewmodels

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.news.app.core.AppDependenciesProvider
import com.news.app.domain.model.Article
import com.news.app.domain.Repository
import com.news.app.ui.fragments.ANOTHER_ERROR
import com.news.app.ui.model.InAppError
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.Locale

class SavedViewModel : ViewModel() {

    lateinit var dataRepository: Repository

    private val _savedList = MutableLiveData<ArrayList<Article>>()
    private var listOfSavedArticles: ArrayList<Article> = arrayListOf()
    val savedList: LiveData<ArrayList<Article>> = _savedList

    private var _errorState = MutableLiveData<InAppError?>()
    val errorState = _errorState

    private var _unshowError = MutableLiveData<Boolean?>()
    val unshowError = _unshowError

    fun init(appDependencies: AppDependenciesProvider) {
        dataRepository = appDependencies.provideRepository()
        getList()
    }

    @SuppressLint("CheckResult")
    private fun getList() {
        val function = { getListWithError() }
        dataRepository.getSavedList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ savedList ->
                savedList.removeIf { it == null }
                _savedList.value = savedList as ArrayList<Article>
                listOfSavedArticles.addAll(savedList)
            },
                {
                    it.printStackTrace()
                    val error = InAppError(ANOTHER_ERROR, function)
                    _errorState.value = error
                    _errorState.value = null
                })
    }

    @SuppressLint("CheckResult")
    private fun getListWithError() {
        dataRepository.getSavedList()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { savedList ->
                postValueAndNotSaveInCache(_unshowError, true)
                savedList.removeIf { it == null }
                _savedList.value = savedList as ArrayList<Article>
                listOfSavedArticles.addAll(savedList)
            }
    }

    private fun <T> postValueAndNotSaveInCache(liveData: MutableLiveData<T>, value: T) {
        liveData.value = value
        liveData.value = null
    }

    fun gotSearchText(text: String) {
        findArticlesThatContainsText(text)
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
        _savedList.value = filteredList
    }

    fun refreshView() {
        getList()
    }
}