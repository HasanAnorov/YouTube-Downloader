package com.hasan.youtubedownloader.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.hasan.youtubedownloader.R
import com.hasan.youtubedownloader.data.YoutubeRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main) {

    val TAG = "main_ahi3646"

    @Inject
    lateinit var repository: YoutubeRepository


    private val viewModel: IntentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            when (intent?.action) {
                Intent.ACTION_SEND -> {
                    if ("text/plain" == intent.type) {
                        handleSendText(intent) // Handle text being sent
                    }
                }

                else -> {
                    // Handle other intents, such as being started from the home screen
                }
            }
        }

        // status bar text color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.decorView.systemUiVisibility =
                (View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        lifecycleScope.launch {
            when (intent?.action) {
                Intent.ACTION_SEND -> {
                    if ("text/plain" == intent.type) {
                        handleSendText(intent) // Handle text being sent
                    }
                }
            }
        }
    }

    private fun handleSendText(intent: Intent) {
        intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
            viewModel.setExternalLink(it)
            // Update UI to reflect text being shared
            Log.d(TAG, "in main handleSendText: $it")
        }
    }

}
