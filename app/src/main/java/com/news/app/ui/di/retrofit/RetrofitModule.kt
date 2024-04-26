package com.news.app.ui.di.retrofit

import com.news.dat.data_impl.retrofit.ApiNewsService
import com.news.dat.data_impl.retrofit.RetrofitObj
import dagger.Module
import dagger.Provides

@Module
class RetrofitModule {

    @Provides
    fun providesRetrofitService(): ApiNewsService {
        return RetrofitObj.service
    }
}