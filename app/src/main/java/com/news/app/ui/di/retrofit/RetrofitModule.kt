package com.news.app.ui.di.retrofit

import com.news.app.data.retrofit.ApiNewsService
import com.news.app.data.retrofit.RetrofitObj
import dagger.Module
import dagger.Provides

@Module
class RetrofitModule {

    @Provides
    fun providesRetrofitService(): ApiNewsService {
        return RetrofitObj.service
    }
}