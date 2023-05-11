package com.hasan.youtubedownloader.ui.home.menu_download

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.hasan.youtubedownloader.R
import com.hasan.youtubedownloader.data.YoutubeRepository
import com.hasan.youtubedownloader.databinding.MenuDownloadBinding
import com.hasan.youtubedownloader.ui.base.BaseDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combineTransform
import javax.inject.Inject

@AndroidEntryPoint
class MenuDownload: BaseDialog() {

    private var _binding : MenuDownloadBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var repository: YoutubeRepository

    override fun getContent(inflater: LayoutInflater, container: ViewGroup?): View {
        Log.d("ahi3646", "getContent: ")

        _binding = MenuDownloadBinding.inflate(inflater,container,false)

        val formats = repository.getFormats()

        formats.forEach{
            Log.d("ahi3646", "getContent: ${it.formatNote}")
        }

        val adapter = MenuDownLoadAdapter(formats){

        }
        binding.recyclerView.adapter = adapter

        return binding.root
    }

}