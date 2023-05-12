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

    private val youtubeDLClient: YoutubeDL = YoutubeDL.getInstance()
    private lateinit var youtubeDLRequest: YoutubeDLRequest

    fun getFormats(link: String): ArrayList<VideoFormat> {

        youtubeDLRequest = YoutubeDLRequest(link)
        val videoFormats = youtubeDLClient.getInfo(youtubeDLRequest).formats

        videoFormats?.sortBy { it.fileSize }
        videoFormats?.removeIf {
            it.ext != "mp4" || it.fileSize == 0L
        }

        val sortedFormats = ArrayList<String>()
        videoFormats?.forEach {
            sortedFormats.add(it.formatNote!!)
        }

        sortedFormats.toSet()
        sortedFormats.toHashSet()

        sortedFormats.forEach{
            Log.d("ahi3646", "getFormats: $it ")
        }

        return videoFormats!!
    }

    fun startDownload(link: String, format: VideoFormat): Flow<Float> = callbackFlow {

        val formatNote = format.formatNote.toString().filter {
            it.isDigit()
        }

        youtubeDLRequest = YoutubeDLRequest(link)

        youtubeDLRequest
            .addOption("--no-mtime")
            .addOption("-f", "bestvideo[height<=${formatNote}][ext=mp4]+bestaudio/best[height<=${formatNote}][ext=mp4]")
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