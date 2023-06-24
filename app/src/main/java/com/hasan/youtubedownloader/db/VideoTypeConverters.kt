package com.hasan.youtubedownloader.db

import androidx.room.TypeConverter
import java.util.Date

class VideoTypeConverters {
    //use this to convert non-primitive types

    /***
     * example
     *

    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun toDate(millisSinceEpoch: Long?): Date? {
        return millisSinceEpoch?.let {
            Date(it)
        }
    }
    */

}