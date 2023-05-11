package com.hasan.youtubedownloader.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.hasan.youtubedownloader.R
import com.hasan.youtubedownloader.databinding.MenuDownloadBinding
import com.hasan.youtubedownloader.ui.base.BaseDialog

class MenuDownload: BaseDialog() {

    private var _binding : MenuDownloadBinding? = null
    private val binding get() = _binding!!

    override fun getContent(inflater: LayoutInflater, container: ViewGroup?): View {
        _binding = MenuDownloadBinding.inflate(inflater,container,false)

        val formats = resources.getStringArray(R.array.formats)
        val arrayAdapter = ArrayAdapter(
            requireContext(),
            R.layout.dropdown_item,
            formats
        )
        binding.autoCompleteTextView.setAdapter(arrayAdapter)

        return binding.root
    }

}