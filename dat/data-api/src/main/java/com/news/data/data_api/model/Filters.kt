package com.news.data.data_api.model

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class Filters(
    var sortByParam: String = "",
    var dateFrom: String = "",
    var dateTo: String = "",
    var language: String = ""
) : Parcelable {
    @IgnoredOnParcel
    val properties = listOf(::sortByParam, ::dateFrom, ::language)
}
