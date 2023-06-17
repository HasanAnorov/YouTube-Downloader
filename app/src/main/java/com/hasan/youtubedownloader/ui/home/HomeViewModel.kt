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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: YoutubeRepository) : ViewModel() {

    private var _formats : MutableLiveData<Resource<ArrayList<String>>> = MutableLiveData(Resource.Loading())
    val formats: LiveData<Resource<ArrayList<String>>> get() = _formats

    fun getFormats(link: String) {
//        viewModelScope.launch(Dispatchers.Main) {
//            _formats.value = Resource.Loading()
//            Log.d(TAG, "getFormats viewModel: initial load")
//        }
        repository.getFormats(link).let {
            when (it) {
                is Resource.Success -> {
                    viewModelScope.launch(Dispatchers.Main) {
                        _formats.value = it
                    }
                }

                is Resource.DataError -> {
                    viewModelScope.launch(Dispatchers.Main) {
                        _formats.value = it
                    }
                }

                else -> {
                    viewModelScope.launch(Dispatchers.Main) {
                        _formats.value = Resource.DataError("Something went wong!")
                    }
                }
            }
        }
    }
}