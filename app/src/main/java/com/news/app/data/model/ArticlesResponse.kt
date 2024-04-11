package com.news.app.data.model

import com.google.gson.annotations.SerializedName

data class ArticlesResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("articles")
    val articles: ArrayList<Article>
)
