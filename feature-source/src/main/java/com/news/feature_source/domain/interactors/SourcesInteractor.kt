package com.news.feature_source.domain.interactors

import com.news.feature_source.domain.use_cases.HeadlinesBySourceUseCase
import com.news.feature_source.domain.use_cases.SourcesUseCase
import com.news.data.data_api.Repository

class SourcesInteractor(
    repository: Repository
) {

    val headlinesBySourceUseCase = HeadlinesBySourceUseCase(repository)
    val sourcesUseCase = SourcesUseCase(repository)

}