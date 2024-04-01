package com.news.app.ui.di.error

import com.news.app.ui.fragments.ErrorFragment
import com.news.app.ui.fragments.NewsDetailsFragment
import com.news.app.ui.viewmodels.DetailsViewModel
import dagger.Component

@Component(modules = [ErrorModule::class])
interface ErrorComponent {

    fun inject(errorFragment: ErrorFragment)
}