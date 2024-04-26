package com.news.app.feature_headlines.domain.use_cases

import com.news.data.data_api.Repository
import com.news.data.data_api.model.Filters

class FilteredNewsUseCase(
    private val repository: Repository
) {

    operator fun invoke(filters: Filters, isInternetEnabled: Boolean) = repository.getFilteredNews(filters, isInternetEnabled)
}