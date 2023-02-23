package com.hasan.youtubedownloader.ui

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.navigation.NavigationView
import com.hasan.youtubedownloader.R
import com.hasan.youtubedownloader.databinding.FragmentHomeBinding
import com.hasan.youtubedownloader.utils.PreferenceHelper


const val TAG = "HOME_FRAGMENT"

class HomeFragment : Fragment(),NavigationView.OnNavigationItemSelectedListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var drawer:DrawerLayout
    private lateinit var navView:NavigationView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        drawer = binding.drawerLayout
        navView = binding.navView
        navView.setNavigationItemSelectedListener(this)

        //drawer toggling
        binding.contentToolbar.menu.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }else {
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        //handling switch on drawer
        val menuItem = binding.navView.menu.findItem(R.id.darkMode)
        val switchDrawer = menuItem.actionView as SwitchCompat
        val window: Window = requireActivity().window

        /** checking current UI theme mode
//        when (requireContext().resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
//            Configuration.UI_MODE_NIGHT_NO -> {
//                switchDrawer.isChecked = false
//            }
//            Configuration.UI_MODE_NIGHT_YES -> {
//                // clear FLAG_TRANSLUCENT_STATUS flag:
//                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
//                // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
//                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//                // finally change the color
//                //window.decorView.systemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//                window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.statusBarNightColor)
//                window.navigationBarColor = ContextCompat.getColor(requireContext(),R.color.statusBarNightColor)
//
//                switchDrawer.isChecked = true
//            }
//        }
        */

        if (PreferenceHelper.isLight(requireContext())){
            switchDrawer.isChecked = false
        }else{
            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

            window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.statusBarNightColor)
            window.navigationBarColor = ContextCompat.getColor(requireContext(),R.color.statusBarNightColor)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            switchDrawer.isChecked = true
        }

        //switchDrawer.isChecked = true
        switchDrawer.setOnClickListener {
            if (switchDrawer.isChecked){
                Toast.makeText(requireContext(), "true", Toast.LENGTH_SHORT).show()
                PreferenceHelper.setThemeMode(requireContext(),false)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

            }else{
                Toast.makeText(requireContext(), "false", Toast.LENGTH_SHORT).show()
                PreferenceHelper.setThemeMode(requireContext(),true)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        //can be done in menu -> item, but use it for better experience
        findNavController().addOnDestinationChangedListener{_,destination,_ ->
            if (destination.id ==R.id.storageFolderFragment){
                navView.menu.findItem(R.id.storageFolderFragment).isCheckable = false
            }
            if (destination.id ==R.id.categoryDownloadFragment){
                navView.menu.findItem(R.id.categoryDownloadFragment).isCheckable = false
            }
        }

        return view
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.storageFolderFragment ->{
                findNavController().navigate(R.id.action_homeFragment_to_storageFolderFragment)
                drawer.closeDrawer(GravityCompat.START)
            }
            R.id.categoryDownloadFragment ->{
                findNavController().navigate(R.id.action_homeFragment_to_categoryDownloadFragment)

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

}