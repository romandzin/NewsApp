package com.news.app.model.data_classes

import com.google.gson.annotations.SerializedName

data class News(

    @SerializedName("source")
    val source: Source,

    @SerializedName("title")
    val newsTitle: String?,

    @SerializedName("urlToImage")
    val newsIcon: String?,

    @SerializedName("publishedAt")
    val publishedAt: String?,

    @SerializedName("content")
    val content: String?
)
