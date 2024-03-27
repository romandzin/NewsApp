package com.news.app.data.model

import com.google.gson.annotations.SerializedName

data class Response(
    @SerializedName("status")
    val status: String,

    @SerializedName("articles")
    val articles: ArrayList<Article>
)
