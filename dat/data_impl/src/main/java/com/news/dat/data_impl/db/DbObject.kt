package com.news.dat.data_impl.db

import android.content.Context
import androidx.room.Room
import javax.inject.Inject

class DbObject @Inject constructor(
    var applicationContext: Context
) {

    val appDatabase: SavedDatabase by lazy {
        Room.databaseBuilder(applicationContext, SavedDatabase::class.java, "app_saved_database")
            .fallbackToDestructiveMigration()
            .build()
    }
}