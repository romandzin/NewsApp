package com.news.app.feature_headlines.ui.mvi

import com.news.data.data_api.model.Filters

sealed class FiltersSideEffect {
    data class ApplyFilters(val filters: Filters) : FiltersSideEffect()
}