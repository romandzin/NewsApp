package com.news.app.data.model.db_entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_articles")
data class ArticleSavedDbEntity(

    @PrimaryKey
    @ColumnInfo("title")
    val newsTitle: String,

    @ColumnInfo("urlToImage")
    val newsIcon: String?,

    @ColumnInfo("publishedAt")
    val publishedAt: String?,

    @ColumnInfo("content")
    val content: String?,

    @ColumnInfo("savedDate")
    val savedDate: String?,

    @ColumnInfo("sourceName")
    val sourceName: String?,

    @ColumnInfo("sourceId")
    val sourceId: String?
)