package com.hasan.youtubedownloader.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.work.*
import com.google.android.material.navigation.NavigationView
import com.hasan.youtubedownloader.R
import com.hasan.youtubedownloader.databinding.FragmentHomeBinding
import com.hasan.youtubedownloader.ui.adapters.HomeAdapter
import com.hasan.youtubedownloader.models.ItemDownload
import com.hasan.youtubedownloader.utils.PreferenceHelper
import com.hasan.youtubedownloader.work.CommandWorker
import com.hasan.youtubedownloader.work.CommandWorker.Companion.commandKey

const val TAG = "HOME_FRAGMENT"

class HomeFragment : Fragment(),NavigationView.OnNavigationItemSelectedListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var drawer:DrawerLayout
    private lateinit var navView:NavigationView
    private lateinit var switchDrawer:SwitchCompat
    private lateinit var window:Window

    //video link
    private var command: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        drawer = binding.drawerLayout
        navView = binding.navView
        navView.setNavigationItemSelectedListener(this)

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
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }else {
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        //handling switch on drawer
        val menuItem = binding.navView.menu.findItem(R.id.darkMode)
        switchDrawer = menuItem.actionView as SwitchCompat
        window = requireActivity().window

        when(PreferenceHelper.isLight(requireContext())){
            PreferenceHelper.INITIAL ->{
                setSystemTheme()
            }
            PreferenceHelper.DARK ->{
                // clear FLAG_TRANSLUCENT_STATUS flag:
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

                window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.statusBarNightColor)
                window.navigationBarColor = ContextCompat.getColor(requireContext(),R.color.statusBarNightColor)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                switchDrawer.isChecked = true
            }
            PreferenceHelper.LIGHT ->{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                switchDrawer.isChecked = false
            }
        }

        switchDrawer.setOnClickListener {
            if (switchDrawer.isChecked){
                //Toast.makeText(requireContext(), "true", Toast.LENGTH_SHORT).show()
                PreferenceHelper.setThemeMode(requireContext(),PreferenceHelper.DARK)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

            }else{
                //Toast.makeText(requireContext(), "false", Toast.LENGTH_SHORT).show()
                PreferenceHelper.setThemeMode(requireContext(),PreferenceHelper.LIGHT)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        //can be done in menu -> item, but use it for better experience
        findNavController().addOnDestinationChangedListener{_,destination,_ ->
            if (destination.id ==R.id.downloadsFragment){
                navView.menu.findItem(R.id.downloadsFragment).isCheckable = false
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
                PreferenceHelper.setThemeMode(requireContext(),PreferenceHelper.LIGHT)
                //Toast.makeText(requireContext(), "light", Toast.LENGTH_SHORT).show()
            }
            Configuration.UI_MODE_NIGHT_YES -> {
                PreferenceHelper.setThemeMode(requireContext(),PreferenceHelper.DARK)
                // clear FLAG_TRANSLUCENT_STATUS flag:
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                // finally change the color
                //window.decorView.systemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.statusBarNightColor)
                window.navigationBarColor = ContextCompat.getColor(requireContext(),R.color.statusBarNightColor)

                switchDrawer.isChecked = true
                //Toast.makeText(requireContext(), "dark", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.downloadsFragment ->{
                findNavController().navigate(R.id.downloadsFragment)
                drawer.closeDrawer(GravityCompat.START)
            }
            R.id.darkMode ->{
                //ignore this
            }
        }
        return true
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