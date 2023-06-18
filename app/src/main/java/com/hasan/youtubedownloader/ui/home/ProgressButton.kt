package com.hasan.youtubedownloader.ui.home

import android.content.Context
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.google.android.material.card.MaterialCardView
import com.hasan.youtubedownloader.R
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Timer
import java.util.logging.Handler

class ProgressButton(context: Context, view: View) {

    private var searchCardView: MaterialCardView = view.findViewById(R.id.card_clear)
    private var edittext: EditText = view.findViewById(R.id.et_paste_linkt)
    private var downloadCard: MaterialCardView = view.findViewById(R.id.card_download)
    private var infoText: TextView = view.findViewById(R.id.info_text)
    private var ivDownload: ImageView = view.findViewById(R.id.iv_download)
    private var progressBar: ProgressBar = view.findViewById(R.id.progressBar)
    private var fadeIn: Animation = AnimationUtils.loadAnimation(context, R.anim.fade_in)

    fun buttonActivated() {

        downloadCard.isClickable = false

        progressBar.visibility = View.VISIBLE
        progressBar.animation = fadeIn
        infoText.visibility = View.VISIBLE
        infoText.animation = fadeIn

        searchCardView.visibility = View.GONE
        edittext.visibility = View.GONE
        ivDownload.visibility = View.GONE
    }

    fun changeInfoText(text: String) {
        infoText.text = text
    }

    fun buttonFinished(result: String) {
        progressBar.visibility = View.GONE
        infoText.text = result

        infoText.visibility = View.GONE

        searchCardView.visibility = View.VISIBLE
        searchCardView.animation = fadeIn
        edittext.visibility = View.VISIBLE
        edittext.animation = fadeIn
        ivDownload.visibility = View.VISIBLE
        ivDownload.animation = fadeIn

    }

}