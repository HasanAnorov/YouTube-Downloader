package com.hasan.youtubedownloader.ui.screens

import android.Manifest
import android.R.color
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.work.*
import com.hasan.youtubedownloader.R
import com.hasan.youtubedownloader.databinding.FragmentHomeBinding
import com.hasan.youtubedownloader.models.ItemDownload
import com.hasan.youtubedownloader.utils.Constants.INITIAL
import com.hasan.youtubedownloader.utils.Constants.LIGHT
import com.hasan.youtubedownloader.utils.Constants.NIGHT
import com.hasan.youtubedownloader.utils.PreferenceHelper
import com.hasan.youtubedownloader.work.CommandWorker
import com.hasan.youtubedownloader.work.CommandWorker.Companion.commandKey


const val TAG = "HOME_FRAGMENT"

class HomeFragment : Fragment(){

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var window:Window

    //video link
    private var command: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        Log.d(TAG, "onCreateView: just")
        
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root


        val recyclerView = binding.recyclerView
        val adapter = HomeAdapter(arrayListOf<ItemDownload>(
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

        //drawer toggling
        binding.contentToolbar.btnMenu.setOnClickListener {
            try {
                findNavController().navigate(R.id.menuSelectDialog)
            } catch (e: Exception) {
                //Timber.e(e)
            }
        }

        window = requireActivity().window
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)

        when(PreferenceHelper.isLight(requireContext())){
            INITIAL ->{
                //setSystemTheme()
            }
            NIGHT ->{
                // clear FLAG_TRANSLUCENT_STATUS flag:
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
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

        binding.cardDownload.setOnClickListener {
            command = binding.etPasteLinkt.text.toString()
            if(isStoragePermissionGranted() && !command.isNullOrBlank()){
                startCommand(command!!)
            }
        }

        return view
    }

    private fun startCommand(command: String) {
        val workTag = CommandWorker.commandWorkTag
        val workManager = WorkManager.getInstance(activity?.applicationContext!!)
        val state = workManager.getWorkInfosByTag(workTag).get()?.getOrNull(0)?.state
        val running = state === WorkInfo.State.RUNNING || state === WorkInfo.State.ENQUEUED
        if (running) {
            Toast.makeText(
                context,
                R.string.command_is_already_running,
                Toast.LENGTH_LONG
            ).show()
            return
        }

        /** think about it later*/
        val workData = workDataOf(
            commandKey to command
        )
        val workRequest = OneTimeWorkRequestBuilder<CommandWorker>()
            .addTag(workTag)
            .setInputData(workData)
            .build()

        workManager.enqueueUniqueWork(
            workTag,
            ExistingWorkPolicy.KEEP,
            workRequest
        )
        Toast.makeText(
            context,
            R.string.command_queued,
            Toast.LENGTH_LONG
        ).show()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestNotificationPermission():Boolean{
        return if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.POST_NOTIFICATIONS  ),
                NOTIFICATION_REQUEST_CODE
            )
            false
        }
    }

    private fun isStoragePermissionGranted(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                STORAGE_REQUEST_CODE
            )
            false
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCommand(command!!)
        }
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
        //checking current UI theme mode
        when (requireContext().resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> {
                PreferenceHelper.setThemeMode(requireContext(),"initial_app_theme")
            }
            Configuration.UI_MODE_NIGHT_YES -> {
                PreferenceHelper.setThemeMode(requireContext(),"initial_app_theme")
                // clear FLAG_TRANSLUCENT_STATUS flag:
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                // finally change the color
                //window.decorView.systemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.statusBarNightColor)
                window.navigationBarColor = ContextCompat.getColor(requireContext(),R.color.statusBarNightColor)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val STORAGE_REQUEST_CODE = 1
        const val NOTIFICATION_REQUEST_CODE = 2
    }

}