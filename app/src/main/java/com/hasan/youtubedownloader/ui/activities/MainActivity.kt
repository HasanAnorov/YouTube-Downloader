package com.hasan.youtubedownloader.ui.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.ActivityNavigator
import androidx.navigation.fragment.NavHostFragment
import com.hasan.youtubedownloader.R
import com.hasan.youtubedownloader.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel:MainVideModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // status bar text color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.decorView.systemUiVisibility =(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or  View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)
        }

        //status bar color
//        window.statusBarColor = ContextCompat.getColor(this,R.color.white)
//        window.navigationBarColor = ContextCompat.getColor(this,R.color.white)

        //added to Navigation instead
//        val fragment = StorageFolderFragment()
//        supportFragmentManager.commit {
//            setCustomAnimations(
//                enter = R.anim.slide_in,
////                exit = R.anim.fade_out,
////                popEnter = R.anim.fade_in,
////                popExit = R.anim.slide_out
//            )
//            replace(R.id.nav_host_fragment,fragment)
//            addToBackStack(null)
//        }

    }

    override fun finish() {
        super.finish()
        ActivityNavigator.applyPopAnimationsToPendingTransition(this)
    }

}