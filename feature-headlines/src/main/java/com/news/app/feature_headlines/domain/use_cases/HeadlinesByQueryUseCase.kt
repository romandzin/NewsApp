package com.news.app.feature_headlines.domain.use_cases

import com.news.data.data_api.Repository

class HeadlinesByQueryUseCase(
    private val repository: Repository
) {

    operator fun invoke(category: String, query: String) = repository.getHeadlinesNewsByQuery(category, query)
}