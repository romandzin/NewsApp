package com.news.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.news.app.data.model.ArticleCacheDbEntity
import com.news.app.data.model.ArticleSavedDbEntity

@Database(
    version = 2,
    entities = [
        ArticleSavedDbEntity::class,
        ArticleCacheDbEntity::class
    ],
)
abstract class SavedDatabase : RoomDatabase() {

    abstract fun getSavedDao(): SavedDao

    abstract fun getCachedDao(): CachedDao

}