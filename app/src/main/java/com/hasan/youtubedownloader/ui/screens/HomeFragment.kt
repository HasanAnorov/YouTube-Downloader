package com.hasan.youtubedownloader.ui.screens

import android.Manifest
import android.content.res.Configuration
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.Settings
import android.provider.Settings.Global
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.hasan.youtubedownloader.R
import com.hasan.youtubedownloader.databinding.FragmentHomeBinding
import com.hasan.youtubedownloader.models.ItemDownload
import com.hasan.youtubedownloader.ui.menu.LoadingDialog
import com.hasan.youtubedownloader.utils.Constants.INITIAL
import com.hasan.youtubedownloader.utils.Constants.LIGHT
import com.hasan.youtubedownloader.utils.Constants.NIGHT
import com.hasan.youtubedownloader.utils.PreferenceHelper
import com.hasan.youtubedownloader.utils.toast
import com.techiness.progressdialoglibrary.ProgressDialog
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLException
import com.yausername.youtubedl_android.YoutubeDLRequest
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

const val TAG = "ahi3646"

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var window: Window
    private lateinit var windowInsetsController: WindowInsetsControllerCompat
    private lateinit var dialog:LoadingDialog

    private var urlCommand = ""
    private var isDownloading :Boolean = false

    private val permission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            startDownload(urlCommand)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            YoutubeDL.getInstance().init(requireContext())
        } catch (e: YoutubeDLException) {
            Log.e(TAG, "failed to initialize youtubedl-android", e)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        window = requireActivity().window
        windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)

        when (PreferenceHelper.isLight(requireContext())) {
            INITIAL -> {
                //setSystemTheme()
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

        binding.contentToolbar.btnMenu.setOnClickListener {
            try {
                findNavController().navigate(R.id.menuSelectDialog)
            } catch (e: Exception) {
                //Timber.e(e)
            }
        }

        binding.cardDownload.setOnClickListener {
            urlCommand = binding.etPasteLinkt.text.toString().trim()
            if (urlCommand.isBlank()) {
                toast("Enter link to download!")
            } else {
                permission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }

        return binding.root
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun showStart(progress:Int){
        GlobalScope.launch(Dispatchers.Main) {
            if (progress<0){
                dialog.setContent(0)
            }else{
                dialog.setContent(progress)
            }
            dialog.show()
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun startDownload(command: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val request = YoutubeDLRequest(command)
            request.addOption(
                "-o", getDownloadLocation().absolutePath + "/%(title)s.%(ext)s"
            )
            YoutubeDL.getInstance().execute(request, "taskId") { progressP, _, line ->
                showStart(progressP.toInt())
                Log.d(TAG, "getProgress - ${id.hashCode()} , ${progressP.toInt()}, $line.")
                if (progressP.toInt() == 100){
                    closeDialog()
                }
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun closeDialog(){
        GlobalScope.launch(Dispatchers.Main) {
            dialog.dismiss()
            Toast.makeText(requireContext(), "Video successfully downloaded!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getDownloadLocation(): File {
        val downloadsDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val youtubeDlDir = File(downloadsDir, "hasan_YT_android")
        if (!youtubeDlDir.exists()) {
            youtubeDlDir.mkdir()
        }
        return youtubeDlDir
    }

    private fun setSystemTheme() {
        when (requireContext().resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> {
                //PreferenceHelper.setThemeMode(requireContext(),"initial_app_theme")
                window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
                windowInsetsController.isAppearanceLightStatusBars = true
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            Configuration.UI_MODE_NIGHT_YES -> {
                //PreferenceHelper.setThemeMode(requireContext(),"initial_app_theme")
                // clear FLAG_TRANSLUCENT_STATUS flag:
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                // finally change the color
                //window.decorView.systemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
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