package com.news.app.feature_headlines.ui.moxy.views

import com.news.data.data_api.model.Article
import moxy.MvpView
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType


@StateStrategyType(value = OneExecutionStateStrategy::class)
interface HeadLinesView: MvpView {

    fun tabSelected(category: String)

    fun refreshView()

    fun setSelectedTab(index: Int)

    fun displayNewsList(newsList: ArrayList<Article>)

    fun showError(errorText: Int, lastAction: () -> Unit)

    fun showLoading()

    fun hideLoading()

    fun setDefaultMode()
    fun removeError()

    fun setSearchModeToFragment()

    fun disableSearchModeInFragment()

}