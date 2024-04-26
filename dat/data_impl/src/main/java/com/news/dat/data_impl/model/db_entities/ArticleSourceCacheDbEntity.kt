package com.news.dat.data_impl.model.db_entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cache_articles_by_source")
data class ArticleSourceCacheDbEntity(

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

    @ColumnInfo("sourceId")
    val sourceId: String?
)
