package com.hasan.youtubedownloader.utils

import android.content.Context
import com.hasan.youtubedownloader.utils.Constants.INITIAL

private const val SHARED_PREF = "sharedPref"
private const val THEME_MODE_KEY = "themeModeKeyString"
private const val TAG = "preferenceHelper"

object PreferenceHelper {

    fun setThemeMode(context: Context, mode:String){
        val sharedPref = context.getSharedPreferences(SHARED_PREF,Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString(THEME_MODE_KEY, mode)
            apply()
            //Timber.tag(TAG).d("set isLight:%s", mode)
        }
    }

    fun isLight(context: Context): String {
        val sharedPref = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
       // Log.d(TAG, "get isLight:${ sharedPref.getString(THEME_MODE_KEY, INITIAL) }")
        return sharedPref.getString(THEME_MODE_KEY, INITIAL)!!
    }

}
