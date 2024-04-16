package com.news.app.data.model.network_reponses

import com.google.gson.annotations.SerializedName
import com.news.app.domain.model.Source

data class SourcesResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("sources")
    val sourcesList: ArrayList<Source>
)
