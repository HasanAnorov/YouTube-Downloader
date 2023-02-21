package com.hasan.youtubedownloader.ui.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hasan.youtubedownloader.R
import com.hasan.youtubedownloader.databinding.FragmentCategoryDownloadBinding

class CategoryDownloadFragment : Fragment() {

    private var _binding : FragmentCategoryDownloadBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryDownloadBinding.inflate(inflater,container,false)



        return binding.root
    }

}