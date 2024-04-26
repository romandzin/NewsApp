package com.news.app.ui.di.room

import com.news.dat.data_impl.db.CachedDao
import com.news.dat.data_impl.db.DbObject
import com.news.dat.data_impl.db.SavedDao
import dagger.Module
import dagger.Provides

@Module
class RoomModule {

    @Provides
    fun providesSavedDao(savedDatabase: DbObject): SavedDao {
        return savedDatabase.appDatabase.getSavedDao()
    }

    @Provides
    fun provideCachedDao(savedDatabase: DbObject): CachedDao {
        return savedDatabase.appDatabase.getCachedDao()
    }
}