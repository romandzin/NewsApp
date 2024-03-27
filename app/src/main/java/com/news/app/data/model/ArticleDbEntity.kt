package com.news.app.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_articles")
data class ArticleDbEntity(
    @PrimaryKey val id: Long,
    @ColumnInfo("title")
    val newsTitle: String?,

    @ColumnInfo("urlToImage")
    val newsIcon: String?,

    @ColumnInfo("publishedAt")
    val publishedAt: String?,

    @ColumnInfo("content")
    val content: String?
)