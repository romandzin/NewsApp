package com.news.core

import com.news.data.data_api.Repository

interface NetworkProvider {

    fun provideRepository(): Repository

}