package com.hasan.youtubedownloader.ui.home

import android.Manifest
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
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
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.hasan.youtubedownloader.R
import com.hasan.youtubedownloader.databinding.FragmentHomeBinding
import com.hasan.youtubedownloader.models.ItemDownload
import com.hasan.youtubedownloader.ui.IntentViewModel
import com.hasan.youtubedownloader.ui.home.menu_download.DialogForResultCallback
import com.hasan.youtubedownloader.ui.home.menu_download.MenuDownload
import com.hasan.youtubedownloader.utils.Constants.LIGHT
import com.hasan.youtubedownloader.utils.Constants.NIGHT
import com.hasan.youtubedownloader.utils.Constants.SYSTEM
import com.hasan.youtubedownloader.utils.DebouncingOnClickListener
import com.hasan.youtubedownloader.utils.PreferenceHelper
import com.hasan.youtubedownloader.utils.Resource
import com.hasan.youtubedownloader.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val TAG = "ahi3646"

@AndroidEntryPoint
class HomeFragment : Fragment(), DialogForResultCallback {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var window: Window
    private lateinit var windowInsetsController: WindowInsetsControllerCompat

    private var urlCommand = ""

    private val viewModel: IntentViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by viewModels()

    private lateinit var dialog: LoadingDialog

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

        val downloads = arrayListOf(
            ItemDownload(R.drawable.images, "1"),
            ItemDownload(R.drawable.images, "2"),
            ItemDownload(R.drawable.images, "3"),
            ItemDownload(R.drawable.images, "4"),
            ItemDownload(R.drawable.images, "5"),
            ItemDownload(R.drawable.images, "6")
        )

        val downloadsCount = downloads.size.toString() + " " + resources.getString(R.string.files)
        binding.downloadsCount.text = downloadsCount
        val recyclerView = binding.recyclerView
        val adapter = HomeAdapter(downloads) { itemDownload, image ->

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
            arrayOf(intArrayOf()), intArrayOf(
                android.R.color.transparent
            )
        )

        val lightRipple = ColorStateList(
            arrayOf(intArrayOf()), intArrayOf(
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
            } else {
                permission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE  )
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog = LoadingDialog(requireContext())

        dialog.findViewById<Button>(R.id.cancel_loading).setOnClickListener {
            lifecycleScope.launch {
                homeViewModel.cancelDownload("taskId")
                dialog.dismiss()
            }
        }

        dialog.findViewById<Button>(R.id.hide_loading).setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun startDownload(link: String, format: String) {
        Log.d(TAG, "startDownload: $format")
        dialog.setContent("Please wait  0 %")
        dialog.show()

        lifecycleScope.launch(Dispatchers.IO) {
            homeViewModel.startDownload(link, format, lifecycle)
        }

        homeViewModel.progress.observe(viewLifecycleOwner) { progress ->
            if (progress.toInt() == 100) {
                onComplete()
            } else {
                updateDialog(progress.toInt())
            }
        }
    }

    private fun updateDialog(progress: Int) {
        if (progress > 0) dialog.setContent("Please wait  $progress %")
    }

    private fun onComplete() {
        homeViewModel.onComplete()
        lifecycleScope.launch {
            dialog.setContent("Video successfully downloaded!")
            delay(1000)
            dialog.dismiss()
        }
    }

    private fun prepareForDownload() {
        Log.d(TAG, "prepareForDownload: ")
        val fadeIn: Animation = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)

        val transparentRipple = ColorStateList(
            arrayOf(intArrayOf()), intArrayOf(
                android.R.color.transparent
            )
        )

        val lightRipple = ColorStateList(
            arrayOf(intArrayOf()), intArrayOf(
                resources.getColor(R.color.rippleColor)
            )
        )

        binding.cardDownload.isClickable = false

        binding.progressBar.animation = fadeIn
        binding.progressBar.visibility = View.VISIBLE
        binding.infoText.animation = fadeIn
        binding.infoText.visibility = View.VISIBLE

        binding.etPasteLinkt.visibility = View.GONE
        binding.cardClear.visibility = View.GONE
        binding.ivDownload.visibility = View.GONE

        binding.cardDownload.isClickable = false
        binding.cardDownload.rippleColor = transparentRipple

        lifecycleScope.launch(Dispatchers.IO) {
            when (val formats = homeViewModel.getFormats(urlCommand)) {
                is Resource.Loading -> {
                    lifecycleScope.launch(Dispatchers.Main) {
                        Log.d(TAG, "prepareForDownload: load")
                        binding.infoText.text = resources.getString(R.string.loading)
                    }
                }

                is Resource.Success -> {
                    lifecycleScope.launch(Dispatchers.Main) {
                        Log.d(TAG, "prepareForDownload: success")
                        binding.progressBar.visibility = View.GONE
                        binding.infoText.text = resources.getString(R.string.success)

                        delay(500)
                        binding.infoText.visibility = View.GONE
                        binding.infoText.text = resources.getString(R.string.please_wait)

                        binding.cardClear.animation = fadeIn
                        binding.cardClear.visibility = View.VISIBLE
                        binding.etPasteLinkt.animation = fadeIn
                        binding.etPasteLinkt.visibility = View.VISIBLE
                        binding.ivDownload.animation = fadeIn
                        binding.ivDownload.visibility = View.VISIBLE

                        binding.cardDownload.isClickable = true
                        binding.cardDownload.rippleColor = lightRipple

//                        bundle.putStringArrayList("formats", formats.data)
//                        bundle.putString("link", urlCommand)
//                        findNavController().navigate(R.id.menuDownload, bundle)
                        MenuDownload(this@HomeFragment, formats.data!!).show(
                            childFragmentManager,
                            "ahi3646"
                        )
                    }
                }

                is Resource.DataError -> {
                    lifecycleScope.launch(Dispatchers.Main) {
                        Log.d(TAG, "prepareForDownload: error")
                        binding.progressBar.visibility = View.GONE
                        binding.infoText.text = formats.errorMessage.toString()

                        delay(2500)
                        binding.infoText.visibility = View.GONE
                        binding.infoText.text = resources.getString(R.string.please_wait)

                        binding.cardClear.animation = fadeIn
                        binding.cardClear.visibility = View.VISIBLE
                        binding.etPasteLinkt.animation = fadeIn
                        binding.etPasteLinkt.visibility = View.VISIBLE
                        binding.ivDownload.animation = fadeIn
                        binding.ivDownload.visibility = View.VISIBLE

                        binding.cardDownload.isClickable = true
                        binding.cardDownload.rippleColor = lightRipple
                    }
                }
            }
        }
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

    override fun onResultSuccess(videoFormat: String) {
        //start download
        Log.d(TAG, "onResultSuccess: $videoFormat")
        startDownload(urlCommand, videoFormat)
    }

    override fun onResultFailed(ex: Exception) {
        Log.d(TAG, "onResultFailed: ${ex.message}")
        toast("Something went wrong !")
    }

}
