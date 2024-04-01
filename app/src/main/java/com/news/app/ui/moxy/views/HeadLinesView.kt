package com.news.app.ui.moxy.views

import com.news.app.data.model.Article
import moxy.MvpView
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType


@StateStrategyType(value = OneExecutionStateStrategy::class)
interface HeadLinesView: MvpView {

    fun tabSelected(category: String)

    fun refreshView()

    fun setSelectedTab(index: Int)

    fun displayNewsList(newsList: ArrayList<Article>)

    fun showError(errorText: String)

    fun showLoading()

    fun hideLoading()

}