package com.astute.ai

import android.Manifest
import android.accessibilityservice.AccessibilityServiceInfo
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.astute.ai.ui.components.PlasmaOrbView
import com.astute.ai.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val audioGranted = permissions[Manifest.permission.RECORD_AUDIO] ?: false
        val overlayGranted = permissions[Manifest.permission.SYSTEM_ALERT_WINDOW] ?: false
        
        MainViewModel.updatePermissions(audioGranted, overlayGranted)
    }
    
    @SuppressLint("InlinedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Check initial permissions
        checkAndRequestPermissions()
        
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = Purple40,
                    secondary = PurpleGrey40,
                    tertiary = Pink40,
                    surface = Color(0xFF120024),
                    background = Color(0xFF120024)
                ),
                typography = Typography(
                    bodyLarge = androidx.compose.ui.text.TextStyle(
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
                        fontWeight = FontWeight.Normal,
                        fontSize = androidx.compose.ui.unit.sp.Sp
                    )
                )
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AstuteMainScreen(
                        onOpenOverlay = {
                            startActivity(Intent(this, ui.AssistantOverlayActivity::class.java))
                        },
                        onRequestPermissions = {
                            permissionLauncher.launch(getRequiredPermissions())
                        },
                        onOpenAccessibilitySettings = {
                            navigateToAccessibilitySettings()
                        },
                        onOpenAssistantSettings = {
                            navigateToAssistantSettings()
                        },
                        viewModel = viewModel()
                    )
                }
            }
        }
    }
    
    private fun checkAndRequestPermissions() {
        val requiredPermissions = getRequiredPermissions()
        val permissionsToRequest = mutableListOf<String>()
        
        for (permission in requiredPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission) 
                != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission)
            }
        }
        
        // Also check overlay permission separately
        if (!Settings.canDrawOverlays(this)) {
            // Will need separate flow for SYSTEM_ALERT_WINDOW
        }
        
        if (permissionsToRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }
    
    private fun getRequiredPermissions(): Array<String> {
        return arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.FOREGROUND_SERVICE_MICROPHONE
        )
    }
    
    @SuppressLint("InlinedApi")
    private fun navigateToAccessibilitySettings() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
    }
    
    @SuppressLint("InlinedApi")
    private fun navigateToAssistantSettings() {
        val intent = Intent(Settings.ACTION_VOICE_INPUT_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
    }
    
    companion object {
        fun hasAccessibilityPermission(context: Context): Boolean {
            val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
            val enabledServices = am.enabledAccessibilityServiceList
            return enabledServices.any { 
                it.resolveInfo.serviceInfo.packageName == context.packageName 
            }
        }
    }
}
