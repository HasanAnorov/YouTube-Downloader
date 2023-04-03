package com.hasan.youtubedownloader.ui.menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hasan.youtubedownloader.base.BaseViewModel

//class ThemeViewModel(private val repository: ThemeRepository) : BaseViewModel() {
//
//    private val _dark = MutableLiveData<Int>()
//    val dark: LiveData<Int> = _dark
//
//    init {
//        darkMode()
//    }
//
//    private fun darkMode() {
//        _dark.postValue(-1)
//        _dark.postValue(repository.darkMode)
//    }
//
//    fun setDarkMode(value: Int) {
//        _dark.postValue(value)
//        repository.darkMode = value
//    }
//
//    fun darkModeActivity():Int{
//        return repository.darkMode
//    }
//}

class ThemeViewModel :BaseViewModel() {

}