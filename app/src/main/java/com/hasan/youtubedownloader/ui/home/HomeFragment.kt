package com.hasan.youtubedownloader.ui.home

import android.Manifest
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.*
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
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

const val TAG = "ahi3646"

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var window: Window
    private lateinit var windowInsetsController: WindowInsetsControllerCompat
    private lateinit var dialog: LoadingDialog

    private var urlCommand = ""
    private var isDownloading: Boolean = false

    private val viewModel: IntentViewModel by activityViewModels()

    @Inject
    lateinit var repository: YoutubeRepository

    private val permission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                if (isDownloading) dialog.show() else startDownload(urlCommand)
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

        dialog = LoadingDialog(requireContext())

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

        binding.etPasteLinkt.doOnTextChanged { text, start, before, count ->
            if (start == 0 && count == 0) {
                binding.cardClear.isClickable = false
                binding.cardClear.isFocusable = false
                binding.clearText.setImageResource(R.drawable.iv_search)
            } else {
                binding.cardClear.isClickable = true
                binding.cardClear.isFocusable = true
                binding.clearText.setImageResource(R.drawable.cancel)
                binding.cardClear.setOnClickListener {
                    binding.etPasteLinkt.text?.clear()
                }
            }
        }

        viewModel.externalLink.observe(viewLifecycleOwner) { link ->
            urlCommand = link
            permission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            Log.d(TAG, "onCreated: $link")
        }

        binding.contentToolbar.btnMenu.setOnClickListener(DebouncingOnClickListener {
            try {
                findNavController().navigate(R.id.menuSelectDialog)
            } catch (e: Exception) {
                //Timber.e(e)
            }
        })

        binding.cardDownload.setOnClickListener {
            urlCommand = binding.etPasteLinkt.text.toString().trim()
            if (urlCommand.isBlank()) {
                toast("Enter link to download!")
            } else {
                permission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }

        dialog.findViewById<Button>(R.id.cancel_loading).setOnClickListener {
            lifecycleScope.launch {
                cancelDownload("taskId")
            }
            isDownloading = false
        }

        dialog.findViewById<Button>(R.id.hide_loading).setOnClickListener {
            dialog.dismiss()
        }

        return binding.root
    }

    private fun showStart(progress: Int) {
        dialog.setContent(if (progress < 0) "Please wait  0" else "Please wait  $progress")
    }

    private fun startDownload(command: String) {
        dialog.setContent("Please wait  0")
        dialog.show()

        isDownloading = true


        repository.startDownload(command)
            .flowWithLifecycle(lifecycle)
            .onEach { progress ->
                showStart(progress.toInt())
                Log.d(TAG, "getProgress -  ${progress}")
                if (progress.toInt() == 100) {
                    isDownloading = false
                    closeDialog()
                }
            }
            .launchIn(lifecycleScope)
    }

    private fun cancelDownload(processId: String) {
        Log.d(TAG, "cancelDownload: ")
        repository.cancelDownload(processId)
        dialog.dismiss()
    }

    private fun closeDialog() {
        dialog.dismiss()
        Toast
            .makeText(requireContext(), "Video successfully downloaded!", Toast.LENGTH_SHORT)
            .show()
    }

    private fun setSystemTheme() {
        when (requireContext().resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> {
                //PreferenceHelper.setThemeMode(requireContext(),"initial_app_theme")
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
