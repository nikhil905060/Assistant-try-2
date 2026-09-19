package com.astute.ai.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.accessibilityservice.GestureDescription
import android.content.Intent
import android.graphics.Path
import android.os.Bundle
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class DeviceAutomationService : AccessibilityService() {
    
    companion object {
        var instance: DeviceAutomationService? = null
            private set
        
        /**
         * Launch an application by package name
         * @param packageName Full package name of the target app
         */
        fun launchApp(packageName: String) {
            instance?.let { service ->
                val launchIntent = service.packageManager.getLaunchIntentForPackage(packageName)
                if (launchIntent != null) {
                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                    service.startActivity(launchIntent)
                } else {
                    android.util.Log.e("DeviceAutomation", "No launch intent for: $packageName")
                }
            } ?: run {
                android.util.Log.e("DeviceAutomation", "Service instance not available")
            }
        }
        
        /**
         * Perform a tap gesture at specified coordinates
         */
        fun tapAt(x: Int, y: Int) {
            instance?.performGesture(
                GestureDescription.Builder().apply {
                    addStroke(
                        GestureDescription.StrokeDescription(
                            Path().apply {
                                moveTo(x.toFloat(), y.toFloat())
                            },
                            0L,
                            100L
                        )
                    )
                }.build()
            )
        }
        
        /**
         * Click a node by its ID
         */
        fun clickNodeId(nodeId: String?): Boolean {
            return instance?.getRootInActiveWindow()?.findAccessibilityNodeInfosByViewId(nodeId)
                ?.firstOrNull()
                ?.let { node ->
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    true
                } ?: false
        }
        
        /**
         * Send text input to currently focused field
         */
        fun sendText(text: String): Boolean {
            return instance?.getRootInActiveWindow()?.findFocus(AccessibilityNodeInfo.FOCUS_INPUT)
                ?.let { node ->
                    val bundle = Bundle().apply {
                        putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text)
                    }
                    node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, bundle)
                } ?: false
        }
        
        /**
         * Navigate back
         */
        fun goBack() {
            instance?.dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK))
            instance?.dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK))
        }
        
        /**
         * Navigate home
         */
        fun goHome() {
            instance?.dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HOME))
            instance?.dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HOME))
        }
        
        /**
         * Open recent apps
         */
        fun openRecentApps() {
            instance?.dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_APP_SWITCH))
            instance?.dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_APP_SWITCH))
        }
    }
    
    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        
        // Configure service capabilities
        info = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPES_ALL_MASK
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS or
                    AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS or
                    AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
            notificationTimeout = 100L
        }
        
        android.util.Log.d("DeviceAutomation", "Service connected successfully")
    }
    
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Process accessibility events if needed
        // Currently unused but kept for future automation features
    }
    
    override fun onInterrupt() {
        android.util.Log.d("DeviceAutomation", "Service interrupted")
    }
    
    override fun onDestroy() {
        super.onDestroy()
        instance = null
        android.util.Log.d("DeviceAutomation", "Service destroyed")
    }
    
    override fun onBind(intent: Intent?): android.os.IBinder? {
        return super.onBind(intent)
    }
}
