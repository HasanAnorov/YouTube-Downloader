package com.hasan.youtubedownloader.ui.home.menu_download

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.hasan.youtubedownloader.R
import com.hasan.youtubedownloader.data.YoutubeRepository
import com.hasan.youtubedownloader.databinding.MenuDownloadBinding
import com.hasan.youtubedownloader.ui.base.BaseDialog
import com.hasan.youtubedownloader.ui.home.LoadingDialog
import com.yausername.youtubedl_android.YoutubeDLException
import com.yausername.youtubedl_android.mapper.VideoFormat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

const val TAG = "ahi3646"

@AndroidEntryPoint
class MenuDownload : BaseDialog() {

    private var _binding: MenuDownloadBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var repository: YoutubeRepository
    private lateinit var dialog: LoadingDialog
    private var isDownloading: Boolean = false

    override fun getContent(inflater: LayoutInflater, container: ViewGroup?): View {
        Log.d("ahi3646", "getContent: ")

        val formats = arguments?.getStringArrayList("formats") ?: arrayListOf()

        _binding = MenuDownloadBinding.inflate(inflater, container, false)
        dialog = LoadingDialog(requireContext())

        try {
            val adapter = MenuDownLoadAdapter(formats) {
                //if (isDownloading) dialog.show() else startDownload(link, it)
                if (isDownloading)
                    dialog.show()
                else {
                    val bundle = Bundle()
                    bundle.putString("videoFormat", it)
                    findNavController().navigate(R.id.homeFragment, bundle)
                    dismiss()
                }
            }
            binding.recyclerView.adapter = adapter
        } catch (e: YoutubeDLException) {
            Log.d(TAG, "getContent: ${e.message}")
        }

        dialog.findViewById<Button>(R.id.cancel_loading).setOnClickListener {
            lifecycleScope.launch {
                cancelDownload("taskId")
            }
            isDownloading = false
        }

        dialog.findViewById<Button>(R.id.hide_loading).setOnClickListener {
            dialog.dismiss()
        }

        return binding.root
    }

    private fun startDownload(link: String, format: String) {
        dialog.setContent("Please wait  0 %")
        dialog.show()

        isDownloading = true

        repository.startDownload(link, format)
            .flowWithLifecycle(lifecycle)
            .onEach { progress ->
                updateDialog(progress.toInt())
                Log.d(TAG, "getProgress -  $progress")
                if (progress.toInt() == 100) {
                    isDownloading = false
                    closeDialog()
                }
            }
            .launchIn(lifecycleScope)
    }

    private fun updateDialog(progress: Int) {
        dialog.setContent(if (progress < 0) "Please wait  0 %" else "Please wait  $progress %")
    }

    private fun cancelDownload(processId: String) {
        Log.d(TAG, "cancelDownload: ")
        repository.cancelDownload(processId)
        dialog.dismiss()
    }

    private fun closeDialog() {
        lifecycleScope.launch {
            dialog.setContent("Video successfully downloaded!")
            delay(1000)
            dialog.dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}