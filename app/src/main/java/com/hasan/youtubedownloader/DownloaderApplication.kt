package com.hasan.youtubedownloader

import android.app.Application
import android.util.Log
import com.hasan.youtubedownloader.ui.home.TAG
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLException

class DownloaderApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        try {
            YoutubeDL.getInstance().init(this)
        } catch (e: YoutubeDLException) {
            Log.e(TAG, "failed to initialize youtubedl-android", e)
        }
    }
}

