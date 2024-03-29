package com.news.app.ui.di.common

import android.app.Application
import android.content.Context
import com.news.app.ui.di.retrofit.RetrofitModule
import com.news.app.ui.di.room.RoomModule
import com.news.app.ui.presenters.HeadlinesPresenter
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [RetrofitModule::class, RepositoryModule::class, RoomModule::class])
interface RepositoryComponent {

    fun inject(headlinesPresenter: HeadlinesPresenter)

}