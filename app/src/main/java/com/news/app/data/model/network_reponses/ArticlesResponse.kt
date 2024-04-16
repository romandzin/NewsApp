package com.news.app.data.model.network_reponses

import com.google.gson.annotations.SerializedName
import com.news.app.domain.model.Article

data class ArticlesResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("articles")
    val articles: ArrayList<Article>
)
