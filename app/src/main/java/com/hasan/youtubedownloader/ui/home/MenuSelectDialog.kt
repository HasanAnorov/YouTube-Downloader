package com.hasan.youtubedownloader.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.view.isGone
import androidx.navigation.fragment.findNavController
import com.hasan.youtubedownloader.R
import com.hasan.youtubedownloader.data.YoutubeRepository
import com.hasan.youtubedownloader.databinding.HomeMenuDialogBinding
import com.hasan.youtubedownloader.ui.base.BaseDialog
import com.yausername.youtubedl_android.YoutubeDL
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MenuSelectDialog : BaseDialog() {

    private var _content: HomeMenuDialogBinding? = null
    private val content get() = _content!!

    private var isUpdating = false

    override fun getContent(inflater: LayoutInflater, container: ViewGroup?): View {
        _content = HomeMenuDialogBinding.inflate(inflater, container, false)
        return content.root
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        content.cardViewAbout.setOnClickListener {
            try {
                findNavController().navigate(R.id.aboutFragment)
            } catch (e: Exception) {
                //Timber.e(e)
            }
        }

        content.cardViewDownloads.setOnClickListener {
            try {
                findNavController().navigate(R.id.downloadsFragment)
            } catch (e: Exception) {
                //Timber.e(e)
            }
        }

        content.cardViewTheme.setOnClickListener {
            try {
                findNavController().navigate(R.id.themeSelectDialog)
            } catch (e: Exception) {
                //Timber.e(e)
            }
        }

        content.cardViewUpdate.setOnClickListener {
            updateYouTubeDL()
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun updateYouTubeDL() {
        if (isUpdating) {
            Toast.makeText(requireContext(), "Update is already in progress", Toast.LENGTH_SHORT)
                .show()
        }

        isUpdating = true
        val dialog = LoadingDialog(requireContext())
        dialog.findViewById<Button>(R.id.cancel_loading).isGone = true
        dialog.findViewById<Button>(R.id.hide_loading).isGone = true
        dialog.setContent("Wait, updating ...")
        dialog.show()
        GlobalScope.launch(Dispatchers.IO) {
            val youtubeDL = YoutubeDL.getInstance()
            YoutubeRepository(youtubeDL,requireContext()).updateYoutubeDl()
            val result = YoutubeDL.getInstance().updateYoutubeDL(requireContext())
            if (result == YoutubeDL.UpdateStatus.ALREADY_UP_TO_DATE) {
                GlobalScope.launch(Dispatchers.Main) {
                    Toast.makeText(requireContext(), R.string.already_updated, Toast.LENGTH_SHORT)
                        .show()
                    isUpdating = false
                    dialog.dismiss()
                }
            }
            if (result == YoutubeDL.UpdateStatus.DONE) {
                GlobalScope.launch(Dispatchers.Main) {
                    Toast.makeText(requireContext(), R.string.updated_successful, Toast.LENGTH_SHORT)
                        .show()
                    isUpdating = false
                    dialog.dismiss()
                }
            }
        }
    }
}
