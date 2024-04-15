package com.news.app.ui.model

data class InAppError(
    val errorType: Int,
    val errorFunction: () -> Unit
)
