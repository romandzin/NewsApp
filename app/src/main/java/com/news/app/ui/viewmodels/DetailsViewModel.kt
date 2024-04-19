package com.news.app.ui.viewmodels

import android.annotation.SuppressLint
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ClickableSpan
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.news.app.core.AppDependenciesProvider
import com.news.app.domain.Repository
import com.news.app.domain.model.Article
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class DetailsViewModel : ViewModel() {

    private lateinit var dataRepository: Repository
    private var currentArticle: Article? = null
    var saved = MutableStateFlow(false)
    var currentArticleFlow = MutableStateFlow(currentArticle)
    var url = MutableSharedFlow<String>(0, 1, BufferOverflow.DROP_OLDEST)
    var clickableText = MutableStateFlow(SpannableString(""))

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

    fun bundleDataIsGet(article: Article) {
        currentArticle = article
        currentArticleFlow.value = article
        createClickableText(article)
        checkIfElementSaved()
    }

    private fun createClickableText(article: Article) {
        if (article.content != null) {
            val separated = article.content.split("[+")
            val clickable = "[+${separated[1]}"
            val ss = SpannableString(article.content)
            val clickableSpan1: ClickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    viewModelScope.launch {
                        if (article.url == "") url.emit("https://google.com")
                        else url.emit(article.url)
                    }
                }
            }
            ss.setSpan(clickableSpan1, separated[0].length, separated[0].length + clickable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            clickableText.value = ss
        }
    }

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