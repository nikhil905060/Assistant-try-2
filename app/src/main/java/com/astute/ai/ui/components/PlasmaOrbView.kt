@file:OptIn(ExperimentalFoundationApi::class)

package com.astute.ai.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import kotlinx.coroutines.delay

/**
 * Animated Plasma Orb Component for Astute AI Assistant
 * Displays a pulsing purple orb that indicates listening state
 */
@Composable
fun PlasmaOrbView(
    isListening: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "plasmaOrb")
    
    // Scale animation for pulsing effect
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isListening) 1.3f else 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (isListening) 600 else 1200,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scaleAnimation"
    )
    
    // Alpha/glow intensity animation
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = if (isListening) 1f else 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 800,
                easing = LinearOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alphaAnimation"
    )
    
    // Rotation for spiral effect when listening
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (isListening) 1500 else 5000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotationAnimation"
    )
    
    // Gradient shift for color variation
    val gradientPosition by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2000,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradientAnimation"
    )
    
    Box(
        modifier = modifier
            .size(180.dp)
            .blur(x = 12.dp, y = 12.dp)
    ) {
        // Outer glow halo
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF7B1FA2).copy(alpha = alpha * 0.5f),
                            Color(0xFF4A148C).copy(alpha = alpha * 0.3f),
                            Color.Transparent
                        ),
                        center = androidx.compose.ui.geometry.Offset(90.dp.value, 90.dp.value),
                        radius = LocalDensity.current.run { 90.dp.toPx() }
                    )
                )
                .animateContentSize()
        )
        
        // Main orb body with gradient
        Box(
            modifier = Modifier
                .size(100.dp * scale)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = alpha),
                            Color(0xFFE1BEE7).copy(alpha = alpha),
                            Color(0xFFBA68C8).copy(alpha = alpha),
                            Color(0xFF7B1FA2).copy(alpha = alpha)
                        ),
                        center = androidx.compose.ui.geometry.Offset(50.dp.value, 50.dp.value),
                        radius = LocalDensity.current.run { 50.dp.toPx() }
                    )
                )
                .then(
                    if (isListening) {
                        Modifier
                            .rotate(rotation)
                    } else {
                        Modifier
                    }
                ),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            // Inner bright core
            Box(
                modifier = Modifier
                    .size(40.dp * scale)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White,
                                Color(0xFFBA68C8)
                            ),
                            radius = LocalDensity.current.run { 20.dp.toPx() }
                        ),
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
            )
        }
    }
}
