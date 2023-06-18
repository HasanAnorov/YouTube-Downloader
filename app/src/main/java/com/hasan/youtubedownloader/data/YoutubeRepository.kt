package com.hasan.youtubedownloader.data

import android.content.Context
import android.os.Environment
import android.util.Log
import com.hasan.youtubedownloader.utils.Resource
import com.yausername.youtubedl_android.DownloadProgressCallback
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLException
import com.yausername.youtubedl_android.YoutubeDLRequest
import com.yausername.youtubedl_android.mapper.VideoFormat
import com.yausername.youtubedl_android.mapper.VideoInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

class YoutubeRepository(
    private val context: Context
) {

    private val youtubeDLClient: YoutubeDL by lazy { YoutubeDL.getInstance() }

    fun getFormats(link: String) = flow<ArrayList<VideoFormat>> {
        youtubeDLClient.getInfo(YoutubeDLRequest(link)).formats
    }

    fun startDownload(link: String, format: String): Flow<Float> = callbackFlow {

        val formatNote = format.filter {
            it.isDigit()
        }

        val youtubeDLRequest = YoutubeDLRequest(link)

        val downloadsDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        val uniqueID = UUID.randomUUID().toString()

        youtubeDLRequest
            .addOption("--no-mtime")
            .addOption("-f", "bestvideo[height<=${formatNote}][ext=mp4]+bestaudio")
            .addOption(
                "-o", getDownloadLocation().absolutePath + "/$uniqueID/%(title)s.%(ext)s"
            )

        val filePath = File(downloadsDir, "${getDownloadLocation()}/$uniqueID/%(title)s.%(ext)s")
        if (filePath.exists()){
            Log.d("ahi3646", "exists $filePath")
        }else{
            Log.d("ahi3646", "not found $filePath")
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

    private fun getDownloadFile(info: VideoInfo, format: VideoFormat, folder: String): File {
        val filepath = File(folder, "${info.title} ${format.formatNote}.${info.ext}")


        (2..10_000).forEach { number ->
            val file = File(folder, "${info.title} ${format.formatNote} ($number).${info.ext}")
            if (!file.exists()) return@forEach
        }

        return if (!filepath.exists()) filepath
        else filepath
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