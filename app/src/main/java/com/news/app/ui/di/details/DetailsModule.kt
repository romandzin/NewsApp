package com.news.app.ui.di.details

import androidx.transition.Visibility.Mode
import com.news.app.ui.viewmodels.DetailsViewModel
import dagger.Module
import dagger.Provides

@Module
class DetailsModule {

    @Provides
    fun providesDetailsViewModel(): DetailsViewModel {
        return DetailsViewModel()
    }
}