package com.hasan.youtubedownloader.work

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.os.bundleOf
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
        notificationManager.notify(0,notification)

        return Result.success()
    }



}