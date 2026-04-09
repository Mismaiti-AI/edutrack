package com.edutrack.ui.atoms

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// ─────────────────────────────────────────────
//  AppCard — M3 elevated surface with edgy defaults:
//    - Larger corner radius (shapes.large = 20dp)
//    - Subtle border for dark surfaces
//    - Optional gradient top border accent
// ─────────────────────────────────────────────

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    elevation: Dp = 2.dp,
    showAccentBorder: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
) {
    val clickMod = if (onClick != null)
        Modifier.clickable(onClick = onClick) else Modifier

    val borderMod = if (showAccentBorder)
        Modifier.border(
            width = 1.dp,
            brush = Brush.linearGradient(
                listOf(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f),
                )
            ),
            shape = MaterialTheme.shapes.large,
        )
    else
        Modifier.border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
            shape = MaterialTheme.shapes.large,
        )

    Surface(
        modifier = modifier
            .then(borderMod)
            .then(clickMod),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = elevation,
        shadowElevation = elevation,
        content = { Column(content = content) },
    )
}

// ─────────────────────────────────────────────
//  AppChip — Filter / Assist / Input
//  Selected state uses primary fill (edgy)
// ─────────────────────────────────────────────

enum class ChipVariant { FILTER, ASSIST, INPUT }

@Composable
fun AppChip(
    label: String,
    selected: Boolean = false,
    variant: ChipVariant = ChipVariant.FILTER,
    onClick: () -> Unit = {},
    leadingIcon: (@Composable () -> Unit)? = null,
) {
    when (variant) {
        ChipVariant.FILTER -> FilterChip(
            selected = selected,
            onClick = onClick,
            label = { Text(label, style = MaterialTheme.typography.labelLarge) },
            leadingIcon = leadingIcon,
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primary,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
            border = FilterChipDefaults.filterChipBorder(
                enabled = true,
                selected = selected,
                borderColor = MaterialTheme.colorScheme.outline.copy(0.4f),
                selectedBorderColor = MaterialTheme.colorScheme.primary,
                borderWidth = 1.dp,
            ),
        )
        ChipVariant.ASSIST -> AssistChip(
            onClick = onClick,
            label = { Text(label, style = MaterialTheme.typography.labelLarge) },
            border = AssistChipDefaults.assistChipBorder(
                enabled = true,
                borderColor = MaterialTheme.colorScheme.outline.copy(0.4f),
            ),
        )
        ChipVariant.INPUT -> InputChip(
            selected = selected,
            onClick = onClick,
            label = { Text(label, style = MaterialTheme.typography.labelLarge) },
        )
    }
}

// ─────────────────────────────────────────────
//  StatusBadge — Sale / New / Live / Label
// ─────────────────────────────────────────────

sealed class BadgeVariant {
    data class Sale(val discount: String) : BadgeVariant()
    data class Label(val text: String, val color: Color? = null) : BadgeVariant()
    data object New : BadgeVariant()
    data object Live : BadgeVariant()
}

@Composable
fun StatusBadge(
    variant: BadgeVariant,
    modifier: Modifier = Modifier,
) {
    val (text, bgColor) = when (variant) {
        is BadgeVariant.Sale -> variant.discount to MaterialTheme.colorScheme.error
        is BadgeVariant.Label -> variant.text to (variant.color ?: MaterialTheme.colorScheme.primaryContainer)
        BadgeVariant.New -> "New" to MaterialTheme.colorScheme.tertiary
        BadgeVariant.Live -> "Live" to MaterialTheme.colorScheme.secondary
    }
    Surface(
        modifier = modifier,
        color = bgColor,
        shape = CircleShape,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.Bold,
        )
    }
}

// ─────────────────────────────────────────────
//  AppListTile — leading / title / trailing
//  Surface card container, not plain row
// ─────────────────────────────────────────────

@Composable
fun AppListTile(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    leadingContent: (@Composable () -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
) {
    val clickable = if (onClick != null)
        Modifier.clickable(onClick = onClick) else Modifier

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .then(clickable),
        shape = MaterialTheme.shapes.medium,
        color = containerColor,
        tonalElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            leadingContent?.invoke()
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                subtitle?.let {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            trailingContent?.invoke()
        }
    }
}

