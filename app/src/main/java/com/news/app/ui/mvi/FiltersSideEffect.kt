package com.news.app.ui.mvi

import com.news.app.ui.model.Filters

sealed class FiltersSideEffect {
    data class ApplyFilters(val filters: Filters) : FiltersSideEffect()
}