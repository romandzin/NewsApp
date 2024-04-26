package com.news.app.ui.di.common

import android.content.Context
import com.news.core.NetworkProvider
import com.news.app.ui.di.retrofit.RetrofitModule
import com.news.app.ui.di.room.RoomModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [RetrofitModule::class, RepositoryModule::class, RoomModule::class])
interface RepositoryComponent: NetworkProvider {

    companion object {
        fun create(applicationContext: Context): RepositoryComponent {
            return DaggerRepositoryComponent.factory().create(applicationContext)
        }
    }

    @Component.Factory
    interface Factory {

        fun create(
            @BindsInstance applicationContext: Context
        ): RepositoryComponent
    }

}