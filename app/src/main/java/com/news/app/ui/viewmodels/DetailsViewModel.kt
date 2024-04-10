package com.news.app.ui.viewmodels

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.news.app.core.AppDependenciesProvider
import com.news.app.data.model.Article
import com.news.app.domain.Repository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class DetailsViewModel : ViewModel() {

    private lateinit var dataRepository: Repository
    private var currentArticle: Article? = null
    var saved = MutableStateFlow(false)


    fun bookmarkButtonClicked() {
        if (!saved.value) saveArticle()
        else deleteArticle()
    }

    private fun deleteArticle() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                dataRepository.deleteArticle(requireNotNull(currentArticle))
                saved.value = false
            }
        }
    }

    private fun saveArticle() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                dataRepository.saveArticle(requireNotNull(currentArticle), getCurrentDate())
                saved.value = true
            }
        }
    }

    fun init(appDependencies: AppDependenciesProvider) {
        dataRepository = appDependencies.provideRepository()
    }

    fun uiUpdated(article: Article) {
        currentArticle = article
        checkIfElementSaved()
    }

    //TODO Получать список обьектов и проверять если обьект сохранен

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        return sdf.format(Calendar.getInstance().time)
    }

    @SuppressLint("CheckResult")
    fun checkIfElementSaved() {
        dataRepository.getSavedList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { savedList ->
                savedList.removeIf { it == null }
                savedList.forEach { article ->
                    if (article?.newsTitle == currentArticle?.newsTitle) saved.value = true
                }
            }
    }
}