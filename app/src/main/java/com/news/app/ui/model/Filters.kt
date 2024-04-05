package com.news.app.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Filters(
    var sortByParam: String = "",
    var dateFrom: String = "",
    var dateTo: String = "",
    var language: String = ""
) : Parcelable
