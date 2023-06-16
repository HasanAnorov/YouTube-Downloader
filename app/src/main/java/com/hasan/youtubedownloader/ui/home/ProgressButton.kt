package com.hasan.youtubedownloader.ui.home

import android.content.Context
import android.content.res.ColorStateList
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

class ProgressButton(context: Context, view: View) {

    private var parentCardView: MaterialCardView
    private var linearLayout: LinearLayout
    private var searchCardView: MaterialCardView
    private var edittext: EditText
    private var downloadCard: MaterialCardView
    private var infoText: TextView
    private var ivDownload: ImageView
    private var progressBar: ProgressBar
    private var fade_in: Animation

    init {
        fade_in = AnimationUtils.loadAnimation(context, R.anim.fade_in)
        parentCardView = view.findViewById(R.id.card_search)
        linearLayout = view.findViewById(R.id.container_layout)
        searchCardView = view.findViewById(R.id.card_clear)
        edittext = view.findViewById(R.id.et_paste_linkt)
        downloadCard = view.findViewById(R.id.card_download)
        infoText = view.findViewById(R.id.info_text)
        progressBar = view.findViewById(R.id.progressBar)
        ivDownload = view.findViewById(R.id.iv_download)
    }

    val lightRipple = ColorStateList(
        arrayOf<IntArray>(intArrayOf()), intArrayOf(
            context.resources.getColor(R.color.rippleColor)
        )
    )

    fun buttonActivated(){
        progressBar.animation = fade_in
        progressBar.visibility = View.VISIBLE
        infoText.visibility = View.VISIBLE
        infoText.animation = fade_in


        searchCardView.visibility = View.GONE
        edittext.visibility = View.GONE
        ivDownload.visibility = View.GONE
    }

    fun buttonFinished(){
        progressBar.visibility = View.GONE
        infoText.text = "Done"
        infoText.visibility = View.GONE

        searchCardView.rippleColor = lightRipple

        searchCardView.visibility = View.VISIBLE
        searchCardView.animation = fade_in
        edittext.visibility = View.VISIBLE
        edittext.animation = fade_in
        ivDownload.visibility = View.VISIBLE
        ivDownload.animation = fade_in
    }

}