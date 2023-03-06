package com.hasan.youtubedownloader.work

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavDeepLinkBuilder
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.hasan.youtubedownloader.NOTIFICATION_CHANNEL_ID
import com.hasan.youtubedownloader.R
import com.hasan.youtubedownloader.ui.activities.MainActivity

class CommandWorker(val context: Context,params:WorkerParameters):Worker(context,params){

    override fun doWork(): Result {

        val pendingIntent = NavDeepLinkBuilder(context)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.storageFolderFragment)
            .createPendingIntent()

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

        return Result.success()
    }

    companion object{
        const val ACTION_SHOW_NOTIFICATION = "com.hasan.youtubedownloader.SHOW_NOTIFICATION"
        const val CUSTOM_PRIVATE_PERMISSION = "com.hasan.youtubedownloader.PRIVATE"
    }



}