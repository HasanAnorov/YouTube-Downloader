package com.hasan.youtubedownloader.work

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavDeepLinkBuilder
import androidx.work.ForegroundInfo
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.hasan.youtubedownloader.NOTIFICATION_CHANNEL_ID
import com.hasan.youtubedownloader.R
import com.hasan.youtubedownloader.ui.activities.MainActivity
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLRequest
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class CommandWorker(val context: Context,params:WorkerParameters):Worker(context,params){

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as
                NotificationManager?

    override fun doWork(): Result {

        val command = inputData.getString(commandKey)!!

        createNotificationChannel()

        val pendingIntent = NavDeepLinkBuilder(context)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.storageFolderFragment)
            .createPendingIntent()

        val notificationId =id.hashCode()

        val resource = context.resources
        val notification = NotificationCompat
            .Builder(context, NOTIFICATION_CHANNEL_ID)
            .setTicker(resource.getString(R.string.new_downloaded_video))
            .setSmallIcon(R.drawable.ic_baseline_lock)
            .setContentTitle(resource.getString(R.string.new_downloaded_video))
            .setContentText(resource.getString(R.string.new_videos_text))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = NotificationManagerCompat.from(context)
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(0,notification)
            context.sendBroadcast(Intent(ACTION_SHOW_NOTIFICATION), CUSTOM_PRIVATE_PERMISSION)
        }else{
            Toast.makeText(context, "You will not be notified", Toast.LENGTH_SHORT).show()
        }

        val foregroundInfo =ForegroundInfo(notificationId,notification)
        setForegroundAsync(foregroundInfo)

        // this is not the recommended way to add options/flags/url and might break in future
        // use the constructor for url, addOption(key) for flags, addOption(key, value) for options
        val request = YoutubeDLRequest(Collections.emptyList())
        val m: Matcher = Pattern.compile(commandRegex).matcher(command)
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
            .setContentTitle(context.getString(R.string.command_noti_title))
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(line)
            )
            .setProgress(100, progress, progress == -1)
            .build()
        notificationManager?.notify(id, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var notificationChannel =
                notificationManager?.getNotificationChannel(channelId)
            if (notificationChannel == null) {
                val channelName = applicationContext.getString(R.string.command_noti_title)
                notificationChannel = NotificationChannel(
                    channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW
                )
                notificationChannel.description =
                    channelName
                notificationManager?.createNotificationChannel(notificationChannel)
            }
        }
    }


    companion object{
        const val ACTION_SHOW_NOTIFICATION = "com.hasan.youtubedownloader.SHOW_NOTIFICATION"
        const val CUSTOM_PRIVATE_PERMISSION = "com.hasan.youtubedownloader.PRIVATE"
        private const val channelId = "dvd_command"
        const val commandKey = "command"
        const val commandWorkTag = "command_work"
        private const val commandRegex = "\"([^\"]*)\"|(\\S+)"
    }



}