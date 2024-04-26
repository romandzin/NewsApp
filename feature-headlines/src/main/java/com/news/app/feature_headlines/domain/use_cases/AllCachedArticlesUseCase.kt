package com.news.app.feature_headlines.domain.use_cases

import com.news.data.data_api.Repository

class AllCachedArticlesUseCase(
    val repository: Repository
) {

    operator fun invoke(category: String, page: Int) = repository.getAllCachedHeadlinesByPage(category, page)

}
