package com.news.app.model.data_classes

import com.google.gson.annotations.SerializedName

data class Response(
    @SerializedName("status")
    val status: String,

    @SerializedName("articles")
    val articles: ArrayList<News>
)
