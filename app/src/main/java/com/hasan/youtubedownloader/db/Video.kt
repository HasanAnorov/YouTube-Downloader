package com.hasan.youtubedownloader.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hasan.youtubedownloader.utils.Constants

@Entity (tableName = Constants.VIDEO_TABLE)
data class Video(
    @PrimaryKey(autoGenerate = true) val uid : Int = 0,
    @ColumnInfo(name = "fulltitle") val fullTitle: String = "",
    @ColumnInfo(name = "formatNote") val formatNote: String = "",
    @ColumnInfo(name = "ext") val ext: String = "",
    @ColumnInfo(name = "thumbnail") val thumbnail: String = "",
    @ColumnInfo(name = "downloaded_path") var downloadedPath: String = ""
)
