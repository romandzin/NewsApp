package com.news.dat.data_impl.model.network_reponses

import com.google.gson.annotations.SerializedName
import com.news.data.data_api.model.Article

data class ArticlesResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("articles")
    val articles: ArrayList<Article>
)
