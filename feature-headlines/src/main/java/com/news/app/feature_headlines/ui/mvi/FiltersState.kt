package com.news.app.feature_headlines.ui.mvi

data class FiltersState(
    val isInternetEnabled: Boolean = true,
    val isCalendarShowed: Boolean = false,
    val dateFrom: String = "",
    val dateTo: String = "",
    val sortCategory: String = "",
    val language: String = ""
)
