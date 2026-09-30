package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AppPlayIcon
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.YellowPrimary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashComplete: () -> Unit
) {
    var showLogo by remember { mutableStateOf(false) }
    val fullTagline = "Series Y novelas online gratis en ennovelas."
    var displayedCharsCount by remember { mutableIntStateOf(0) }
    var showCursor by remember { mutableStateOf(true) }

    val infiniteTransition = rememberInfiniteTransition(label = "splashPulse")
    val haloPulse by infiniteTransition.animateFloat(
        initialValue = 0.90f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "haloPulseAnim"
    )

    // Cursor blink transition
    LaunchedEffect(Unit) {
        while (true) {
            delay(400)
            showCursor = !showCursor
        }
    }

    // Sequence: Show logo -> Typewriter tagline animation -> Navigate Home
    LaunchedEffect(Unit) {
        delay(120)
        showLogo = true
        delay(400)

        // Typewriter character by character
        for (i in 1..fullTagline.length) {
            displayedCharsCount = i
            delay(42)
        }

        // Wait a moment so user can read the full typed tagline
        delay(700)
        onSplashComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .testTag("splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        // Soft Blue-Yellow Radial Halo
        Box(
            modifier = Modifier
                .size(320.dp)
                .scale(haloPulse)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            BluePrimary.copy(alpha = 0.08f),
                            YellowPrimary.copy(alpha = 0.04f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            // 1. New Rounded Box Play Icon with Yellow Border, Blue Fill & White Triangle
            AnimatedVisibility(
                visible = showLogo,
                enter = fadeIn(tween(600)) + scaleIn(tween(600), initialScale = 0.75f)
            ) {
                AppPlayIcon(
                    size = 100.dp,
                    backgroundColor = BluePrimary,
                    borderColor = YellowPrimary,
                    iconColor = Color.White,
                    cornerRadiusRatio = 0.28f,
                    modifier = Modifier.shadow(16.dp, shape = CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Brand Name: Ennovelas
            AnimatedVisibility(
                visible = showLogo,
                enter = fadeIn(tween(700))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "En",
                        color = BluePrimary,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.SansSerif,
                        letterSpacing = (-1).sp
                    )
                    Text(
                        text = "novelas",
                        color = YellowPrimary,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.SansSerif,
                        letterSpacing = (-1).sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Typewriter Animated Tagline
            val currentText = fullTagline.take(displayedCharsCount)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    text = currentText + if (displayedCharsCount < fullTagline.length && showCursor) "|" else "",
                    color = TextPrimary,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.SansSerif,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            }
        }

        // Clean subtle Version Tag
        Text(
            text = "Ennovelas v1.0.0",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 36.dp),
            fontSize = 11.5.sp,
            color = TextMuted,
            fontWeight = FontWeight.Medium
        )
    }
}
