package com.hasan.youtubedownloader.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.webkit.MimeTypeMap
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.IOUtils
import com.hasan.youtubedownloader.R
import com.hasan.youtubedownloader.utils.FileNameUtils
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class DownloadWorker(context:Context, workerParams: WorkerParameters):
    CoroutineWorker(context, workerParams) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

//    override suspend fun getForegroundInfo(): ForegroundInfo {
//        return super.getForegroundInfo()
//    }

    override suspend fun doWork(): Result {
        val downloadUriInput = inputData.getString("DOWNLOAD_URI") ?: return Result.failure()

        createNotification()

        val notificationId = id.hashCode()
        val notification = NotificationCompat.Builder(
            applicationContext,
            channelId
        )
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Downloading ... ")
            .setContentText("Downloading - setInfo")
            .build()

        val foregroundInfo = ForegroundInfo(notificationId, notification)
        setForeground(foregroundInfo)

        // this is not the recommended way to add options/flags/url and might break in future
        // use the constructor for url, addOption(key) for flags, addOption(key, value) for options
        val request = YoutubeDLRequest(Collections.emptyList())
        val m: Matcher = Pattern.compile(commandRegex).matcher(downloadUriInput)
        while (m.find()) {
            if (m.group(1) != null) {
                request.addOption(m.group(1))
            } else {
                request.addOption(m.group(2))
            }
        }

        YoutubeDL.getInstance()
            .execute(request) { progress, _, line ->
                showProgress(id.hashCode(), progress.toInt(), line)
            }

        return Result.success()
    }

    private fun showProgress(id: Int, progress: Int, line: String) {
        val notification = NotificationCompat.Builder(
            applicationContext,
            channelId
        )
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(applicationContext.getString(R.string.command_noti_title))
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(line)
            )
            .setProgress(100, progress, progress == -1)
            .build()
        notificationManager.notify(id, notification)
    }

    private fun getDownloadLocation():File{
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val youtubeDlDir = File(downloadsDir,"youtubedl-android")
        if (!youtubeDlDir.exists()){
            youtubeDlDir.mkdir()
        }
        return youtubeDlDir
    }

    private fun createNotification(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var notificationChannel =
                notificationManager.getNotificationChannel(channelId)
            if (notificationChannel == null) {
                val channelName = applicationContext.getString(R.string.notification_channel_name)
                notificationChannel = NotificationChannel(
                    channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW
                )
                notificationChannel.description =
                    channelName
                notificationManager.createNotificationChannel(notificationChannel)
            }
        }
    }

    companion object {
        private const val channelId = "dvd_command"
        const val commandKey = "command"
        const val commandWorkTag = "command_work"
        private const val commandRegex = "\"([^\"]*)\"|(\\S+)"
    }

}