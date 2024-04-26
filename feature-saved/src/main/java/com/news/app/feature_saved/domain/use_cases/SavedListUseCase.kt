package com.news.app.feature_saved.domain.use_cases

import com.news.data.data_api.Repository

class SavedListUseCase(
    private val repository: Repository
) {

    operator fun invoke() = repository.getSavedList()
}