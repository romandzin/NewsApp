package com.news.app.ui.di.error

import com.news.app.ui.viewmodels.DetailsViewModel
import com.news.app.ui.viewmodels.ErrorViewModel
import dagger.Module
import dagger.Provides

@Module
class ErrorModule {

    @Provides
    fun providesErrorViewModel(): ErrorViewModel {
        return ErrorViewModel()
    }
}