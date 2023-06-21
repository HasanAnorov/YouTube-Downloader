package com.hasan.youtubedownloader.data

import android.content.Context
import android.os.Environment
import android.util.Log
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
import kotlinx.coroutines.withContext
import java.io.File

class YoutubeRepository(
    private val context: Context
) {

    private val youtubeDLClient: YoutubeDL by lazy { YoutubeDL.getInstance() }

    fun getFormats(link: String): ArrayList<VideoFormat>? {
        return youtubeDLClient.getInfo(YoutubeDLRequest(link)).formats
    }

    private fun getDownloadPath(title: String): String{
        val downloadPath =
            Environment.getExternalStorageDirectory()
                .toString() + File.separator + "DemoYouTubeDownloader"

        val folderPath = File(downloadPath, "YT_Hasan")
        var file: File? = null
        var videoFilePath = ""

        if (!folderPath.exists()) {
            if (folderPath.mkdirs()) {
                file = File(downloadPath, "/YT_Downloader/$title.mp4")
            } else {
                Log.d("ahi3646", "startDownload: cannot create file directory ")
            }
        } else {
            file = File(downloadPath, "/YT_Downloader/" + System.currentTimeMillis() + "mp4")
        }
        videoFilePath = file!!.absolutePath
        return videoFilePath
    }

    fun startDownload(link: String, format: String): Flow<Float> = callbackFlow {
        val formatNote = format.filter {
            it.isDigit()
        }

        val youtubeDLRequest = YoutubeDLRequest(link)

        youtubeDLRequest
            .addOption("--no-mtime")
            .addOption("-f", "bestvideo[height<=${formatNote}][ext=mp4]+bestaudio")
            .addOption(
                "-o", getDownloadLocation().absolutePath + "/%(title)s.%(ext)s"
            )

        //Log.d("ahi3646", "startDownload: demo path - ${getDownloadPath("andrew_tate")} ")

//        val downloadsDir =
//            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//        val filePath = File(downloadsDir, "hasan_YT_android")
//        if (filePath.exists()) {
//            Log.d("ahi3646", "exists $filePath")
//        } else {
//            Log.d("ahi3646", "not found $filePath")
//        }

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

                Log.d("ahi3646", "startDownload: error - ${e.message}")
                close()
            }catch (e: YoutubeDLException){
                Log.d("ahi3646", "startDownload: error - ${e.message}")
            }
        }
        awaitClose {
            destroyProcessById("taskId")
        }
    }

    private fun destroyProcessById(taskId: String) {
        youtubeDLClient.destroyProcessById(taskId)
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
        val downloadPath =
            Environment.getExternalStorageDirectory()
                .toString() + File.separator + "YouTubeDownloader"
        val youtubeDlDir = File(downloadPath, "hasan_YT_android")
        if (!youtubeDlDir.exists()) {
            youtubeDlDir.mkdir()
        }
        return youtubeDlDir
    }

//    private fun getDownloadLocation(): File {
//        val downloadsDir =
//            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//        val youtubeDlDir = File(downloadsDir, "hasan_YT_android")
//        if (!youtubeDlDir.exists()) {
//            youtubeDlDir.mkdir()
//        }
//        return youtubeDlDir
//    }

}