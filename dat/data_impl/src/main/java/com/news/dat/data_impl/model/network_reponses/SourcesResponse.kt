package com.news.dat.data_impl.model.network_reponses

import com.google.gson.annotations.SerializedName
import com.news.data.data_api.model.Source

data class SourcesResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("sources")
    val sourcesList: ArrayList<Source>
)
