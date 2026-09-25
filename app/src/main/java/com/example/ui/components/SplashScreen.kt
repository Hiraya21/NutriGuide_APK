package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.FarmGreenDark
import com.example.ui.theme.FarmGreenPrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Ultra-smooth cinematic opening animation based on the official NutriGuide Emblem Logo.
 *
 * Sequence of visual elements:
 * 1. Deep Field Auroral Background with drifting organic light gradients
 * 2. 16-Lobed Scalloped Rosette Seal blooming with elastic spring physics
 * 3. Central Emblem Landing with expanding agricultural shockwave ripples
 * 4. Stitched perimeter tracing + 3 pulsing radar soil furrow arcs
 * 5. Organic leaf seedling unfurling at lower left
 * 6. Specular lens-flare gleam sweep across the medallion
 * 7. Bioluminescent nutrient spores floating upward
 * 8. Kinetic display typography and system calibration bar
 * 9. Interactive tap ripple and instant skip affordance
 */
@Composable
fun SplashScreen(
    onAnimationFinished: () -> Unit
) {
    // Interactive dismiss flag to prevent multiple triggers
    var isFinished by remember { mutableStateOf(false) }
    fun finish() {
        if (!isFinished) {
            isFinished = true
            onAnimationFinished()
        }
    }

    val interactionSource = remember { MutableInteractionSource() }

    // Core Animation States
    val rosetteScale = remember { Animatable(0.15f) }
    val rosetteRotation = remember { Animatable(-60f) }
    val rosetteAlpha = remember { Animatable(0f) }

    // Shockwave pulse from center
    val shockwaveScale = remember { Animatable(0.2f) }
    val shockwaveAlpha = remember { Animatable(0f) }

    // Logo internal elements
    val stitchProgress = remember { Animatable(0f) }
    val furrowArc1 = remember { Animatable(0f) }
    val furrowArc2 = remember { Animatable(0f) }
    val furrowArc3 = remember { Animatable(0f) }
    val leafGrowth = remember { Animatable(0f) }

    val emblemAlpha = remember { Animatable(0f) }
    val emblemScale = remember { Animatable(0.5f) }
    val emblemElevation = remember { Animatable(0f) }
    val shimmerOffset = remember { Animatable(-350f) }

    val titleAlpha = remember { Animatable(0f) }
    val titleOffsetY = remember { Animatable(36f) }
    val badgeScale = remember { Animatable(0f) }
    val subtitleAlpha = remember { Animatable(0f) }
    val progressValue = remember { Animatable(0f) }
    val skipAlpha = remember { Animatable(0f) }
    val screenFadeOut = remember { Animatable(1f) }

    // Tap Ripple effect coordinates
    var tapRippleCenter by remember { mutableStateOf<Offset?>(null) }
    val tapRippleRadius = remember { Animatable(0f) }
    val tapRippleAlpha = remember { Animatable(0f) }

    // Infinite Ambient Transitions
    val infiniteTransition = rememberInfiniteTransition(label = "ambient_effects")

    // Slow ambient rotation of the outer scalloped aura
    val ambientSpin by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(32000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ambient_spin"
    )

    // Breathing pulse for the glowing aura
    val auraPulse by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura_pulse"
    )

    // Radar pulse wave loop for furrow arcs
    val radarPulse by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "radar_pulse"
    )

    // Gentle floating particles phase
    val particlePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particle_phase"
    )

    // Auroral glow drift
    val auroraDrift by infiniteTransition.animateFloat(
        initialValue = -50f,
        targetValue = 50f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aurora_drift"
    )

    // Master Cinematic Animation Timeline
    LaunchedEffect(Unit) {
        // Allow user to see the skip affordance quickly
        launch {
            delay(400)
            skipAlpha.animateTo(1f, animationSpec = tween(350))
        }

        // Phase 1: Scalloped Rosette bloom & rotation (0ms - 500ms)
        launch {
            rosetteAlpha.animateTo(1f, animationSpec = tween(300, easing = FastOutSlowInEasing))
        }
        launch {
            rosetteRotation.animateTo(
                targetValue = 0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
        rosetteScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )

        // Center shockwave ripple when medallion strikes
        launch {
            delay(280)
            shockwaveAlpha.snapTo(0.85f)
            launch {
                shockwaveScale.animateTo(2.4f, animationSpec = tween(900, easing = FastOutSlowInEasing))
            }
            shockwaveAlpha.animateTo(0f, animationSpec = tween(900, easing = FastOutSlowInEasing))
        }

        // Phase 2: Stitched Ring tracing + Furrow arcs propagation (350ms - 850ms)
        launch {
            delay(150)
            stitchProgress.animateTo(1f, animationSpec = tween(650, easing = FastOutSlowInEasing))
        }

        // Staggered furrow arcs (3 lines on the right of the logo)
        launch {
            delay(250)
            furrowArc1.animateTo(1f, animationSpec = tween(400, easing = FastOutSlowInEasing))
        }
        launch {
            delay(350)
            furrowArc2.animateTo(1f, animationSpec = tween(400, easing = FastOutSlowInEasing))
        }
        launch {
            delay(450)
            furrowArc3.animateTo(1f, animationSpec = tween(400, easing = FastOutSlowInEasing))
        }

        // Leaf sprout at bottom left
        launch {
            delay(300)
            leafGrowth.animateTo(
                1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }

        // Phase 3: High-Res Logo Medallion sharp reveal with specular shimmer sweep (450ms - 1100ms)
        launch {
            delay(200)
            emblemAlpha.animateTo(1f, animationSpec = tween(350, easing = FastOutSlowInEasing))
        }
        launch {
            delay(200)
            emblemElevation.animateTo(24f, animationSpec = tween(500))
            emblemScale.animateTo(
                1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }

        delay(600)
        // Shimmer glint sweeps diagonally across the emblem
        launch {
            shimmerOffset.animateTo(450f, animationSpec = tween(750, easing = FastOutSlowInEasing))
        }

        // Phase 4: Brand Typography entrance + Calibration progress (800ms - 1500ms)
        launch {
            titleAlpha.animateTo(1f, animationSpec = tween(450, easing = FastOutSlowInEasing))
        }
        launch {
            titleOffsetY.animateTo(0f, animationSpec = tween(450, easing = FastOutSlowInEasing))
        }
        launch {
            delay(150)
            badgeScale.animateTo(
                1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        }
        launch {
            delay(250)
            subtitleAlpha.animateTo(1f, animationSpec = tween(450, easing = FastOutSlowInEasing))
        }
        launch {
            delay(300)
            progressValue.animateTo(1f, animationSpec = tween(1200, easing = FastOutSlowInEasing))
        }

        // App holding phase for appreciation
        delay(1400)

        // Phase 5: Smooth exit transition into main app
        screenFadeOut.animateTo(0f, animationSpec = tween(350, easing = FastOutSlowInEasing))
        finish()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(screenFadeOut.value)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF051D0B),
                        FarmGreenDark,
                        Color(0xFF1E5B28),
                        Color(0xFF061A0A)
                    )
                )
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { offset ->
                        tapRippleCenter = offset
                        // Trigger rapid exit on intentional tap
                        finish()
                    }
                )
            }
            .testTag("screen_splash"),
        contentAlignment = Alignment.Center
    ) {
        // Ambient Organic Auroral Gradients in Background
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)

            // Upper Emerald Light Blob
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF43A047).copy(alpha = 0.28f),
                        Color.Transparent
                    ),
                    center = Offset(center.x + auroraDrift * 1.2f, size.height * 0.25f),
                    radius = size.width * 0.7f
                ),
                radius = size.width * 0.7f,
                center = Offset(center.x + auroraDrift * 1.2f, size.height * 0.25f)
            )

            // Lower Golden Nutrient Glow Blob
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFD54F).copy(alpha = 0.22f),
                        Color.Transparent
                    ),
                    center = Offset(center.x - auroraDrift, size.height * 0.72f),
                    radius = size.width * 0.65f
                ),
                radius = size.width * 0.65f,
                center = Offset(center.x - auroraDrift, size.height * 0.72f)
            )
        }

        // Top Navigation with Skip Button
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(16.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .alpha(skipAlpha.value)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.14f))
                    .border(1.dp, Color(0xFFFFD54F).copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = { finish() }
                    )
                    .padding(horizontal = 14.dp, vertical = 7.dp)
            ) {
                Text(
                    text = "Skip",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.95f),
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(5.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Skip splash",
                    tint = Color(0xFFFFD54F),
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        // Bioluminescent Floating Nutrient Spores (16 particles)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val particleSpecs = listOf(
                Triple(0.18f, 0.24f, Color(0xFFFFD54F)),
                Triple(0.82f, 0.20f, Color(0xFF81C784)),
                Triple(0.12f, 0.68f, Color(0xFFA5D6A7)),
                Triple(0.88f, 0.62f, Color(0xFFFFD54F)),
                Triple(0.24f, 0.85f, Color(0xFFC8E6C9)),
                Triple(0.76f, 0.82f, Color(0xFF81C784)),
                Triple(0.50f, 0.14f, Color(0xFFFFE082)),
                Triple(0.50f, 0.90f, Color(0xFFA5D6A7)),
                Triple(0.35f, 0.40f, Color(0xFFFFD54F)),
                Triple(0.65f, 0.42f, Color(0xFF81C784)),
                Triple(0.08f, 0.48f, Color(0xFFA5D6A7)),
                Triple(0.92f, 0.46f, Color(0xFFFFE082)),
                Triple(0.30f, 0.70f, Color(0xFFC8E6C9)),
                Triple(0.70f, 0.68f, Color(0xFF81C784)),
                Triple(0.42f, 0.82f, Color(0xFFFFD54F)),
                Triple(0.58f, 0.22f, Color(0xFFA5D6A7))
            )

            particleSpecs.forEachIndexed { index, (xRatio, yRatio, color) ->
                val floatY = (yRatio - (particlePhase * 0.14f * ((index % 4) + 1))) % 1.0f
                val safeY = if (floatY < 0f) floatY + 1f else floatY
                val x = size.width * xRatio + sin((particlePhase * 2 * PI + index * 0.5f).toFloat()) * 16f
                val y = size.height * safeY
                val radius = (3f + (index % 3) * 1.5f)
                val alpha = (0.3f + sin((particlePhase * PI + index).toFloat()) * 0.35f).coerceIn(0.15f, 0.85f)

                // Particle glow aura
                drawCircle(
                    color = color.copy(alpha = alpha * 0.35f),
                    radius = radius * 2.2f,
                    center = Offset(x, y)
                )
                // Particle bright core
                drawCircle(
                    color = Color.White.copy(alpha = alpha),
                    radius = radius * 0.6f,
                    center = Offset(x, y)
                )
            }
        }

        // Center Composition: Emblem Construction & Typography
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            // === LOGO-BASED ANIMATED EMBLEM CONTAINER ===
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(240.dp)
                    .scale(rosetteScale.value)
                    .rotate(rosetteRotation.value)
                    .alpha(rosetteAlpha.value)
            ) {
                // Expanding Shockwave Wave Rings
                Canvas(modifier = Modifier.fillMaxSize()) {
                    if (shockwaveAlpha.value > 0f) {
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val r = (size.minDimension / 2f) * shockwaveScale.value
                        drawCircle(
                            color = Color(0xFFFFD54F).copy(alpha = shockwaveAlpha.value * 0.7f),
                            radius = r,
                            center = center,
                            style = Stroke(width = 3.5f)
                        )
                        drawCircle(
                            color = Color(0xFF81C784).copy(alpha = shockwaveAlpha.value * 0.4f),
                            radius = r * 0.85f,
                            center = center,
                            style = Stroke(width = 2f)
                        )
                    }
                }

                // 1. Ambient Pulsing Radial Aura
                Box(
                    modifier = Modifier
                        .size(230.dp)
                        .scale(auraPulse)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFFFD54F).copy(alpha = 0.38f),
                                    Color(0xFF4CAF50).copy(alpha = 0.24f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // 2. Dual Counter-Rotating Sunburst Rays
                Canvas(
                    modifier = Modifier
                        .size(224.dp)
                        .rotate(ambientSpin)
                ) {
                    val rayCount = 16
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val maxRadius = size.minDimension / 2f
                    for (i in 0 until rayCount) {
                        val angle = (i * 360f / rayCount) * (PI.toFloat() / 180f)
                        val start = Offset(
                            center.x + (maxRadius * 0.84f) * cos(angle),
                            center.y + (maxRadius * 0.84f) * sin(angle)
                        )
                        val end = Offset(
                            center.x + maxRadius * cos(angle),
                            center.y + maxRadius * sin(angle)
                        )
                        drawLine(
                            color = Color(0xFFFFD54F).copy(alpha = 0.45f),
                            start = start,
                            end = end,
                            strokeWidth = 3f,
                            cap = StrokeCap.Round
                        )
                    }
                }

                // 3. Custom Canvas: 16-Lobed Rosette + Dashed Stitched Ring + 3 Furrow Arcs + Leaf Sprout
                Canvas(
                    modifier = Modifier.size(210.dp)
                ) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val baseRadius = size.minDimension / 2f - 6f

                    // --- DRAW 16-LOBED SCALLOPED BADGE ---
                    val lobes = 16
                    val scallopPath = Path()
                    val totalSteps = 240
                    for (step in 0..totalSteps) {
                        val theta = (step.toFloat() / totalSteps) * (2 * PI.toFloat())
                        // Smooth sinusoidal scalloped rim
                        val r = baseRadius * 0.90f + (baseRadius * 0.10f) * cos(lobes * theta)
                        val px = center.x + r * cos(theta)
                        val py = center.y + r * sin(theta)
                        if (step == 0) {
                            scallopPath.moveTo(px, py)
                        } else {
                            scallopPath.lineTo(px, py)
                        }
                    }
                    scallopPath.close()

                    // Fill Rosette with lush agricultural gradient
                    drawPath(
                        path = scallopPath,
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF388E3C),
                                Color(0xFF2E7D32),
                                Color(0xFF1B5E20)
                            ),
                            center = center,
                            radius = baseRadius
                        )
                    )

                    // Scallop Golden Contour Stroke
                    drawPath(
                        path = scallopPath,
                        color = Color(0xFFFFD54F).copy(alpha = 0.80f),
                        style = Stroke(width = 2.5f)
                    )

                    // --- DRAW STITCHED INNER CIRCULAR RING ---
                    val stitchRadius = baseRadius * 0.77f
                    if (stitchProgress.value > 0f) {
                        drawArc(
                            color = Color(0xFFFAF7EE).copy(alpha = 0.92f),
                            startAngle = -90f,
                            sweepAngle = 360f * stitchProgress.value,
                            useCenter = false,
                            topLeft = Offset(center.x - stitchRadius, center.y - stitchRadius),
                            size = Size(stitchRadius * 2f, stitchRadius * 2f),
                            style = Stroke(
                                width = 2.4f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f),
                                cap = StrokeCap.Round
                            )
                        )
                    }

                    // --- DRAW 3 CONCENTRIC SOIL FURROW / NUTRITION ARCS (Right side of logo) ---
                    val furrowCenter = Offset(center.x + baseRadius * 0.05f, center.y)

                    // Arc 1 (Inner furrow wave)
                    if (furrowArc1.value > 0f) {
                        val r1 = baseRadius * 0.44f
                        val arcAlpha = (furrowArc1.value * radarPulse).coerceIn(0.4f, 1f)
                        drawArc(
                            color = Color(0xFF81C784).copy(alpha = arcAlpha),
                            startAngle = -35f,
                            sweepAngle = 70f * furrowArc1.value,
                            useCenter = false,
                            topLeft = Offset(furrowCenter.x - r1, furrowCenter.y - r1),
                            size = Size(r1 * 2f, r1 * 2f),
                            style = Stroke(width = 4.0f, cap = StrokeCap.Round)
                        )
                    }

                    // Arc 2 (Middle furrow wave)
                    if (furrowArc2.value > 0f) {
                        val r2 = baseRadius * 0.55f
                        val arcAlpha = (furrowArc2.value * radarPulse).coerceIn(0.4f, 1f)
                        drawArc(
                            color = Color(0xFFA5D6A7).copy(alpha = arcAlpha),
                            startAngle = -42f,
                            sweepAngle = 82f * furrowArc2.value,
                            useCenter = false,
                            topLeft = Offset(furrowCenter.x - r2, furrowCenter.y - r2),
                            size = Size(r2 * 2f, r2 * 2f),
                            style = Stroke(width = 4.4f, cap = StrokeCap.Round)
                        )
                    }

                    // Arc 3 (Outer furrow wave)
                    if (furrowArc3.value > 0f) {
                        val r3 = baseRadius * 0.67f
                        val arcAlpha = (furrowArc3.value * radarPulse).coerceIn(0.5f, 1f)
                        drawArc(
                            color = Color(0xFFFFD54F).copy(alpha = arcAlpha),
                            startAngle = -48f,
                            sweepAngle = 92f * furrowArc3.value,
                            useCenter = false,
                            topLeft = Offset(furrowCenter.x - r3, furrowCenter.y - r3),
                            size = Size(r3 * 2f, r3 * 2f),
                            style = Stroke(width = 4.8f, cap = StrokeCap.Round)
                        )
                    }

                    // --- DRAW DYNAMIC SPROUTING LEAF (Lower-left motif) ---
                    if (leafGrowth.value > 0f) {
                        val leafOrigin = Offset(center.x - baseRadius * 0.42f, center.y + baseRadius * 0.28f)
                        val scale = leafGrowth.value
                        val leafPath = Path().apply {
                            moveTo(leafOrigin.x, leafOrigin.y)
                            cubicTo(
                                leafOrigin.x - 24f * scale, leafOrigin.y - 10f * scale,
                                leafOrigin.x - 30f * scale, leafOrigin.y - 38f * scale,
                                leafOrigin.x - 14f * scale, leafOrigin.y - 52f * scale
                            )
                            cubicTo(
                                leafOrigin.x + 8f * scale, leafOrigin.y - 36f * scale,
                                leafOrigin.x + 2f * scale, leafOrigin.y - 12f * scale,
                                leafOrigin.x, leafOrigin.y
                            )
                            close()
                        }
                        drawPath(
                            path = leafPath,
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFF81C784), Color(0xFF2E7D32)),
                                start = Offset(leafOrigin.x, leafOrigin.y),
                                end = Offset(leafOrigin.x - 14f * scale, leafOrigin.y - 52f * scale)
                            )
                        )
                        // Leaf central vein
                        drawLine(
                            color = Color.White.copy(alpha = 0.92f),
                            start = leafOrigin,
                            end = Offset(leafOrigin.x - 14f * scale, leafOrigin.y - 50f * scale),
                            strokeWidth = 2.0f,
                            cap = StrokeCap.Round
                        )
                    }
                }

                // 4. THE AUTHENTIC NUTRIGUIDE EMBLEM (Carabao Medallion Disc)
                Box(
                    modifier = Modifier
                        .size(174.dp)
                        .scale(emblemScale.value)
                        .alpha(emblemAlpha.value)
                        .shadow(emblemElevation.value.dp, CircleShape)
                        .clip(CircleShape)
                        .background(Color(0xFFFAF7EE))
                        .border(3.dp, Color(0xFF2E7D32), CircleShape)
                        .border(6.dp, Color(0xFFFFD54F).copy(alpha = 0.85f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_nutriguide_logo),
                        contentDescription = "NutriGuide Official Logo",
                        modifier = Modifier
                            .size(168.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Fit
                    )

                    // 5. Specular Shimmer / Lens Flare Sweep
                    Canvas(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val shimmerX = shimmerOffset.value
                        val shimmerBrush = Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = 0.45f),
                                Color(0xFFFFD54F).copy(alpha = 0.70f),
                                Color.White.copy(alpha = 0.45f),
                                Color.Transparent
                            ),
                            start = Offset(shimmerX - 70f, 0f),
                            end = Offset(shimmerX + 70f, size.height)
                        )
                        rotate(30f, pivot = center) {
                            drawRect(
                                brush = shimmerBrush,
                                topLeft = Offset(shimmerX - 90f, -size.height),
                                size = Size(180f, size.height * 3f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // === BRAND TYPOGRAPHY & IDENTITY ===
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .alpha(titleAlpha.value)
                    .offset { IntOffset(0, titleOffsetY.value.roundToInt()) }
            ) {
                // App Title with letter spacing and golden gleam
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "NutriGuide",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 1.6.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .scale(badgeScale.value)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(Color(0xFFFFD54F), Color(0xFFFFB300))
                                )
                            )
                            .shadow(4.dp, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "PH",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF1B5E20)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Official Scientific & Institutional Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White.copy(alpha = 0.16f))
                        .border(1.2.dp, Color(0xFFFFD54F).copy(alpha = 0.60f), RoundedCornerShape(24.dp))
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Agriculture,
                            contentDescription = null,
                            tint = Color(0xFFFFD54F),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Precision Agriculture • Soil Nutrition",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD54F),
                            letterSpacing = 0.4.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "DA-PhilRice & RCEF Fertilizer Management",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.90f)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Animated Agricultural Calibration Progress Bar
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .alpha(subtitleAlpha.value)
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {
                // Progress track
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color.White.copy(alpha = 0.18f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progressValue.value)
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF81C784),
                                        Color(0xFFFFD54F),
                                        Color(0xFFA5D6A7)
                                    )
                                )
                            )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Calibration Status Text
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFD54F))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (progressValue.value < 0.6f) "Calibrating Soil Nutrients..." else "Ready for Precision Farming",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.White.copy(alpha = 0.88f)
                    )
                }
            }
        }
    }
}
