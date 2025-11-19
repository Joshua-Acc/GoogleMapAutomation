package com.example.googlemapautomation.ui.home



import android.content.Context
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.TextView
import androidx.core.content.ContextCompat

object OverlayToast {
    fun show(context: Context, message: String, duration: Long = 2000L) {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(android.R.layout.simple_list_item_1, null)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = message
        textView.setBackgroundColor(0xAA000000.toInt())
        textView.setTextColor(0xFFFFFFFF.toInt())
        textView.setPadding(30, 20, 30, 20)

        val background = GradientDrawable()
        background.cornerRadius = 50f   // 圆角半径
        background.setColor(0xAA000000.toInt())
        textView.background = background

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        params.y = 100 // 距离底部的偏移

        wm.addView(view, params)

        Handler(Looper.getMainLooper()).postDelayed({
            try {
                wm.removeView(view)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, duration)
    }
}
