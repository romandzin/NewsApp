package com.news.dat.data_impl.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.news.dat.data_impl.model.db_entities.ArticleCacheDbEntity
import com.news.dat.data_impl.model.db_entities.ArticleSavedDbEntity
import com.news.dat.data_impl.model.db_entities.SourceDbEntity
import com.news.dat.data_impl.model.db_entities.ArticleSourceCacheDbEntity

@Database(
    version = 5,
    entities = [
        ArticleSavedDbEntity::class,
        ArticleCacheDbEntity::class,
        SourceDbEntity::class,
        ArticleSourceCacheDbEntity::class
    ],
)
abstract class SavedDatabase : RoomDatabase() {

    abstract fun getSavedDao(): SavedDao

    abstract fun getCachedDao(): CachedDao

}