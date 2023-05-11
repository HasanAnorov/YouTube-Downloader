package com.hasan.youtubedownloader.data

import android.content.Context
import android.os.Build
import android.os.Environment
import android.util.Log
import com.yausername.youtubedl_android.DownloadProgressCallback
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLRequest
import com.yausername.youtubedl_android.mapper.VideoFormat
import com.yausername.youtubedl_android.mapper.VideoInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.log

class YoutubeRepository(
    private val context: Context
) {

    private lateinit var youtubeDLClient: YoutubeDL
    private lateinit var youtubeDLRequest: YoutubeDLRequest

    fun prepareForDownload(link: String) {
        Log.d("ahi3646", "prepareForDownload: $link")
        youtubeDLClient = YoutubeDL.getInstance()
        youtubeDLRequest = YoutubeDLRequest(link)
    }

     fun getFormats(): ArrayList<VideoFormat> {
        Log.d("ahi3646", "getFormats: ")

        val videoFormats = youtubeDLClient.getInfo(youtubeDLRequest).formats

        videoFormats?.sortBy { it.fileSize }
        videoFormats?.removeIf {
            it.ext != "mp4" || it.fileSize == 0L
        }

        videoFormats?.forEach {
            Log.d(
                "ahi3646",
                "formats - ${"formatNote - " + it.formatNote + " format - " + it.format + " formatID - " + it.formatId + " formatSize - " + it.fileSize + " ext - " + it.ext + " preference - " + it.preference} "
            )
        }
        return videoFormats!!
    }


    fun startDownload(link: String): Flow<Float> = callbackFlow {

        Log.d("ahi3646", "startDownload: triggered $link ")

        youtubeDLRequest
//            .addOption("--no-mtime")
//            .addOption("--downloader", "libaria2c.so")
//            .addOption("--external-downloader-args", "aria2c:\"--summary-interval=1\"")
//            .addOption("-f", "bestvideo[ext=mp4]+bestaudio[ext=m4a]/best[ext=mp4]/best")
            //.addOption("--list-formats")
            .addOption(
                "-o", getDownloadLocation().absolutePath + "/%(title)s.%(ext)s"
            )

        val callback = object : DownloadProgressCallback {
            override fun onProgressUpdate(progress: Float, etaInSeconds: Long, line: String?) {
                trySend(progress)
            }
        }

        withContext(Dispatchers.IO) {
            try {
                youtubeDLClient.execute(
                    request = youtubeDLRequest,
                    processId = "taskId",
                    callback = { progress, etaInSeconds, line ->
                        callback.onProgressUpdate(
                            progress,
                            etaInSeconds,
                            line
                        )
                    }
                )
            } catch (e: YoutubeDL.CanceledException) {
                close()
            }
        }

        awaitClose {
            youtubeDLClient.destroyProcessById("taskId")
        }
    }

    fun updateYoutubeDL(updateChannel: YoutubeDL.UpdateChannel) {
        youtubeDLClient.updateYoutubeDL(context, updateChannel)
    }

    fun cancelDownload(taskId: String) {
        youtubeDLClient.destroyProcessById(taskId)
    }

    private fun getDownloadLocation(): File {
        val downloadsDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val youtubeDlDir = File(downloadsDir, "hasan_YT_android")
        if (!youtubeDlDir.exists()) {
            youtubeDlDir.mkdir()
        }
        return youtubeDlDir
    }

}