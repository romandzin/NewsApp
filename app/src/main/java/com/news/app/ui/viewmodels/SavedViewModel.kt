package com.news.app.ui.viewmodels

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.news.app.core.AppDependenciesProvider
import com.news.app.data.model.Article
import com.news.app.domain.Repository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.Locale

class SavedViewModel: ViewModel() {

    lateinit var dataRepository: Repository

    private val _savedList = MutableLiveData<ArrayList<Article>>()
    private var listOfSavedArticles: ArrayList<Article> = arrayListOf()
    val savedList: LiveData<ArrayList<Article>> = _savedList

    fun init(appDependencies: AppDependenciesProvider) {
        dataRepository = appDependencies.provideRepository()
        getList()
    }

    @SuppressLint("CheckResult")
    fun getList() {
        dataRepository.getSavedList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { savedList ->
                savedList.removeIf { it == null }
                _savedList.value = savedList as ArrayList<Article>
                listOfSavedArticles.addAll(savedList)
                Log.d("tag", savedList.toString())
            }
    }

    fun filter(text: String) {
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
}