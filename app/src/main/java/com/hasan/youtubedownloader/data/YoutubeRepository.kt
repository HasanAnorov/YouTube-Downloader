package com.hasan.youtubedownloader.data

import android.content.Context
import com.yausername.youtubedl_android.YoutubeDL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class YoutubeRepository(
    private val youtubeDl: YoutubeDL,
    private val context: Context
) {

    suspend fun updateYoutubeDl() {
        withContext(Dispatchers.IO) {
            youtubeDl.updateYoutubeDL(context, updateChannel = YoutubeDL.UpdateChannel._STABLE)
        }
    }

}