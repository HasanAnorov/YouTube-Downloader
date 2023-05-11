//package com.hasan.youtubedownloader.data
//
//import android.content.Context
//import android.os.Environment
//import com.yausername.youtubedl_android.DownloadProgressCallback
//import com.yausername.youtubedl_android.YoutubeDL
//import com.yausername.youtubedl_android.YoutubeDLRequest
//import dagger.hilt.android.AndroidEntryPoint
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.channels.awaitClose
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.SharingStarted
//import kotlinx.coroutines.flow.callbackFlow
//import kotlinx.coroutines.flow.shareIn
//import java.io.File
//
//class SharedDownloadManager(
//    private val context: Context
//) {
//
//    private val youtubeDLClient = YoutubeDL.getInstance()
//
//    private fun getDownloadLocation(): File {
//        val downloadsDir =
//            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//        val youtubeDlDir = File(downloadsDir, "hasan_YT_android")
//        if (!youtubeDlDir.exists()) {
//            youtubeDlDir.mkdir()
//        }
//        return youtubeDlDir
//    }
//
//    fun download(link: String): Flow<Float> = callbackFlow {
//
//        val youtubeDLRequest = YoutubeDLRequest(link).addOption(
//            "-o", getDownloadLocation().absolutePath + "/%(title)s.%(ext)s"
//        )
//
//        val callback = object : DownloadProgressCallback {
//            override fun onProgressUpdate(progress: Float, etaInSeconds: Long, line: String?) {
//                trySend(progress)
//            }
//        }
//
//        youtubeDLClient.execute(
//            request = youtubeDLRequest,
//            processId = "taskId",
//            callback = { progress, etaInSeconds, line ->
//                callback.onProgressUpdate(
//                    progress,
//                    etaInSeconds,
//                    line
//                )
//            }
//        )
//
//        awaitClose {
//            youtubeDLClient.destroyProcessById("taskId")
//        }
//    }
//
////    private val _progressUpdates = callbackFlow<Float> {
////
////        val callback = object : DownloadProgressCallback {
////            override fun onProgressUpdate(progress: Float, etaInSeconds: Long, line: String?) {
////                trySend(progress)
////            }
////        }
////
////        youtubeDLClient.execute(
////            request = youtubeDLRequest,
////            processId = "taskId",
////            callback = { progress, etaInSeconds, line ->
////                callback.onProgressUpdate(
////                    progress,
////                    etaInSeconds,
////                    line
////                )
////            }
////        )
////
////        awaitClose {
////            youtubeDLClient.destroyProcessById("taskId")
////        }
////
////    }.shareIn(
////        externalSCope,
////        replay = 0,
////        started = SharingStarted.WhileSubscribed()
////    )
//
////    fun downloadFlow(): Flow<Float> {
////        return _progressUpdates
////    }
//
//    fun updateYoutubeDL(updateChannel: YoutubeDL.UpdateChannel){
//        youtubeDLClient.updateYoutubeDL(context,updateChannel)
//    }
//
//}