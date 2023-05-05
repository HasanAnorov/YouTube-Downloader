package com.hasan.youtubedownloader.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
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
import com.yausername.youtubedl_android.YoutubeDL.getInstance
import com.yausername.youtubedl_android.YoutubeDLException
import com.yausername.youtubedl_android.YoutubeDLRequest
import java.io.File
import kotlin.math.log


const val TAG = "HOME_FRAGMENT"

class HomeFragment : Fragment(){

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var window: Window
    private lateinit var windowInsetsController : WindowInsetsControllerCompat

    private var urlCommand = ""
    //private var isDownloading :Boolean = false

    private val permission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            startDownload(urlCommand)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            getInstance().init(requireContext())
        } catch (e: YoutubeDLException) {
            Log.e(TAG, "failed to initialize youtubedl-android", e)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        window = requireActivity().window
        windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)

        when(PreferenceHelper.isLight(requireContext())){
            INITIAL ->{
                //setSystemTheme()
            }
            NIGHT ->{

                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

                //can't change drawableStart tint color
//                binding.etPasteLinkt.compoundDrawables[0].setTint(resources.getColor(R.color.white_max))
//                binding.etPasteLinkt.compoundDrawables.filterNotNull().forEach {
//                    it.mutate()
//                    it.setTint(resources.getColor(R.color.white))
//                }

                windowInsetsController.isAppearanceLightStatusBars = false
                window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.black_dark)
                window.navigationBarColor = ContextCompat.getColor(requireContext(),R.color.black_dark)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            LIGHT ->{
                window.statusBarColor = ContextCompat.getColor(requireContext(),R.color.white)
                windowInsetsController.isAppearanceLightStatusBars = true
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        val recyclerView = binding.recyclerView
        val adapter = HomeAdapter(arrayListOf(
            ItemDownload(R.drawable.images,"1"),
            ItemDownload(R.drawable.images,"2"),
            ItemDownload(R.drawable.images,"3"),
            ItemDownload(R.drawable.images,"4"),
            ItemDownload(R.drawable.images,"5"),
            ItemDownload(R.drawable.images,"6")
        )){ itemDownload, image ->

            ViewCompat.setTransitionName(image,"item_image")
            val extras = FragmentNavigatorExtras(image to "hero_image")
            //why is that ?
            val bundle = Bundle()
            bundle.putInt("image",itemDownload.image)
            findNavController().navigate(R.id.action_homeFragment_to_playFragment,
            bundle,
            null,
            extras)
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

            if(urlCommand.isBlank()){
                toast("Enter link to download. Empty url blank!")
            }else{
                permission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }

        }

        return binding.root
    }

    private fun startDownload(command: String) {

        val dialog = LoadingDialog(requireContext())
        dialog.setContent("0")
        dialog.show()

        //val progressDialog = ProgressDialog(ProgressDialog.MODE_DETERMINATE,requireContext())

        Log.d(TAG, "startDownload: $command")
        val request = YoutubeDLRequest(command)
        request.addOption(
            "-o",
            getDownloadLocation().absolutePath + "/%(title)s.%(ext)s")

        getInstance()
            .execute(request, "taskId") { percentage, _, line ->
                activity?.runOnUiThread {
                    dialog.setContent("${percentage.toInt()}")
                    Log.d(TAG, "startDownload: ${id.hashCode()} , ${percentage.toInt()}, $line")
                    //if(progress.toInt() == 100) dialog.dismiss()

//                    with(progressDialog)
//                    {
//                        theme = ProgressDialog.THEME_LIGHT
//                        mode = ProgressDialog.MODE_DETERMINATE
//                        progress = percentage.toInt()
//                        showProgressTextAsFraction(true)
//                        setNegativeButton("Cancel","Determinate",null)
//                        show()
//                    }

                }
            }

        //isDownloading = true

        /**
        val request = YoutubeDLRequest(command)
        val youtubeDlDirection = getDownloadLocation()
        val config = File(youtubeDlDirection, "config.txt")

//        if (useConfigFile.isChecked() && config.exists()) {
//            request.addOption("--config-location", config.absolutePath)
//        } else {
            request.addOption("--no-mtime")
            request.addOption("--downloader", "libaria2c.so")
            request.addOption("--external-downloader-args", "aria2c:\"--summary-interval=1\"")
            request.addOption("-f", "bestvideo[ext=mp4]+bestaudio[ext=m4a]/best[ext=mp4]/best")
            request.addOption("-o", youtubeDlDirection.absolutePath + "/%(title)s.%(ext)s")
//        }

        */

        /*** Background worker code
//        val workTag = CommandWorker.commandWorkTag
//        val workManager = WorkManager.getInstance(activity?.applicationContext!!)
//        val workData = workDataOf(
//            commandKey to command
//        )
//
//        val downloadRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
//            .addTag(workTag)
//            .setInputData(workData)
//            .build()
//            workManager.enqueueUniqueWork(workTag,ExistingWorkPolicy.KEEP,downloadRequest)


//        val state = workManager.getWorkInfosByTag(workTag).get()?.getOrNull(0)?.state
//        val running = state === WorkInfo.State.RUNNING || state === WorkInfo.State.ENQUEUED
//        if (running) {
//            Toast.makeText(
//                context,
//                R.string.command_is_already_running,
//                Toast.LENGTH_LONG
//            ).show()
//            return
//        }
*/

    }

    private fun getDownloadLocation():File{
        val downloadsDir = Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val youtubeDlDir = File(downloadsDir,"hasan_YT_android")
        if (!youtubeDlDir.exists()){
            youtubeDlDir.mkdir()
        }
        return youtubeDlDir
    }

    /**
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        postponeEnterTransition()
//        (view.parent as? ViewGroup)?.doOnPreDraw {
//            startPostponedEnterTransition()
//        }
//    }
    */

    private fun setSystemTheme(){
        when (requireContext().resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> {
                //PreferenceHelper.setThemeMode(requireContext(),"initial_app_theme")
                window.statusBarColor = ContextCompat.getColor(requireContext(),R.color.white)
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
                window.navigationBarColor = ContextCompat.getColor(requireContext(),R.color.black_dark)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}