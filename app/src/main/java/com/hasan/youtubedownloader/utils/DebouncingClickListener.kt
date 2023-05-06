package com.hasan.youtubedownloader.utils

import android.view.View

class DebouncingOnClickListener(
    private val intervalMills:Long = 500L,
    private val doClick: (View) -> Unit
) : View.OnClickListener {

    override fun onClick(v: View) {
        if (enabled) {
            enabled = false
            v.postDelayed(ENABLE_AGAIN,intervalMills)
            doClick(v)
        }
    }

    companion object {
        private val ENABLE_AGAIN =
            Runnable { enabled = true }
        var enabled = true
    }
}