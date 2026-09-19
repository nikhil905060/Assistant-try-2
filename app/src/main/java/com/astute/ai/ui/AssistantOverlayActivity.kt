package com.astute.ai.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.astute.ai.R
import com.astute.ai.service.DeviceAutomationService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class AssistantOverlayActivity : ComponentActivity() {
    
    private var isListening by mutableStateOf(false)
    private var recognitionStarted by mutableStateOf(false)
    private var recognitionCompleted by mutableStateOf(false)
    
    private val recordAudioPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startSpeechRecognition()
        } else {
            Toast.makeText(
                this,
                getString(R.string.permission_denied),
                Toast.LENGTH_LONG
            ).show()
            finish()
        }
    }
    
    private val speechRecognizerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val matches = result.data?.getStringArrayListExtra(
                RecognizerIntent.EXTRA_RESULTS
            )
            
            if (matches != null && matches.isNotEmpty()) {
                handleSpeechRecognition(matches.first())
            } else {
                Toast.makeText(
                    this,
                    "No speech detected",
                    Toast.LENGTH_SHORT
                ).show()
                resetState()
            }
        } else {
            Toast.makeText(
                this,
                "Speech recognition cancelled",
                Toast.LENGTH_SHORT
            ).show()
            resetState()
        }
    }
    
    @SuppressLint("InlinedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Configure window for overlay behavior
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        
        window.apply {
            addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_LAYOUT_INSCREEN or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
            
            decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            )
        }
        
        // Request audio recording permission
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.RECORD_AUDIO
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            startSpeechRecognition()
        } else {
            recordAudioPermissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
        }
        
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = Color(0xFF7B1FA2),
                    secondary = Color(0xFFBA68C8),
                    surface = Color(0xFF120024),
                    background = Color(0xFF120024)
                )
            ) {
                AssistantOverlayScreen(
                    isListening = isListening,
                    onPromptTapped = {
                        if (isListening) {
                            stopListening()
                        } else {
                            startSpeechRecognition()
                        }
                    },
                    onRetryClicked = {
                        resetState()
                        startSpeechRecognition()
                    }
                )
            }
        }
    }
    
    private fun startSpeechRecognition() {
        if (recognitionStarted) return
        
        isListening = true
        recognitionStarted = true
        
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault()
            )
            putExtra(
                RecognizerIntent.EXTRA_PARTIAL_RESULTS,
                true
            )
            putExtra(
                RecognizerIntent.EXTRA_MAX_RESULTS,
                1
            )
            putExtra(
                RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.overlay_title)
            )
            putExtra(
                RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS,
                1000
            )
            putExtra(
                RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,
                5000
            )
            putExtra(
                RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS,
                10000
            )
        }
        
        try {
            speechRecognizerLauncher.launch(intent)
            
            // Auto-timeout after 15 seconds
            lifecycleScope.launch {
                delay(15000)
                if (recognitionStarted && !recognitionCompleted) {
                    stopListening()
                    Toast.makeText(
                        this@AssistantOverlayActivity,
                        "Time out. Please try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                    resetState()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(
                this,
                "Speech recognition unavailable",
                Toast.LENGTH_SHORT
            ).show()
            resetState()
        }
    }
    
    private fun stopListening() {
        isListening = false
        recognitionStarted = false
        recognitionCompleted = false
    }
    
    private fun resetState() {
        isListening = false
        recognitionStarted = false
        recognitionCompleted = false
    }
    
    private fun handleSpeechRecognition(text: String) {
        recognitionCompleted = true
        isListening = false
        
        val lowerText = text.lowercase(Locale.getDefault())
        lifecycleScope.launch {
            when {
                lowerText.contains("youtube") || lowerText.contains("open youtube") -> {
                    Toast.makeText(
                        this@AssistantOverlayActivity,
                        getString(R.string.activity_opening, "YouTube"),
                        Toast.LENGTH_SHORT
                    ).show()
                    
                    DeviceAutomationService.instance?.launchApp("com.google.android.youtube")
                    delay(500)
                    finish()
                }
                
                lowerText.contains("settings") || lowerText.contains("open settings") -> {
                    Toast.makeText(
                        this@AssistantOverlayActivity,
                        getString(R.string.activity_opening, "Settings"),
                        Toast.LENGTH_SHORT
                    ).show()
                    
                    DeviceAutomationService.instance?.launchApp("com.android.settings")
                    delay(500)
                    finish()
                }
                
                lowerText.contains("maps") || lowerText.contains("open maps") -> {
                    Toast.makeText(
                        this@AssistantOverlayActivity,
                        getString(R.string.activity_opening, "Maps"),
                        Toast.LENGTH_SHORT
                    ).show()
                    
                    DeviceAutomationService.instance?.launchApp("com.google.android.apps.maps")
                    delay(500)
                    finish()
                }
                
                else -> {
                    Toast.makeText(
                        this@AssistantOverlayActivity,
                        "Command not recognized: $text",
                        Toast.LENGTH_LONG
                    ).show()
                    delay(2000)
                    resetState()
                }
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        resetState()
    }
    
    @Composable
    fun AssistantOverlayScreen(
        isListening: Boolean,
        onPromptTapped: () -> Unit,
        onRetryClicked: () -> Unit
    ) {
        val context = LocalContext.current
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF120024))
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { onPromptTapped() }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp)
            ) {
                // Glowing Plasma Orb
                PlasmaOrbView(isListening = isListening)
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Status Text
                Text(
                    text = when {
                        isListening -> context.getString(R.string.listening)
                        else -> context.getString(R.string.tap_to_speak)
                    },
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                SubtitleText(
                    text = context.getString(R.string.overlay_title),
                    color = Color(0xFFBA68C8)
                )
            }
            
            // Retry Button (shown when not listening)
            if (!isListening) {
                FloatingActionButton(
                    onClick = onRetryClicked,
                    containerColor = Color(0xFF7B1FA2),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = context.getString(R.string.retry),
                        tint = Color.White
                    )
                }
            }
        }
    }
    
    @Composable
    fun PlasmaOrbView(isListening: Boolean) {
        val infiniteTransition = rememberInfiniteTransition(label = "orbAnimation")
        
        val pulseScale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.2f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulseScale"
        )
        
        val glowAlpha by infiniteTransition.animateFloat(
            initialValue = 0.3f,
            targetValue = 0.8f,
            animationSpec = infiniteRepeatable(
                animation = tween(800, easing = LinearOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "glowAlpha"
        )
        
        val colorShift by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(if (isListening) 2000 else 4000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "colorShift"
        )
        
        Box(
            modifier = Modifier.size(200.dp),
            contentAlignment = Alignment.Center
        ) {
            // Outer Glow Ring
            Canvas(modifier = Modifier.size(200.dp)) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.Copy(0xFF7B1FA2).copy(alpha = glowAlpha),
                            Color.Copy(0xFF4A148C).copy(alpha = glowAlpha * 0.5f),
                            Color.Transparent
                        ),
                        radius = size.minDimension * 0.8f
                    ),
                    radius = size.minDimension * 0.5f
                )
            }
            
            // Pulsing Orb
            Canvas(modifier = Modifier.size((200 * pulseScale).dp)) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.9f),
                            Color.Copy(0xFFBA68C8),
                            Color.Copy(0xFF7B1FA2)
                        ),
                        center = Offset(size.width / 2, size.height / 2),
                        radius = size.minDimension / 2
                    ),
                    radius = size.minDimension / 2
                )
            }
            
            // Inner Core
            Canvas(modifier = Modifier.size((60 * pulseScale).dp)) {
                drawCircle(
                    color = Color.White,
                    radius = size.minDimension / 2
                )
            }
            
            // Listening Indicator Rings
            if (isListening) {
                val ringProgress by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(500, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "ringProgress"
                )
                
                Canvas(modifier = Modifier.size(250.dp)) {
                    drawArc(
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                Color.Copy(0xFF7B1FA2),
                                Color.Copy(0xFFBA68C8)
                            )
                        ),
                        startAngle = 0f + (ringProgress * 360f),
                        sweepAngle = 120f,
                        useCenter = false,
                        topLeft = Offset.Zero,
                        style = Stroke(width = 8.dp.toPx())
                    )
                }
            }
        }
    }
    
    @Composable
    fun SubtitleText(text: String, color: Color = Color.Gray) {
        Text(
            text = text,
            color = color,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}
