package com.news.app.core

import androidx.room.Room
import androidx.room.RoomDatabase
import com.news.app.data.retrofit.ApiNewsService
import com.news.app.domain.Repository
import retrofit2.Retrofit

interface NetworkProvider {

    fun provideRepository(): Repository

}