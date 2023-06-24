package com.hasan.youtubedownloader.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Video::class], version = 1)
//@TypeConverters(VideoTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao() : VideoDao
}