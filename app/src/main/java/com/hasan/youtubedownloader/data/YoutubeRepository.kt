package com.hasan.youtubedownloader.data

import android.content.Context
import android.os.Environment
import android.util.Log
import com.yausername.youtubedl_android.DownloadProgressCallback
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import java.io.File

class YoutubeRepository(
    private val context: Context
) {

    private val youtubeDLClient = YoutubeDL.getInstance()

    fun updateYoutubeDL(updateChannel: YoutubeDL.UpdateChannel) {
        youtubeDLClient.updateYoutubeDL(context, updateChannel)
    }

    fun cancelDownload(taskId: String) {
        youtubeDLClient.destroyProcessById(taskId)
    }

    fun startDownload(link: String): Flow<Float> = callbackFlow {

        Log.d("ahi3646", "startDownload: triggered $link ")

        val youtubeDLRequest = YoutubeDLRequest(link)
            .addOption("--no-mtime")
            .addOption("--downloader", "libaria2c.so")
            .addOption("--external-downloader-args", "aria2c:\"--summary-interval=1\"")
            .addOption("-f", "bestvideo[ext=mp4]+bestaudio[ext=m4a]/best[ext=mp4]/best")
            .addOption(
                "-o", getDownloadLocation().absolutePath + "/%(title)s.%(ext)s"
            )



        with(youtubeDLClient.getInfo(youtubeDLRequest)){
            Log.d("ahi3646", "resolution: $resolution ")
            requestedFormats?.forEach {
                Log.d("ahi3646", "requestedFormats - ${"formatNote - " + it.formatNote + " format - " + it.format + " formatID - " + it.formatId } ")            }
            formats?.forEach {
                Log.d("ahi3646", "formats - ${"formatNote - " + it.formatNote + " format - " + it.format + " formatID - " + it.formatId } ")
            }
        }

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