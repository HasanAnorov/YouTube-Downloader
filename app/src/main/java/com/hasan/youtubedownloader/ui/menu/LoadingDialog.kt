package com.hasan.youtubedownloader.ui.menu

import android.app.Dialog
import android.content.Context
import android.widget.TextView
import com.hasan.youtubedownloader.R

class LoadingDialog(context: Context): Dialog(context){

    init {
        setContentView(R.layout.custom_progressbar)
        //setCancelable(false)
        setCanceledOnTouchOutside(false)
    }

    fun setContent(progressbarPercent:Int){
        findViewById<TextView>(R.id.progress_circular_percent).text = "Please wait - $progressbarPercent%"
    }

}