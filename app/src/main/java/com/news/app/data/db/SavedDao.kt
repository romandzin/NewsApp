package com.news.app.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.news.app.data.model.ArticleDbEntity
import io.reactivex.rxjava3.core.Flowable

@Dao
interface SavedDao {

    @Insert(entity = ArticleDbEntity::class)
    fun insertNewArticle(articleDbEntity: ArticleDbEntity)

    @Query("SELECT * FROM saved_articles")
    fun getAllArticles(): Flowable<List<ArticleDbEntity>>

    @Query("DELETE from saved_articles WHERE title = (:articleTitle)")
    fun deleteArticleByTitle(articleTitle: String)

}