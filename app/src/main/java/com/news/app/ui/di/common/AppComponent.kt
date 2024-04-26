package com.news.app.ui.di.common

import android.content.Context
import com.news.core.AppDependenciesProvider
import com.news.core.ApplicationComponent
import com.news.core.ApplicationContextProvider
import com.news.core.NetworkProvider
import dagger.BindsInstance
import dagger.Component

@Component(
    dependencies = [NetworkProvider::class, ApplicationContextProvider::class],
)
interface AppComponent: AppDependenciesProvider {

    companion object {

        fun create(applicationContext: Context): AppComponent {
            val networkProvider = RepositoryComponent.create(applicationContext)
            val applicationContextProvider = ApplicationComponent.create(applicationContext)
            return DaggerAppComponent.factory().create(networkProvider, applicationContextProvider, applicationContext)
        }
    }

    @Component.Factory
    interface Factory {

        fun create(
            networkProvider: NetworkProvider,
            applicationContextProvider: ApplicationContextProvider,
            @BindsInstance applicationContext: Context,
        ): AppComponent
    }
}