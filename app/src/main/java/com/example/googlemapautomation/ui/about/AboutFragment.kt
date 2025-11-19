package com.example.googlemapautomation.ui.about

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.googlemapautomation.databinding.FragmentAboutBinding


class AboutFragment : Fragment() {

    private var _binding: FragmentAboutBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(AboutViewModel::class.java)

        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textContent
//        notificationsViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        textView.text = "Joshua Coding Assessment:\n" +
                "Build a simple Android Kotlin app to automate simple actions on Google Maps.\n" +
                "Must use the following minimal frameworks:\n" +
                "1. Accessibility Services for automation\n" +
                "2. (Optional) Kotlin coroutines for handling delays\n\n" +
                "App to contain following functions:\n" +
                "1. A button to launch the Google Maps app as a new task\n" +
                "2. Once launched, automate tap on search bar on top\n" +
                "3. Input \"KL Eco City\" to search bar\n" +
                "4. Tap on the first result which CONTAINS \"KL Eco City - Menara 2\"\n" +
                "5. Once directed to the details page, tap on the first photo to maximize it\n" +
                "6. In the full screen photo, capture the text in the photo details on the bottom and show it in a Toast\n" +
                "7. Refer to the video for demo flow"

// 关键设置
        textView.gravity = Gravity.START  // 左对齐文字
        textView.textAlignment = TextView.TEXT_ALIGNMENT_VIEW_START // 确保多行左对齐
        textView.isSingleLine = false     // 多行显示
        textView.setLineSpacing(4f, 1.0f) // 调整行间距

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}