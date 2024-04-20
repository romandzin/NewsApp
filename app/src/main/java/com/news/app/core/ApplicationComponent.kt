package com.news.app.core

import android.content.Context
import com.news.app.ui.di.common.DaggerRepositoryComponent
import com.news.app.ui.di.common.RepositoryComponent
import dagger.BindsInstance
import dagger.Component
import javax.inject.Named

@Component
interface ApplicationComponent: ApplicationContextProvider {

    companion object {
        fun create(@Named("application.context") applicationContext: Context): ApplicationComponent {
            return DaggerApplicationComponent.factory().create(applicationContext)
        }
    }

    @Component.Factory
    interface Factory {

        fun create(
            @Named("application.context") @BindsInstance applicationContext: Context
        ): ApplicationComponent
    }

}