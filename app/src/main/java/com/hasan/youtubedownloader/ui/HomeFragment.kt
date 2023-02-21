package com.hasan.youtubedownloader.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentController
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView
import com.hasan.youtubedownloader.R
import com.hasan.youtubedownloader.databinding.FragmentHomeBinding

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

        val menuItem = binding.navView.menu.findItem(R.id.darkMode)
        val switchDrawer = menuItem.actionView as SwitchCompat
        switchDrawer.isChecked = true
        switchDrawer.setOnClickListener {
            Toast.makeText(
                requireContext(),
                if (switchDrawer.isChecked) "is checked!!!" else "not checked!!!",
                Toast.LENGTH_SHORT
            ).show()
        }

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
//                val switch_id = item.actionView as SwitchCompat
//                switch_id.isChecked = true
//                switch_id.setOnClickListener {
//                    Toast.makeText(
//                        requireContext(),
//                        if (switch_id.isChecked) "is checked!!!" else "not checked!!!",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
            }
//            else ->{
//                val switch_id = item.actionView as SwitchCompat
//                switch_id.isChecked = true
//                switch_id.setOnClickListener {
//                    Toast.makeText(
//                        requireContext(),
//                        if (switch_id.isChecked) "is checked!!!" else "not checked!!!",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}