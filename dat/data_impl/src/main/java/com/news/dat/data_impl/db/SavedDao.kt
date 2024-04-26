package com.news.dat.data_impl.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.news.dat.data_impl.model.db_entities.ArticleSavedDbEntity
import io.reactivex.rxjava3.core.Flowable

@Dao
interface SavedDao {

    @Insert(entity = ArticleSavedDbEntity::class)
    fun insertNewArticle(articleSavedDbEntity: ArticleSavedDbEntity)

    @Query("SELECT * FROM saved_articles")
    fun getAllArticles(): Flowable<List<ArticleSavedDbEntity>>

    @Query("DELETE from saved_articles WHERE title = (:articleTitle)")
    fun deleteArticleByTitle(articleTitle: String)

}