package com.news.feature_source.domain.use_cases

import com.news.data.data_api.Repository

class SourcesUseCase(
    private val repository: Repository
) {

    operator fun invoke() = repository.getSources()
}