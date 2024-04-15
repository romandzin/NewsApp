package com.news.app.ui.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.news.app.R
import com.news.app.ui.fragments.ANOTHER_ERROR
import com.news.app.ui.fragments.NO_INTERNET_ERROR

class ErrorViewModel: ViewModel() {
    private val _errorTextLiveData = MutableLiveData<String>()
    var errorTextLiveData: LiveData<String> = _errorTextLiveData

    fun viewInit(errorType: Int) {
        when (errorType) {
            NO_INTERNET_ERROR -> {
                _errorTextLiveData.value = "No internet connection"
            }
            ANOTHER_ERROR -> {
                _errorTextLiveData.value = "Something went wrong \n" +
                        "Try later"
            }
        }
    }

    fun refreshButtonClicked(lastFunction: (() -> Unit)?) {
        if (lastFunction != null) {
            lastFunction()
        }
    }
}