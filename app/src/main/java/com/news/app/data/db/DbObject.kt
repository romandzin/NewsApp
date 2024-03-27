package com.news.app.data.db

import android.content.Context
import androidx.room.Room

object DbObject {

    private lateinit var applicationContext: Context

    fun init(context: Context) {
        applicationContext = context
    }

    val appDatabase: SavedDatabase by lazy {
        Room.databaseBuilder(applicationContext, SavedDatabase::class.java, "saved_articles")
            .createFromAsset("room_article.db")
            .build()
    }
}