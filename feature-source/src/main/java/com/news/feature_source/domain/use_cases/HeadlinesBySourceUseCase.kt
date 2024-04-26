package com.news.feature_source.domain.use_cases

import android.util.Size
import com.news.data.data_api.Repository

class HeadlinesBySourceUseCase(
    private val repository: Repository
) {

    operator fun invoke(source: String, pageSize: Int, page: Int) = repository.getHeadlinesNewsWithSource(source, pageSize, page)
}