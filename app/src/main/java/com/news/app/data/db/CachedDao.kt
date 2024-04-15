package com.news.app.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.IGNORE
import androidx.room.Query
import com.news.app.data.model.ArticleCacheDbEntity
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable

@Dao
interface CachedDao {

    @Insert(entity = ArticleCacheDbEntity::class, onConflict = IGNORE)
    fun insertCacheArticle(articleCacheDbEntity: ArticleCacheDbEntity)

    @Query("SELECT * from cache_articles WHERE category = (:category)")
    fun getCachedArticlesByCategory(category: String): Observable<List<ArticleCacheDbEntity>>

    @Query("DELETE from cache_articles")
    fun deleteAll()
}