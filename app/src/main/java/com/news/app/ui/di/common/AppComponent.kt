package com.news.app.ui.di.common

import android.content.Context
import com.news.app.core.AppDependenciesProvider
import com.news.app.core.NetworkProvider
import dagger.BindsInstance
import dagger.Component

@Component(
    dependencies = [NetworkProvider::class],
)
interface AppComponent: AppDependenciesProvider {

    companion object {

        fun create(applicationContext: Context): AppComponent {
            val networkProvider = RepositoryComponent.create(applicationContext)
            return DaggerAppComponent.factory().create(networkProvider, applicationContext)
        }
    }

    @Component.Factory
    interface Factory {

        fun create(
            networkProvider: NetworkProvider,
            @BindsInstance applicationContext: Context,
        ): AppComponent
    }
}