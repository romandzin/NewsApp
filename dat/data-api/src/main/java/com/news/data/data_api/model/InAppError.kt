package com.news.data.data_api.model

data class InAppError(
    val errorType: Int,
    val errorFunction: () -> Unit
)
