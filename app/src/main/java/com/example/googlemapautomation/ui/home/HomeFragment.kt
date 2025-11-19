package com.example.googlemapautomation.ui.home

import android.provider.Settings

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityManager
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.googlemapautomation.MyAccessibilityService
import com.example.googlemapautomation.automationStarted
import com.example.googlemapautomation.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.btnLaunch.setOnClickListener {

            automationStarted = false
          // openGoogleMaps()
            requestOverlayPermission()

        }



        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun requestOverlayPermission() {
        if (!Settings.canDrawOverlays(requireContext())) {
            AlertDialog.Builder(requireContext())
                .setTitle("Overlay Permission")
                .setMessage("Automation requires overlay permission to display prompts. Do you want to enable it?")
                .setPositiveButton("去开启") { dialog, _ ->
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:${requireContext().packageName}")
                    )
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    dialog.dismiss()
                }
                .setNegativeButton("取消") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()




        } else {
            // 权限已开启，可以直接启动 Google Maps
            goToAccessibilitySettingsIfDisabled()

        }
    }



    private fun openGoogleMaps() {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("geo:0,0?q=")   // Opens Google Maps
            // DO NOT use FLAG_ACTIVITY_NEW_TASK inside a Fragment
        }

            startActivity(intent)

    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val serviceName = "${requireContext().packageName}/${MyAccessibilityService::class.java.name}"
        val enabledServices = Settings.Secure.getString(
            requireContext().contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false

        return enabledServices.split(":").any { it.equals(serviceName, ignoreCase = true) }
    }

    private fun goToAccessibilitySettingsIfDisabled() {
        if (!isAccessibilityServiceEnabled()) {

            AlertDialog.Builder(requireContext())
                .setTitle("Accessibility Service Required")
                .setMessage("Please enable Accessibility Service to allow automation.")
                .setPositiveButton("Turn On") { dialog, _ ->
                    //  Accessibility
                    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()

        }else{
            openGoogleMaps()
        }

    }


}