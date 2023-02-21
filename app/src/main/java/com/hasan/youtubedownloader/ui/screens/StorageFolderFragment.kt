package com.hasan.youtubedownloader.ui.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit
import com.hasan.youtubedownloader.R
import com.hasan.youtubedownloader.databinding.FragmentStorageFolderBinding

class StorageFolderFragment : Fragment() {

    private var _binding : FragmentStorageFolderBinding? = null
    private val binding  get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStorageFolderBinding.inflate(inflater,container,false)


        return binding.root
    }

}