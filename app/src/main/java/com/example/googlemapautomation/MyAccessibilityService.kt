package com.example.googlemapautomation

import android.accessibilityservice.AccessibilityService
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.TextView
import android.widget.Toast
import com.example.googlemapautomation.ui.home.OverlayToast
import kotlinx.coroutines.*


var automationStarted = false
class MyAccessibilityService : AccessibilityService() {
    companion object {
        var instance: MyAccessibilityService? = null
            private set
    }


  //  private var automationStarted = false
    private val serviceScope = CoroutineScope(Dispatchers.Default)

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val pkg = event?.packageName?.toString() ?: return

        // If user left Maps → reset flag
        if (pkg != "com.google.android.apps.maps") {
            if (automationStarted) {
                Log.d("ACC", "User left Maps, resetting automation flag")
            }
            automationStarted = false
            return
        }

        // Trigger automation once per Maps session
        if (!automationStarted &&
            event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
        ) {
            automationStarted = true
            Log.d("ACC", "Automation START for this session")
            startAutomation()
        }

    }

    override fun onInterrupt() {}

    private fun startAutomation() {

        serviceScope.launch {

            delay(1200)

            // Refresh root each step
            var root = rootInActiveWindow ?: return@launch

            // 1. Click search bar
            clickSearchBar(root)

            delay(1000)
            root = rootInActiveWindow ?: return@launch

            // 2. Enter text
            inputText("KL Eco City")

            delay(1200)
            root = rootInActiveWindow ?: return@launch

            // 3. Click the result
            findAndClickContains(root, "KL Eco City - Menara 2")

            delay(2500)
            root = rootInActiveWindow ?: return@launch

            // 4. Click first photo
            findAndClickContains(root, "Photo")

            delay(2500)
            root = rootInActiveWindow ?: return@launch

            // 5. Get bottom photo details
            val details = getPhotoBottomText(root)
            showToast("Photo details: ${details ?: "empty"}")


        }
    }


    fun setAutomationStarted(value: Boolean) {
        automationStarted = value
    }

    // -------------------------
    // Helpers
    // -------------------------

    private fun clickSearchBar(root: AccessibilityNodeInfo): Boolean {
        // Try multiple known Maps selectors
        val keywords = listOf(
            "Search here",
            "Search",
            "Search for a place"
        )

        for (k in keywords) {
            if (findAndClick(root, k)) return true
        }

        // Try stable viewId
        val idNodes = root.findAccessibilityNodeInfosByViewId(
            "com.google.android.apps.maps:id/search_omnibox_text_box"
        )
        if (idNodes.isNotEmpty()) {
            idNodes[0].performAction(AccessibilityNodeInfo.ACTION_CLICK)
            return true
        }

        return false
    }

    private fun findAndClick(root: AccessibilityNodeInfo, exact: String): Boolean {
        val nodes = root.findAccessibilityNodeInfosByText(exact)
        for (n in nodes) {
            if (n.isClickable) {
                n.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                return true
            }
        }
        return false
    }

    private fun findAndClickContains(root: AccessibilityNodeInfo, text: String): Boolean {
        val nodes = root.findAccessibilityNodeInfosByText(text)
        for (node in nodes) {
            var cur: AccessibilityNodeInfo? = node
            while (cur != null) {
                if (cur.isClickable) {
                    cur.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    return true
                }
                cur = cur.parent
            }
        }
        return false
    }

    private fun inputText(text: String) {
        val args = Bundle().apply {
            putCharSequence(
                AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                text
            )
        }

        val root = rootInActiveWindow ?: return
        val editNodes = root.findAccessibilityNodeInfosByViewId(
            "com.google.android.apps.maps:id/search_omnibox_edit_text"
        )

        if (editNodes.isNotEmpty()) {
            editNodes[0].performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args)
        }
    }

    private fun getPhotoBottomText(root: AccessibilityNodeInfo): String? {
        val all = gatherAllNodes(root)
        return all
            .mapNotNull { it.text?.toString() }
            .firstOrNull { it.length > 5 } // bottom details typically longer
    }

    private fun gatherAllNodes(node: AccessibilityNodeInfo): List<AccessibilityNodeInfo> {
        val result = mutableListOf<AccessibilityNodeInfo>()
        fun dfs(n: AccessibilityNodeInfo?) {
            if (n == null) return
            result.add(n)
            for (i in 0 until n.childCount) dfs(n.getChild(i))
        }
        dfs(node)
        return result
    }

    private fun showToast(message: String) {
        Handler(mainLooper).post {
            Log.d("message toast picture bottom", message ?: "no message")
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            OverlayToast.show(this, message, 5000L)
        }
    }



}
