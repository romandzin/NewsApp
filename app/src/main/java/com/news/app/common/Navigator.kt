package com.news.app.common

import androidx.fragment.app.Fragment

interface Navigator {
    fun moveToDetailsFragment(fragment: Fragment, nameTag: String)

    fun showError(errorType: Int)
}