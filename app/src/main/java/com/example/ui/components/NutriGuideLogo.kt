package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
 */
@Composable
fun NutriGuideLogo(
    size: Dp = 48.dp,
    modifier: Modifier = Modifier,
    elevation: Dp = 2.dp,
    showBorder: Boolean = true,
    borderColor: Color = Color(0xFF2E7D32)
) {
    Box(
        modifier = modifier
            .size(size)
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
