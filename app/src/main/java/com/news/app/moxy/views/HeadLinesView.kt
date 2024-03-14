package com.news.app.moxy.views

import com.news.app.model.data_classes.News
import moxy.MvpView
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType


@StateStrategyType(value = OneExecutionStateStrategy::class)
interface HeadLinesView: MvpView {

    fun viewShowed()

    fun displayNewsList(newsList: ArrayList<News>)

    fun showError()

}