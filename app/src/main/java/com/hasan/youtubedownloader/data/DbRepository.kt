package com.hasan.youtubedownloader.data

import com.hasan.youtubedownloader.db.Video
import com.hasan.youtubedownloader.db.VideoDao
import javax.inject.Inject

class DbRepository @Inject constructor(private val dao: VideoDao) {

    fun insertVideo(video: Video) = dao.insertVideo(video)

    fun deleteVideo(video: Video) = dao.deleteVideo(video)

    fun getAll(): List<Video> = dao.getAll()

    fun findByTitle(title: String): Video = dao.findByTitle(title)

}