package com.news.core

import com.news.data.data_api.model.Article

const val NO_INTERNET_ERROR = 0
const val ANOTHER_ERROR = 1

interface MainAppNavigator {
    fun moveToDetailsFragment(article: Article, nameTag: String)

    fun showError(errorType: Int, lastFunctionBeforeError: () -> Unit)

    fun goBack()

    fun sourcesShowingArticles(source: String)

    fun removeError()
}