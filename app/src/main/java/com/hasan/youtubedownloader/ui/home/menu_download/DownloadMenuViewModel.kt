package com.hasan.youtubedownloader.ui.home.menu_download

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hasan.youtubedownloader.data.YoutubeRepository
import com.yausername.youtubedl_android.mapper.VideoInfo

class DownloadMenuViewModel(
    private val repository: YoutubeRepository
) : ViewModel() {

    private var _formats = MutableLiveData<VideoInfo>()
    private val formats : LiveData<VideoInfo> = _formats


}