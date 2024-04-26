package com.news.app.feature_headlines.domain.use_cases

import com.news.data.data_api.Repository

class HeadlinesUseCase(
    private val repository: Repository
) {

    operator fun invoke(category: String, page: Int, pageSize: Int) = repository.getHeadlinesNews(category, page, pageSize)
}