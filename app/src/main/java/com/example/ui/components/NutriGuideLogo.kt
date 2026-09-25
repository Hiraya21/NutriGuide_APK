package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R

/**
 * Official NutriGuide agricultural emblem logo component.
 * Displays the circular scalloped badge featuring the dairy/carabao silhouette,
 * rice/leaf seedling, and agricultural fertility arcs.
 * Supports smooth ambient breathing & subtle luminous aura.
 */
@Composable
fun NutriGuideLogo(
    size: Dp = 48.dp,
    modifier: Modifier = Modifier,
    elevation: Dp = 2.dp,
    showBorder: Boolean = true,
    borderColor: Color = Color(0xFF2E7D32),
    enableAmbientGlow: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "logo_ambient")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (enableAmbientGlow) 1.035f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val auraAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = if (enableAmbientGlow) 0.65f else 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura_alpha"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // Subtle ambient gold-green halo
        if (enableAmbientGlow) {
            Box(
                modifier = Modifier
                    .size(size * 1.15f)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFFD54F).copy(alpha = auraAlpha * 0.45f),
                                Color(0xFF4CAF50).copy(alpha = auraAlpha * 0.25f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        Box(
            modifier = Modifier
                .size(size)
                .scale(pulseScale)
                .shadow(elevation, CircleShape)
                .clip(CircleShape)
                .background(Color.White)
                .then(
                    if (showBorder) Modifier.border(1.5.dp, borderColor, CircleShape)
                    else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_nutriguide_logo),
                contentDescription = "NutriGuide Official Logo",
                modifier = Modifier.size(size),
                contentScale = ContentScale.Fit
            )
        }
    }
}
