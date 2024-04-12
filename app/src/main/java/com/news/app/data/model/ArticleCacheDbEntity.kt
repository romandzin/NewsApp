package com.news.app.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cache_articles")
data class ArticleCacheDbEntity(

    @PrimaryKey
    @ColumnInfo("title")
    val newsTitle: String,

    @ColumnInfo("urlToImage")
    val newsIcon: String?,

    @ColumnInfo("publishedAt")
    val publishedAt: String?,

    @ColumnInfo("content")
    val content: String?,

    @ColumnInfo("sourceName")
    val sourceName: String?,

    @ColumnInfo("page")
    val page: Int,

    @ColumnInfo("pag")
    val pag: Int = 1

)
