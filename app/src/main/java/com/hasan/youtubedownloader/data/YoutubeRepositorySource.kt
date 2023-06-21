package com.hasan.youtubedownloader.data

import android.content.Context
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.mapper.VideoFormat
import kotlinx.coroutines.flow.Flow

interface YoutubeRepositorySource {

    fun getFormats(link: String): ArrayList<VideoFormat>?

    fun startDownload(link: String, format: String): Flow<Float>

    fun cancelDownload(taskId: String): Boolean

    fun updateYoutubeDL(
        updateChannel: YoutubeDL.UpdateChannel
    ): YoutubeDL.UpdateStatus?

}