package com.hasan.youtubedownloader.data

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import com.yausername.youtubedl_android.DownloadProgressCallback
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLException
import com.yausername.youtubedl_android.YoutubeDLRequest
import com.yausername.youtubedl_android.mapper.VideoFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL

class YoutubeRepository(
    private val context: Context
) : YoutubeRepositorySource {

    private val youtubeDLClient: YoutubeDL by lazy { YoutubeDL.getInstance() }

    override fun getFormats(link: String): ArrayList<VideoFormat>? {
        return youtubeDLClient.getInfo(YoutubeDLRequest(link)).formats
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun startDownload(link: String, format: String): Flow<Float> = callbackFlow {
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

        val callback = DownloadProgressCallback { progress, _, _ -> trySend(progress) }

        withContext(Dispatchers.IO) {
            try {
                youtubeDLClient.execute(youtubeDLRequest, "taskId", callback)
//                youtubeDLClient.execute(
//                    request = youtubeDLRequest,
//                    processId = "taskId",
//                    callback = { progress, etaInSeconds, line ->
//                        callback.onProgressUpdate(
//                            progress,
//                            etaInSeconds,
//                            line
//                        )
//                    }
//                )
            } catch (e: InterruptedException) {

                Log.d("ahi3646", "startDownload: error - ${e.message}")
                close()
            } catch (e: YoutubeDLException) {
                Log.d("ahi3646", "startDownload: error - ${e.message}")
            }
        }
        awaitClose {
            youtubeDLClient.destroyProcessById("taskId")
        }
    }

    override fun updateYoutubeDL(): YoutubeDL.UpdateStatus? {
        return youtubeDLClient.updateYoutubeDL(context)
    }

    override fun cancelDownload(taskId: String): Boolean {
        return youtubeDLClient.destroyProcessById(taskId)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private suspend fun saveFileUsingMediaStore(url: String, fileName: String) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }
        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        if (uri != null) {
            withContext(Dispatchers.IO) {
                URL(url).openStream().use { input ->
                    resolver.openOutputStream(uri).use { output ->
                        input.copyTo(output!!, DEFAULT_BUFFER_SIZE)
                    }
                }
            }
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