package com.hasan.youtubedownloader.ui.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.hasan.youtubedownloader.base.BaseDialog
import com.hasan.youtubedownloader.databinding.ThemeSelectDialogBinding
import com.hasan.youtubedownloader.utils.Constants.INITIAL
import com.hasan.youtubedownloader.utils.Constants.LIGHT
import com.hasan.youtubedownloader.utils.Constants.NIGHT
import com.hasan.youtubedownloader.utils.PreferenceHelper
import com.hasan.youtubedownloader.utils.gone
import com.hasan.youtubedownloader.utils.visible

class ThemeSelectDialog : BaseDialog() {

    private var _content:ThemeSelectDialogBinding? = null
    private val content get() = _content!!

    private val viewModel by viewModels<ThemeViewModel>()

    private var mode = "initial_app_theme"

    override fun getContent(inflater: LayoutInflater, container: ViewGroup?): View {
        _content = ThemeSelectDialogBinding.inflate(inflater,container,false)
        return content.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onDark(PreferenceHelper.isLight(requireContext()))

    }

    private fun onDark(model: String) {
        mode = model
        when(model){
            NIGHT ->{
                content.imageViewNight.visible()
                content.imageViewLight.gone()
                content.imageViewDefault.gone()
            }
            LIGHT ->{
                content.imageViewNight.gone()
                content.imageViewLight.visible()
                content.imageViewDefault.gone()
            }
            INITIAL ->{
                content.imageViewNight.gone()
                content.imageViewLight.gone()
                content.imageViewDefault.visible()
            }
        }

        content.cardViewDefault.setOnClickListener {
            try {
                PreferenceHelper.setThemeMode(requireContext(), INITIAL)
                //viewModel.setDarkMode(-1)
//                activity?.recreate()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                //Timber.e("Default: $mode")
                findNavController().popBackStack()
            } catch (e: Exception) {
                //Timber.e(e)
            }
        }

        content.cardViewLight.setOnClickListener {
            try {
                // why is that - if (mode == 1 || mode == -1){
                PreferenceHelper.setThemeMode(requireContext(), LIGHT)
                    //viewModel.setDarkMode(0)
//                    activity?.recreate()
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    //Timber.e("Light: $mode")
                //}
                findNavController().popBackStack()
            } catch (e: Exception) {
                //Timber.e(e)
            }
        }

        content.cardViewNight.setOnClickListener {
            try {
                //why os that - if (mode == 0 || mode == -1){
                PreferenceHelper.setThemeMode(requireContext(), NIGHT)
                    //viewModel.setDarkMode(1)
//                    activity?.recreate()
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    //Timber.e("Night: $mode")
                //}
                findNavController().popBackStack()
            } catch (e: Exception) {
                //Timber.e(e)
            }
        }

    }

}