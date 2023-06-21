package com.hasan.youtubedownloader.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.view.isGone
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.hasan.youtubedownloader.R
import com.hasan.youtubedownloader.data.YoutubeRepository
import com.hasan.youtubedownloader.databinding.HomeMenuDialogBinding
import com.hasan.youtubedownloader.ui.base.BaseDialog
import com.yausername.youtubedl_android.YoutubeDL
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MenuSelectDialog : BaseDialog() {

    private var _content: HomeMenuDialogBinding? = null
    private val content get() = _content!!

    private var isUpdating = false

    lateinit var loadingDialog: LoadingDialog

    @Inject
    lateinit var repository: YoutubeRepository

    override fun getContent(inflater: LayoutInflater, container: ViewGroup?): View {
        _content = HomeMenuDialogBinding.inflate(inflater, container, false)
        return content.root
    }

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

    private fun updateYouTubeDL() {
        if (isUpdating) {
            Toast.makeText(requireContext(), "Update is already in progress", Toast.LENGTH_SHORT)
                .show()
        }

        isUpdating = true
        loadingDialog = LoadingDialog(requireContext())
        loadingDialog.findViewById<Button>(R.id.cancel_loading).isGone = true
        loadingDialog.findViewById<Button>(R.id.hide_loading).isGone = true
        loadingDialog.setContent("Wait, updating ...")
        loadingDialog.show()

        lifecycleScope.launch(Dispatchers.IO) {
            repository.updateYoutubeDL(YoutubeDL.UpdateChannel._STABLE)
            val result = YoutubeDL.getInstance().updateYoutubeDL(requireContext())
            if (result == YoutubeDL.UpdateStatus.ALREADY_UP_TO_DATE) {
                closeDialog(resources.getString(R.string.already_up_to_date))
            }
            if (result == YoutubeDL.UpdateStatus.DONE) {
                closeDialog(resources.getString(R.string.updated_successful))
            }
        }

    }

    private fun closeDialog(updateStatus: String) {
        lifecycleScope.launch(Dispatchers.Main) {
            Toast.makeText(requireContext(), updateStatus, Toast.LENGTH_SHORT)
                .show()
            isUpdating = false
            loadingDialog.dismiss()
        }
    }

}
