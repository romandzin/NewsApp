package com.news.app.model.data_classes

import com.google.gson.annotations.SerializedName


data class Source(

    @SerializedName("id")
    val id: String?,

    @SerializedName("name")
    val name: String?
)
