package com.news.app.ui.di.details

import com.news.app.ui.fragments.NewsDetailsFragment
import com.news.app.ui.viewmodels.DetailsViewModel
import dagger.Component

@Component(modules = [DetailsModule::class])
interface DetailsComponent {

    fun inject(newsDetailsFragment: NewsDetailsFragment)

    fun inject(detailsViewModel: DetailsViewModel)
}