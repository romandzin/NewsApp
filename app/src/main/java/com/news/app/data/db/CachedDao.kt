package com.news.app.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.IGNORE
import androidx.room.Query
import com.news.app.data.model.db_entities.ArticleCacheDbEntity
import com.news.app.data.model.db_entities.ArticleSourceCacheDbEntity
import com.news.app.data.model.db_entities.SourceDbEntity
import io.reactivex.rxjava3.core.Observable

@Dao
interface CachedDao {

    @Insert(entity = ArticleCacheDbEntity::class, onConflict = IGNORE)
    fun insertCacheArticle(articleCacheDbEntity: ArticleCacheDbEntity)

    @Insert(entity = SourceDbEntity::class, onConflict = IGNORE)
    fun insertSource(source: SourceDbEntity)

    @Insert(entity = ArticleSourceCacheDbEntity::class, onConflict = IGNORE)
    fun insertCacheArticleBySource(articleSourceCacheDbEntity: ArticleSourceCacheDbEntity)

    @Query("SELECT * from cached_source")
    fun getCachedSources(): Observable<List<SourceDbEntity>>

    @Query("SELECT * from cache_articles WHERE category = (:category)")
    fun getCachedArticlesByCategory(category: String): Observable<List<ArticleCacheDbEntity>>

    @Query("SELECT * from cache_articles_by_source WHERE sourceId = (:source)")
    fun getCachedArticlesBySource(source: String): Observable<List<ArticleSourceCacheDbEntity>>

    @Query("DELETE from cache_articles")
    fun deleteAll()
}