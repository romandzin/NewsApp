package com.news.app.data.model

import com.google.gson.annotations.SerializedName

data class SourcesResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("sources")
    val sourcesList: ArrayList<Source>
)
