package com.news.app.data.db

import android.app.Application
import android.content.Context
import androidx.room.Room
import javax.inject.Inject

class DbObject @Inject constructor(
    var applicationContext: Context
) {

    val appDatabase: SavedDatabase by lazy {
        Room.databaseBuilder(applicationContext, SavedDatabase::class.java, "saved_articles")
            .build()
    }
}