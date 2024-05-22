package com.news.app.feature_saved.ui.viewModel

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.news.app.feature_saved.domain.use_cases.SavedListUseCase
import com.news.core.AppDependenciesProvider
import com.news.data.data_api.model.Article
import com.news.data.data_api.model.InAppError
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.Locale

class SavedViewModel : ViewModel() {

    private lateinit var savedListUseCase: SavedListUseCase

    private val _savedList = MutableLiveData<ArrayList<Article>>()
    private var listOfSavedArticles: ArrayList<Article> = arrayListOf()
    val savedList: LiveData<ArrayList<Article>> = _savedList

    private var _errorState = MutableLiveData<InAppError?>()
    val errorState = _errorState

    private var _unshowError = MutableLiveData<Boolean?>()
    val unshowError = _unshowError

    val compositeDisposable = CompositeDisposable()

    fun init(appDependencies: AppDependenciesProvider) {
        savedListUseCase = SavedListUseCase(appDependencies.provideRepository())
        getList()
    }

    private fun getList() {
        val function = { getListWithError() }
        val disposable = savedListUseCase()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ savedList ->
                savedList.removeIf { it == null }
                _savedList.value = savedList as ArrayList<Article>
                listOfSavedArticles.addAll(savedList)
            },
                {
                    it.printStackTrace()
                    val error = InAppError(com.news.core.ANOTHER_ERROR, function)
                    _errorState.value = error
                    _errorState.value = null
                })
        compositeDisposable.add(disposable)
    }

    private fun getListWithError() {
        val disposable = savedListUseCase()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { savedList ->
                postValueAndNotSaveInCache(_unshowError, true)
                savedList.removeIf { it == null }
                _savedList.value = savedList as ArrayList<Article>
                listOfSavedArticles.addAll(savedList)
            }
        compositeDisposable.add(disposable)
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

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}