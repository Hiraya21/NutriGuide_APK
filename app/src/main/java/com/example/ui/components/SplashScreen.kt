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
import androidx.compose.runtime.remember
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
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
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
 * High-craft opening splash animation centered entirely on the official NutriGuide emblem.
 *
 * Sequence of logo motifs:
 * 1. Scalloped Rosette Expansion: 16-lobe rosette blooming and rotating with spring physics.
 * 2. Stitched Inner Ring: Dashed perimeter circle tracing around the medallion.
 * 3. Radiating Furrow Arcs: 3 concentric curved waves pulsing out from the right side.
 * 4. Sprouting Leaf: Organic unfurl at the bottom-left of the seal.
 * 5. Carabao Emblem & Golden Shimmer: Full emblem reveals with diagonal light sweep.
 * 6. Brand Typography & Subtitle: "NutriGuide" display title and DA-PhilRice tags.
 */
@Composable
fun SplashScreen(
    onAnimationFinished: () -> Unit
) {
    // Interactive dismiss flag to prevent multiple triggers
    val interactionSource = remember { MutableInteractionSource() }

    // Core Animation States
    val rosetteScale = remember { Animatable(0.2f) }
    val rosetteRotation = remember { Animatable(-45f) }
    val rosetteAlpha = remember { Animatable(0f) }

    val stitchProgress = remember { Animatable(0f) }
    val furrowArc1 = remember { Animatable(0f) }
    val furrowArc2 = remember { Animatable(0f) }
    val furrowArc3 = remember { Animatable(0f) }
    val leafGrowth = remember { Animatable(0f) }

    val emblemAlpha = remember { Animatable(0f) }
    val emblemScale = remember { Animatable(0.7f) }
    val shimmerOffset = remember { Animatable(-300f) }

    val titleAlpha = remember { Animatable(0f) }
    val titleOffsetY = remember { Animatable(30f) }
    val subtitleAlpha = remember { Animatable(0f) }
    val skipAlpha = remember { Animatable(0f) }
    val screenFadeOut = remember { Animatable(1f) }

    // Infinite Ambient Transitions
    val infiniteTransition = rememberInfiniteTransition(label = "ambient_effects")

    // Slow ambient rotation of the outer scalloped aura
    val ambientSpin by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(28000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ambient_spin"
    )

    // Breathing pulse for the glowing aura
    val auraPulse by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura_pulse"
    )

    // Gentle floating particles offset
    val particlePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particle_phase"
    )

    // Master Animation Timeline
    LaunchedEffect(Unit) {
        // Allow user to see the skip affordance quickly
        launch {
            delay(500)
            skipAlpha.animateTo(1f, animationSpec = tween(400))
        }

        // Phase 1: Scalloped Rosette bloom & rotation (0ms - 550ms)
        launch {
            rosetteAlpha.animateTo(1f, animationSpec = tween(350, easing = FastOutSlowInEasing))
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

        // Phase 2: Stitched Ring tracing + Furrow arcs propagation (400ms - 900ms)
        launch {
            stitchProgress.animateTo(1f, animationSpec = tween(650, easing = FastOutSlowInEasing))
        }

        // Staggered furrow arcs (the 3 lines on the right of the logo)
        launch {
            delay(120)
            furrowArc1.animateTo(1f, animationSpec = tween(450, easing = FastOutSlowInEasing))
        }
        launch {
            delay(220)
            furrowArc2.animateTo(1f, animationSpec = tween(450, easing = FastOutSlowInEasing))
        }
        launch {
            delay(320)
            furrowArc3.animateTo(1f, animationSpec = tween(450, easing = FastOutSlowInEasing))
        }

        // Leaf sprout at bottom left
        launch {
            delay(200)
            leafGrowth.animateTo(
                1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
        }

        // Phase 3: High-Res Logo Medallion sharp reveal with shimmer sweep (700ms - 1300ms)
        launch {
            emblemAlpha.animateTo(1f, animationSpec = tween(400, easing = FastOutSlowInEasing))
        }
        launch {
            emblemScale.animateTo(
                1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }

        delay(400)
        // Shimmer glint sweeps diagonally across the emblem
        shimmerOffset.animateTo(350f, animationSpec = tween(700, easing = FastOutSlowInEasing))

        // Phase 4: Brand Typography entrance (1200ms - 1700ms)
        launch {
            titleAlpha.animateTo(1f, animationSpec = tween(500, easing = FastOutSlowInEasing))
        }
        launch {
            titleOffsetY.animateTo(0f, animationSpec = tween(500, easing = FastOutSlowInEasing))
        }
        launch {
            delay(200)
            subtitleAlpha.animateTo(1f, animationSpec = tween(500, easing = FastOutSlowInEasing))
        }

        // App holding phase for user appreciation
        delay(1300)

        // Phase 5: Smooth exit transition into main app
        screenFadeOut.animateTo(0f, animationSpec = tween(350, easing = FastOutSlowInEasing))
        onAnimationFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(screenFadeOut.value)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF07210E),
                        FarmGreenDark,
                        Color(0xFF1E5B28),
                        Color(0xFF081C0D)
                    )
                )
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { onAnimationFinished() }
            )
            .testTag("screen_splash"),
        contentAlignment = Alignment.Center
    ) {
        // Top Bar with Skip Button
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
                    .background(Color.White.copy(alpha = 0.12f))
                    .border(1.dp, Color.White.copy(alpha = 0.20f), RoundedCornerShape(20.dp))
                    .clickable { onAnimationFinished() }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Skip",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.size(13.dp)
                )
            }
        }

        // Background Floating Fertilizer & Soil Vitality Particles
        Canvas(modifier = Modifier.fillMaxSize()) {
            val particleList = listOf(
                Pair(0.20f, 0.30f) to Color(0xFFFFD54F),
                Pair(0.80f, 0.25f) to Color(0xFF81C784),
                Pair(0.15f, 0.70f) to Color(0xFFA5D6A7),
                Pair(0.85f, 0.65f) to Color(0xFFFFD54F),
                Pair(0.28f, 0.82f) to Color(0xFFC8E6C9),
                Pair(0.72f, 0.80f) to Color(0xFF81C784),
                Pair(0.50f, 0.18f) to Color(0xFFFFE082),
                Pair(0.50f, 0.88f) to Color(0xFFA5D6A7)
            )

            particleList.forEachIndexed { index, (pos, color) ->
                val (baseXRatio, baseYRatio) = pos
                val floatY = (baseYRatio - (particlePhase * 0.12f * ((index % 3) + 1))) % 1.0f
                val safeY = if (floatY < 0f) floatY + 1f else floatY
                val x = size.width * baseXRatio + sin((particlePhase * 2 * PI + index).toFloat()) * 12f
                val y = size.height * safeY
                val radius = (3.5f + (index % 3) * 1.5f)
                val alpha = (0.25f + sin((particlePhase * PI + index).toFloat()) * 0.25f).coerceIn(0.1f, 0.7f)

                drawCircle(
                    color = color.copy(alpha = alpha),
                    radius = radius,
                    center = Offset(x, y)
                )
            }
        }

        // Central Composition: Logo Construction & Typography
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            // === LOGO-BASED ANIMATED BADGE CONTAINER ===
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(240.dp)
                    .scale(rosetteScale.value)
                    .rotate(rosetteRotation.value)
                    .alpha(rosetteAlpha.value)
            ) {
                // 1. Ambient Pulsing Radial Aura
                Box(
                    modifier = Modifier
                        .size(230.dp)
                        .scale(auraPulse)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFFFD54F).copy(alpha = 0.35f),
                                    Color(0xFF4CAF50).copy(alpha = 0.22f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // 2. Decorative Sunburst Rays
                Canvas(
                    modifier = Modifier
                        .size(220.dp)
                        .rotate(ambientSpin)
                ) {
                    val rayCount = 16
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val maxRadius = size.minDimension / 2f
                    for (i in 0 until rayCount) {
                        val angle = (i * 360f / rayCount) * (PI.toFloat() / 180f)
                        val start = Offset(
                            center.x + (maxRadius * 0.82f) * cos(angle),
                            center.y + (maxRadius * 0.82f) * sin(angle)
                        )
                        val end = Offset(
                            center.x + maxRadius * cos(angle),
                            center.y + maxRadius * sin(angle)
                        )
                        drawLine(
                            color = Color(0xFFFFD54F).copy(alpha = 0.40f),
                            start = start,
                            end = end,
                            strokeWidth = 3f,
                            cap = StrokeCap.Round
                        )
                    }
                }

                // 3. Custom Canvas: Scalloped Rosette + Dashed Stitched Ring + 3 Furrow Arcs + Leaf Sprout
                Canvas(
                    modifier = Modifier.size(210.dp)
                ) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val baseRadius = size.minDimension / 2f - 8f

                    // --- DRAW 16-LOBED SCALLOPED BADGE ---
                    val lobes = 16
                    val scallopPath = Path()
                    val totalSteps = 240
                    for (step in 0..totalSteps) {
                        val theta = (step.toFloat() / totalSteps) * (2 * PI.toFloat())
                        // Modulation for 16 smooth scallop petals
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

                    // Fill Scalloped Rosette with rich organic green
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

                    // Scallop Golden Border Accent
                    drawPath(
                        path = scallopPath,
                        color = Color(0xFFFFD54F).copy(alpha = 0.75f),
                        style = Stroke(width = 2.5f)
                    )

                    // --- DRAW STITCHED INNER CIRCULAR RING ---
                    val stitchRadius = baseRadius * 0.77f
                    if (stitchProgress.value > 0f) {
                        drawArc(
                            color = Color(0xFFFAF7EE).copy(alpha = 0.90f),
                            startAngle = -90f,
                            sweepAngle = 360f * stitchProgress.value,
                            useCenter = false,
                            topLeft = Offset(center.x - stitchRadius, center.y - stitchRadius),
                            size = Size(stitchRadius * 2f, stitchRadius * 2f),
                            style = Stroke(
                                width = 2.2f,
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
                        drawArc(
                            color = Color(0xFF81C784).copy(alpha = 0.85f * furrowArc1.value),
                            startAngle = -35f,
                            sweepAngle = 70f * furrowArc1.value,
                            useCenter = false,
                            topLeft = Offset(furrowCenter.x - r1, furrowCenter.y - r1),
                            size = Size(r1 * 2f, r1 * 2f),
                            style = Stroke(width = 3.8f, cap = StrokeCap.Round)
                        )
                    }

                    // Arc 2 (Middle furrow wave)
                    if (furrowArc2.value > 0f) {
                        val r2 = baseRadius * 0.55f
                        drawArc(
                            color = Color(0xFFA5D6A7).copy(alpha = 0.90f * furrowArc2.value),
                            startAngle = -42f,
                            sweepAngle = 82f * furrowArc2.value,
                            useCenter = false,
                            topLeft = Offset(furrowCenter.x - r2, furrowCenter.y - r2),
                            size = Size(r2 * 2f, r2 * 2f),
                            style = Stroke(width = 4.2f, cap = StrokeCap.Round)
                        )
                    }

                    // Arc 3 (Outer furrow wave)
                    if (furrowArc3.value > 0f) {
                        val r3 = baseRadius * 0.67f
                        drawArc(
                            color = Color(0xFFFFD54F).copy(alpha = 0.95f * furrowArc3.value),
                            startAngle = -48f,
                            sweepAngle = 92f * furrowArc3.value,
                            useCenter = false,
                            topLeft = Offset(furrowCenter.x - r3, furrowCenter.y - r3),
                            size = Size(r3 * 2f, r3 * 2f),
                            style = Stroke(width = 4.6f, cap = StrokeCap.Round)
                        )
                    }

                    // --- DRAW DYNAMIC SPROUTING LEAF (Lower-left motif) ---
                    if (leafGrowth.value > 0f) {
                        val leafOrigin = Offset(center.x - baseRadius * 0.42f, center.y + baseRadius * 0.28f)
                        val scale = leafGrowth.value
                        val leafPath = Path().apply {
                            moveTo(leafOrigin.x, leafOrigin.y)
                            cubicTo(
                                leafOrigin.x - 22f * scale, leafOrigin.y - 10f * scale,
                                leafOrigin.x - 28f * scale, leafOrigin.y - 36f * scale,
                                leafOrigin.x - 12f * scale, leafOrigin.y - 50f * scale
                            )
                            cubicTo(
                                leafOrigin.x + 8f * scale, leafOrigin.y - 34f * scale,
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
                                end = Offset(leafOrigin.x - 12f * scale, leafOrigin.y - 50f * scale)
                            )
                        )
                        // Leaf central vein
                        drawLine(
                            color = Color.White.copy(alpha = 0.9f),
                            start = leafOrigin,
                            end = Offset(leafOrigin.x - 12f * scale, leafOrigin.y - 48f * scale),
                            strokeWidth = 1.8f,
                            cap = StrokeCap.Round
                        )
                    }
                }

                // 4. THE AUTHENTIC NUTRIGUIDE EMBLEM (Carabao, Leaf, Furrows, Scallop Seal)
                Box(
                    modifier = Modifier
                        .size(172.dp)
                        .scale(emblemScale.value)
                        .alpha(emblemAlpha.value)
                        .shadow(18.dp, CircleShape)
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
                            .size(166.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Fit
                    )

                    // 5. Diagonal Golden Shimmer / Gleam Sweep
                    Canvas(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val shimmerX = shimmerOffset.value
                        val shimmerBrush = Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = 0.45f),
                                Color(0xFFFFD54F).copy(alpha = 0.65f),
                                Color.White.copy(alpha = 0.45f),
                                Color.Transparent
                            ),
                            start = Offset(shimmerX - 60f, 0f),
                            end = Offset(shimmerX + 60f, size.height)
                        )
                        rotate(28f, pivot = center) {
                            drawRect(
                                brush = shimmerBrush,
                                topLeft = Offset(shimmerX - 80f, -size.height),
                                size = Size(160f, size.height * 3f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

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
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFFFD54F))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "PH",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1B5E20)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Official Scientific & Institutional Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White.copy(alpha = 0.15f))
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
                            modifier = Modifier.size(15.dp)
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

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "DA-PhilRice & RCEF Fertilizer Management",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.88f)
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Animated Status Pulse
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .alpha(subtitleAlpha.value)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black.copy(alpha = 0.28f))
                    .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Soil / Seed vitality glowing indicator
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF81C784))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Calibrating Soil Nutrients & Farm Maps...",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White.copy(alpha = 0.90f)
                )
            }
        }
    }
}
