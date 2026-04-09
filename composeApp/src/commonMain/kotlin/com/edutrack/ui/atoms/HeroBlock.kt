package com.edutrack.ui.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// ─────────────────────────────────────────────
//  HeroBlock — full-width gradient/image hero
//
//  Variants:
//    GRADIENT  → linear gradient (primary → secondary)
//    TONAL     → surface variant tint
//    IMAGE     → placeholder + dark scrim overlay
//    DARK_CARD → deep surface elevated card
// ─────────────────────────────────────────────

enum class HeroVariant { GRADIENT, TONAL, IMAGE, DARK_CARD }

@Composable
fun HeroBlock(
    title: String,
    subtitle: String? = null,
    variant: HeroVariant = HeroVariant.GRADIENT,
    height: Dp = 200.dp,
    modifier: Modifier = Modifier,
    content: (@Composable () -> Unit)? = null,
) {
    val bgModifier: Modifier = when (variant) {
        HeroVariant.GRADIENT -> Modifier.background(
            Brush.linearGradient(
                colors = listOf(
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.secondary,
                ),
                start = Offset.Zero,
                end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
            )
        )
        HeroVariant.TONAL -> Modifier.background(
            MaterialTheme.colorScheme.surfaceVariant
        )
        HeroVariant.DARK_CARD -> Modifier.background(
            MaterialTheme.colorScheme.surface
        )
        HeroVariant.IMAGE -> Modifier.background(
            MaterialTheme.colorScheme.surfaceVariant
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(MaterialTheme.shapes.extraLarge)
            .then(bgModifier),
    ) {
        // Radial accent glow (GRADIENT only)
        if (variant == HeroVariant.GRADIENT) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(160.dp)
                    .offset(x = 40.dp, y = (-40).dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.15f),
                                Color.Transparent,
                            )
                        )
                    )
            )
        }

        // Text + content slot
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (variant == HeroVariant.GRADIENT || variant == HeroVariant.IMAGE)
                        Color.White.copy(alpha = 0.72f)
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(3.dp))
            }
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = if (variant == HeroVariant.GRADIENT || variant == HeroVariant.IMAGE)
                    Color.White
                else
                    MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.ExtraBold,
            )
            if (content != null) {
                Spacer(Modifier.height(10.dp))
                content()
            }
        }
    }
}
