package com.hasan.youtubedownloader.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.hasan.youtubedownloader.data.YoutubeRepository
import com.hasan.youtubedownloader.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: YoutubeRepository) : ViewModel() {

    private var _formats = MutableStateFlow<Resource<ArrayList<String>>>(Resource.Loading())
    val formats: StateFlow<Resource<ArrayList<String>>> get() = _formats

    suspend fun getFormats(link: String) {
        _formats.value = Resource.Loading()
        repository.getFormats(link).catch {
            Log.d(TAG, "getFormats: ${it.message}")
            _formats.value = Resource.DataError("Entered url is not valid")
        }.collect { videoFormats ->
            videoFormats.sortBy { it.fileSize }
            videoFormats.removeIf {
                it.ext != "mp4" || it.fileSize == 0L
            }

            val sortedFormats = ArrayList<String>()
            videoFormats.forEach {
                sortedFormats.add(it.formatNote!!)
            }

            sortedFormats.forEach {
                Log.d("ahi3646", "getFormats: $it ")
            }
            _formats.value = Resource.Success(sortedFormats)
        }
    }
}