package com.news.app.common

import android.os.Build
import android.os.Bundle
import android.os.Parcelable

object Extensions {

    fun <T: Parcelable?> Bundle.getParcelableCompat(key: String, clazz: Class<T>): T {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getParcelable(key, clazz)!!
        } else getParcelable(key)!!
    }
}