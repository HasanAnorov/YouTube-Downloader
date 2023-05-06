package com.hasan.youtubedownloader.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hasan.youtubedownloader.databinding.LayoutDailogBinding

abstract class BaseDialog : BottomSheetDialogFragment() {

    lateinit var root: LayoutDailogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        root = LayoutDailogBinding.inflate(inflater, container, false)
        val content = getContent(inflater, container)
        root.layoutDialog.addView(content)
        return root.root
    }

    abstract fun getContent(inflater: LayoutInflater, container: ViewGroup?): View

}