// ─────────────────────────────────────────────
//  LinearProgressBar — animated gradient fill
// ─────────────────────────────────────────────

@Composable
fun LinearProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 6.dp,
    useGradient: Boolean = true,
    animated: Boolean = true,
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = if (animated) tween(500) else snap(),
        label = "progressAnimation",
    )
    val fillBrush = if (useGradient)
        Brush.horizontalGradient(
            listOf(
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.secondary,
            )
        )
    else
        SolidColor(MaterialTheme.colorScheme.primary)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animatedProgress)
                .fillMaxHeight()
                .clip(CircleShape)
                .background(fillBrush),
        )
    }
}

// ─────────────────────────────────────────────
//  AppAvatar — circle with optional gradient ring
// ─────────────────────────────────────────────

@Composable
fun AppAvatar(
    size: Dp = 40.dp,
    initials: String = "?",
    hasRing: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val outerSize = if (hasRing) size + 4.dp else size

    Box(
        modifier = modifier.size(outerSize),
        contentAlignment = Alignment.Center,
    ) {
        if (hasRing) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary,
                            )
                        ),
                        shape = CircleShape,
                    )
            )
        }
        Box(
            modifier = Modifier
                .size(size)
                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                .border(
                    if (hasRing) 2.dp else 0.dp,
                    MaterialTheme.colorScheme.background,
                    CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = initials.take(2).uppercase(),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

// ─────────────────────────────────────────────
//  AppFab — gradient FAB (edgy default)
// ─────────────────────────────────────────────

@Composable
fun AppFab(
    onClick: () -> Unit,
    icon: ImageVector,
    label: String? = null,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(
                if (label != null) MaterialTheme.shapes.medium
                else CircleShape
            )
            .background(
                Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary,
                    )
                )
            )
            .clickable(onClick = onClick)
            .shadow(
                elevation = 8.dp,
                shape = if (label != null) MaterialTheme.shapes.medium else CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = if (label != null) 18.dp else 16.dp,
                vertical = 16.dp,
            ),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(icon, contentDescription = label, tint = Color.White)
            label?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

// ─────────────────────────────────────────────
//  StatRow — horizontal metric cards
// ─────────────────────────────────────────────

data class StatItem(
    val value: String,
    val label: String,
    val delta: String? = null,
    val isPositive: Boolean = true,
)

@Composable
fun StatRow(
    stats: List<StatItem>,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        stats.forEach { stat ->
            AppCard(modifier = Modifier.weight(1f)) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        stat.value,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        stat.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    stat.delta?.let { delta ->
                        Spacer(Modifier.height(2.dp))
                        Text(
                            delta,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (stat.isPositive)
                                MaterialTheme.colorScheme.tertiary
                            else
                                MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
//  IconBox — Rounded-square icon container
//    - Colored background with tinted Material icon
//    - Used as leadingContent in AppListTile
// ─────────────────────────────────────────────
@Composable
fun IconBox(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    iconSize: Dp = 22.dp,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
) {
    Box(
        modifier = modifier
            .size(size)
            .background(containerColor, MaterialTheme.shapes.medium),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(iconSize),
            tint = contentColor,
        )
    }
}

// ─────────────────────────────────────────────
//  CarouselCard — Gradient card for horizontal scroll
//    - Diagonal gradient (primary → tertiary)
//    - Used in LazyRow on dashboard
// ─────────────────────────────────────────────
@Composable
fun CarouselCard(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val clickMod = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
    Box(
        modifier = modifier
            .width(260.dp)
            .height(140.dp)
            .shadow(4.dp, MaterialTheme.shapes.large)
            .clip(MaterialTheme.shapes.large)
            .background(
                Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.tertiary,
                    ),
                ),
            )
            .then(clickMod),
        contentAlignment = Alignment.BottomStart,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            subtitle?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.72f),
                )
                Spacer(Modifier.height(3.dp))
            }
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

// ─────────────────────────────────────────────
//  EmptyStateContent — Friendly empty state
//    - Large muted icon + message + optional CTA
// ─────────────────────────────────────────────
@Composable
fun EmptyStateContent(
    icon: ImageVector,
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
        )
        Text(
            message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (actionLabel != null && onAction != null) {
            Spacer(Modifier.height(4.dp))
            FilledTonalButton(onClick = onAction) {
                Text(actionLabel, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
