package com.news.app.feature_headlines.domain.interactors

import com.news.app.feature_headlines.domain.use_cases.AllCachedArticlesUseCase
import com.news.app.feature_headlines.domain.use_cases.FilteredNewsUseCase
import com.news.app.feature_headlines.domain.use_cases.HeadlinesByQueryUseCase
import com.news.app.feature_headlines.domain.use_cases.HeadlinesUseCase
import com.news.data.data_api.Repository

class HeadlinesInteractor(
    repository: Repository
) {

    val headlinesUseCase = HeadlinesUseCase(repository)
    val allCachedHeadlinesUseCase = AllCachedArticlesUseCase(repository)
    val headlinesByQueryUseCase = HeadlinesByQueryUseCase(repository)
    val filteredNewsUseCase = FilteredNewsUseCase(repository)

}