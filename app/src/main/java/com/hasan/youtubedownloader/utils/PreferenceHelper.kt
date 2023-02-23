package com.hasan.youtubedownloader.utils

import android.content.Context
import android.util.Log
import kotlin.math.log

private const val SHARED_PREF = "sharedPref"
private const val THEME_MODE_KEY = "themeModeKey"
private const val TAG = "preferenceHelper"

object PreferenceHelper {

    fun setThemeMode(context: Context, isLight:Boolean){
        val sharedPref = context.getSharedPreferences(SHARED_PREF,Context.MODE_PRIVATE)
        with(sharedPref.edit()){
            putBoolean(THEME_MODE_KEY,isLight)
            apply()
            Log.d(TAG, "set isLight:${ isLight }")
        }
    }

    fun isLight(context: Context): Boolean {
        val sharedPref = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
        Log.d(TAG, "get isLight:${ sharedPref.getBoolean(THEME_MODE_KEY, true) }")
        return sharedPref.getBoolean(THEME_MODE_KEY, true)
    }

}