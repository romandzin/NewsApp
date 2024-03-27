package com.news.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.news.app.data.model.ArticleDbEntity

@Database(
    version = 1,
    entities = [
        ArticleDbEntity::class
    ]
)
abstract class SavedDatabase : RoomDatabase() {

    abstract fun getSavedDao(): SavedDao

}