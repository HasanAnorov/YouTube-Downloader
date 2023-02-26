package com.hasan.youtubedownloader.utils

import android.content.Context
import android.content.res.Resources.Theme
import android.util.Log
import kotlin.math.log

private const val SHARED_PREF = "sharedPref"
private const val THEME_MODE_KEY = "themeModeKeyString"
private const val TAG = "preferenceHelper"

object PreferenceHelper {

    const val DARK = "dark_theme"
    const val LIGHT = "light_theme"
    const val INITIAL = "initial_app_theme"

    fun setThemeMode(context: Context, mode:String){
        val sharedPref = context.getSharedPreferences(SHARED_PREF,Context.MODE_PRIVATE)
        with(sharedPref.edit()){
            putString(THEME_MODE_KEY,mode)
            apply()
            Log.d(TAG, "set isLight:${ mode}")
        }
    }

    fun isLight(context: Context): String {
        val sharedPref = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
       // Log.d(TAG, "get isLight:${ sharedPref.getString(THEME_MODE_KEY, INITIAL) }")
        return sharedPref.getString(THEME_MODE_KEY, INITIAL)!!
    }

}
