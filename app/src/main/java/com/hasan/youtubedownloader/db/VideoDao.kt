package com.hasan.youtubedownloader.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.hasan.youtubedownloader.utils.Constants.VIDEO_TABLE

@Dao
interface VideoDao {

    @Insert
    fun insertVideo(video: Video)

    @Delete
    fun deleteVideo(video: Video)

    @Query("SELECT * FROM $VIDEO_TABLE")
    fun getAll(): List<Video>

    @Query("SELECT * FROM $VIDEO_TABLE WHERE fulltitle LIKE :title")
    fun findByTitle(title: String): Video

}