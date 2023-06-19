package com.hasan.youtubedownloader.ui.home.menu_download

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.hasan.youtubedownloader.databinding.MenuDownloadBinding
import com.hasan.youtubedownloader.ui.base.BaseDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val TAG = "ahi3646"

interface DialogForResultCallback {
    fun onResultSuccess(item: String)
    fun onResultFailed(ex: Exception)
}

@AndroidEntryPoint
class MenuDownload(
    private var callback: DialogForResultCallback, private val formats: ArrayList<String>
) : BaseDialog() {

    private var _binding: MenuDownloadBinding? = null
    private val binding get() = _binding!!

    override fun getContent(inflater: LayoutInflater, container: ViewGroup?): View {
        _binding = MenuDownloadBinding.inflate(inflater, container, false)

        val adapter by lazy {
            MenuDownLoadAdapter(formats) {
                Log.d(TAG, "getContent: else")
                callback.onResultSuccess(it)
                dismiss()
            }
        }

        binding.recyclerView.adapter = adapter

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}