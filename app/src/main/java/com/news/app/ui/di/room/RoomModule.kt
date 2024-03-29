package com.news.app.ui.di.room

import com.news.app.data.db.DbObject
import com.news.app.data.db.SavedDao
import com.news.app.data.db.SavedDatabase
import com.news.app.data.retrofit.ApiNewsService
import com.news.app.data.retrofit.RetrofitObj
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
class RoomModule {

    @Provides
    fun providesRoomObject(savedDatabase: DbObject): SavedDao {
        return savedDatabase.appDatabase.getSavedDao()
    }
}