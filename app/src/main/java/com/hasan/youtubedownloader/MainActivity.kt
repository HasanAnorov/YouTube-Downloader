package com.hasan.youtubedownloader

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.commit
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.hasan.youtubedownloader.databinding.ActivityMainBinding
import com.hasan.youtubedownloader.ui.screens.StorageFolderFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    //private lateinit var appBarConfiguration: AppBarConfiguration
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

//        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController,appBarConfiguration)

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


//        val menuItem = findViewById<>().menu.findItem(R.id.darkMode)
//        val switch_id = menuItem.actionView as SwitchCompat
//        switch_id.isChecked = true
//        switch_id.setOnClickListener {
//            Toast.makeText(
//                applicationContext,
//                if (switch_id.isChecked) "is checked!!!" else "not checked!!!",
//                Toast.LENGTH_SHORT
//            ).show()
//        }


    }


}