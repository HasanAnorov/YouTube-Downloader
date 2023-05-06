package com.hasan.youtubedownloader.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.hasan.youtubedownloader.R
import com.hasan.youtubedownloader.ui.base.BaseDialog
import com.hasan.youtubedownloader.databinding.HomeMenuDialogBinding

class MenuSelectDialog : BaseDialog() {

    private var _content : HomeMenuDialogBinding? = null
    private val content get() = _content!!

    override fun getContent(inflater: LayoutInflater, container: ViewGroup?): View {
        _content = HomeMenuDialogBinding.inflate(inflater,container,false)
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

    }

}
