package com.hasan.youtubedownloader.ui

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class IntentViewModel : ViewModel() {
    private val mutableExternalLink = MutableLiveData<String>()
    val externalLink: LiveData<String> get() = mutableExternalLink

    fun setExternalLink(intent: String) {
        mutableExternalLink.value = intent
    }
}