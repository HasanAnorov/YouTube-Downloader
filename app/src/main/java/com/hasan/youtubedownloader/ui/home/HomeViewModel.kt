package com.hasan.youtubedownloader.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import com.hasan.youtubedownloader.data.YoutubeRepository
import com.hasan.youtubedownloader.utils.Resource
import com.yausername.youtubedl_android.YoutubeDLException
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: YoutubeRepository) : ViewModel() {

    fun getJustFormats(link: String): Resource<ArrayList<String>>{
        return try {
            Log.d(TAG, "getJustFormats: try")
            val formats = repository.getFormats(link)
            formats?.sortBy { it.fileSize }
            formats?.removeIf {
                it.ext != "mp4" || it.fileSize == 0L
            }
            val sortedFormats = ArrayList<String>()
            formats?.forEach {
                sortedFormats.add(it.formatNote!!)
            }
            Resource.Success(sortedFormats)
        }catch (e: YoutubeDLException){
            Log.d(TAG, "getJustFormats: catch - ${e.message}")
            Resource.DataError("Entered url is not valid")
        }
    }

}