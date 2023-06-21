package com.hasan.youtubedownloader

import android.app.Application
import android.util.Log
import com.hasan.youtubedownloader.ui.home.TAG
import com.yausername.aria2c.Aria2c
import com.yausername.ffmpeg.FFmpeg
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLException
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DownloaderApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        try {
            YoutubeDL.getInstance().init(this)
            FFmpeg.getInstance().init(this)
            Aria2c.getInstance().init(this)
        } catch (e: YoutubeDLException) {
            Log.e(TAG, "failed to initialize youtubedl-android", e)
        }
    }

}

