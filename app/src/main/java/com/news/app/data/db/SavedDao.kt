package com.news.app.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.news.app.data.model.ArticleDbEntity
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable

@Dao
interface SavedDao {

    @Insert(entity = ArticleDbEntity::class)
    fun insertNewArticle(articleDbEntity: ArticleDbEntity)

    @Query("SELECT * FROM saved_articles")
    fun getAllArticles(): Observable<List<ArticleDbEntity>>

    @Delete
    fun deleteArticle(articleDbEntity: ArticleDbEntity)

}