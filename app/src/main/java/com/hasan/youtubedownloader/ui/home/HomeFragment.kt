package com.hasan.youtubedownloader.ui.home

import android.Manifest
import android.content.ClipDescription.MIMETYPE_TEXT_PLAIN
import android.content.ClipboardManager
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.hasan.youtubedownloader.R
import com.hasan.youtubedownloader.data.YoutubeRepository
import com.hasan.youtubedownloader.databinding.FragmentHomeBinding
import com.hasan.youtubedownloader.models.ItemDownload
import com.hasan.youtubedownloader.ui.IntentViewModel
import com.hasan.youtubedownloader.utils.Constants.LIGHT
import com.hasan.youtubedownloader.utils.Constants.NIGHT
import com.hasan.youtubedownloader.utils.Constants.SYSTEM
import com.hasan.youtubedownloader.utils.DebouncingOnClickListener
import com.hasan.youtubedownloader.utils.PreferenceHelper
import com.hasan.youtubedownloader.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


const val TAG = "ahi3646"

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var window: Window
    private lateinit var windowInsetsController: WindowInsetsControllerCompat

    private var urlCommand = ""

    private val viewModel: IntentViewModel by activityViewModels()

    @Inject
    lateinit var repository: YoutubeRepository

    private val permission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                prepareForDownload()
            } else {
                Toast.makeText(requireContext(), "Permission denied !", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        window = requireActivity().window
        windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)

        when (PreferenceHelper.isLight(requireContext())) {
            SYSTEM -> {
                setSystemTheme()
            }

            NIGHT -> {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

                windowInsetsController.isAppearanceLightStatusBars = false
                window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.black_dark)
                window.navigationBarColor =
                    ContextCompat.getColor(requireContext(), R.color.black_dark)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }

            LIGHT -> {
                window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
                windowInsetsController.isAppearanceLightStatusBars = true
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        val recyclerView = binding.recyclerView
        val adapter = HomeAdapter(
            arrayListOf(
                ItemDownload(R.drawable.images, "1"),
                ItemDownload(R.drawable.images, "2"),
                ItemDownload(R.drawable.images, "3"),
                ItemDownload(R.drawable.images, "4"),
                ItemDownload(R.drawable.images, "5"),
                ItemDownload(R.drawable.images, "6")
            )
        ) { itemDownload, image ->

            ViewCompat.setTransitionName(image, "item_image")
            val extras = FragmentNavigatorExtras(image to "hero_image")
            //why is that ?
            val bundle = Bundle()
            bundle.putInt("image", itemDownload.image)
            findNavController().navigate(
                R.id.action_homeFragment_to_playFragment, bundle, null, extras
            )
        }

        recyclerView.adapter = adapter

        val transparentRipple = ColorStateList(
            arrayOf<IntArray>(intArrayOf()), intArrayOf(
                android.R.color.transparent
            )
        )

        val lightRipple = ColorStateList(
            arrayOf<IntArray>(intArrayOf()), intArrayOf(
                resources.getColor(R.color.rippleColor)
            )
        )

        binding.etPasteLinkt.doOnTextChanged { text, start, before, count ->
            if (start == 0 && count == 0) {
                binding.clearText.setImageResource(R.drawable.iv_search)
                binding.cardClear.rippleColor = transparentRipple
            } else {
                binding.cardClear.rippleColor = lightRipple
                binding.clearText.setImageResource(R.drawable.cancel)
                binding.cardClear.setOnClickListener {
                    binding.etPasteLinkt.text?.clear()
                    binding.cardClear.rippleColor = transparentRipple
                }
            }
        }

        viewModel.externalLink.observe(viewLifecycleOwner) { link ->
            urlCommand = link
            permission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        binding.contentToolbar.btnMenu.setOnClickListener(DebouncingOnClickListener {
            try {
                findNavController().navigate(R.id.menuSelectDialog)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }
        })

        binding.cardDownload.setOnClickListener {
            urlCommand = binding.etPasteLinkt.text.toString().trim()
            if (urlCommand.isBlank()) {
                toast("Enter link to download!")
            }
//            else if (urlCommand.isNotBlank() && !urlCommand.isEmailValid()){
//                toast("Enter valid link !")
//            }
            else {
                binding.cardDownload.rippleColor = transparentRipple
                val progressButton = ProgressButton(requireContext(),binding.cardSearch)
                progressButton.buttonActivated()

                permission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
        return binding.root
    }

    private fun prepareForDownload() {
        val bundle = Bundle()
        bundle.putString("link", urlCommand)
        findNavController().navigate(R.id.menuDownload,bundle)
    }

    private fun setSystemTheme() {
        when (requireContext().resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
                windowInsetsController.isAppearanceLightStatusBars = true
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            Configuration.UI_MODE_NIGHT_YES -> {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                windowInsetsController.isAppearanceLightStatusBars = false
                window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.black_dark)
                window.navigationBarColor =
                    ContextCompat.getColor(requireContext(), R.color.black_dark)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
