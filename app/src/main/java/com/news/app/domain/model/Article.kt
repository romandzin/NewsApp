package com.news.app.domain.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Article(

    @SerializedName("source")
    val source: Source,

    @SerializedName("title")
    val newsTitle: String?,

    @SerializedName("urlToImage")
    val newsIcon: String?,

    @SerializedName("publishedAt")
    val publishedAt: String?,

    @SerializedName("content")
    val content: String?,

    @SerializedName("url")
    val url: String = ""
) : Parcelable
