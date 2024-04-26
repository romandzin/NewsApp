package com.news.dat.data_impl.model.db_entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Entity(tableName = "cached_source")
data class SourceDbEntity(

    @PrimaryKey
    @ColumnInfo("id")
    val id: String,

    @ColumnInfo("name")
    val name: String?,

    @ColumnInfo("url")
    val url: String? = "",

    @ColumnInfo("category")
    val category: String? = "",

    @ColumnInfo("language")
    val language: String? = "",

    @ColumnInfo("country")
    val country: String? = "",
)
