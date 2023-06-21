package com.hasan.youtubedownloader.ui.home

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.viewModelScope
import com.hasan.youtubedownloader.data.YoutubeRepository
import com.hasan.youtubedownloader.utils.Resource
import com.yausername.youtubedl_android.YoutubeDLException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: YoutubeRepository) : ViewModel() {

//    @Inject
//    lateinit var repository: YoutubeRepository

    private var _progress = MutableLiveData(0f)
    val progress: LiveData<Float> = _progress

    var isDownloading = false

    fun getFormats(link: String): Resource<ArrayList<String>> {
        return try {
            Log.d(TAG, "getJustFormats: try")
            val formats = repository.getFormats(link)
            formats?.sortBy { it.fileSize }
            formats?.removeIf {
                it.ext != "mp4" || it.fileSize == 0L
            }
            val sortedFormats = ArrayList<String>()
            formats?.forEach {
                if(!sortedFormats.contains(it.formatNote))
                sortedFormats.add(it.formatNote!!)
            }
            Resource.Success(sortedFormats)
        } catch (e: YoutubeDLException) {
            Log.d(TAG, "getJustFormats: catch - ${e.message}")
            Resource.DataError("Entered url is not valid")
        }
    }

    fun startDownload(link: String, format: String, lifecycle: Lifecycle) {
        isDownloading = true

        Log.d(TAG, "startDownload: vm ${progress.value}")
        repository.startDownload(link, format)
            .flowWithLifecycle(lifecycle = lifecycle)
            .onEach { progress ->
                _progress.postValue(progress)
                Log.d(TAG, "getProgress -  $progress")
            }
            .launchIn(viewModelScope)

    }

    fun onComplete() {
        _progress.postValue(0f)
        isDownloading = false
    }

    fun cancelDownload(taskId: String) {
        repository.cancelDownload(taskId)
        _progress.postValue(0f)
        isDownloading = false
    }

}