package com.nailsky.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nailsky.app.data.NailDesign
import com.nailsky.app.data.sampleDesigns
import com.nailsky.app.ui.theme.LocalNailSkyDarkTheme
import com.nailsky.app.ui.theme.NailSkyTheme
import com.nailsky.app.ui.theme.ThemePreviews
import com.nailsky.app.ui.theme.deepen

private val CardShape = RoundedCornerShape(20.dp)

/** Card for the "Inspiration" staggered grid. */
@Composable
fun DesignCard(
    design: NailDesign,
    isSaved: Boolean,
    onToggleSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        GradientTile(
            design = design,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f / design.heightRatio),
        ) {
            SaveToggle(
                isSaved = isSaved,
                onToggle = onToggleSave,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
            )
        }
        Column(Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
            Text(
                text = design.title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = design.category.label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/** Wide card for the "Trending" carousel. */
@Composable
fun TrendingCard(
    design: NailDesign,
    isSaved: Boolean,
    onToggleSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.width(220.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        GradientTile(
            design = design,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
        ) {
            SaveToggle(
                isSaved = isSaved,
                onToggle = onToggleSave,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.38f)),
                        ),
                    )
                    .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 12.dp),
            ) {
                Text(
                    text = design.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = design.category.label,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.85f),
                )
            }
        }
    }
}

/**
 * Placeholder artwork: a soft gradient with stylised nail shapes. Uses deeper tones in dark mode.
 */
@Composable
fun GradientTile(
    design: NailDesign,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit = {},
) {
    val dark = LocalNailSkyDarkTheme.current
    val colors = remember(design.colors, dark) {
        if (dark) design.colors.map { it.deepen() } else design.colors
    }
    Box(modifier.background(Brush.linearGradient(colors))) {
        NailShapes(Modifier.fillMaxSize())
        content()
    }
}

@Composable
private fun NailShapes(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val nailWidth = size.minDimension * 0.16f
        val gap = nailWidth * 0.45f
        val heights = floatArrayOf(0.42f, 0.5f, 0.44f)
        val totalWidth = nailWidth * 3 + gap * 2
        val startX = (size.width - totalWidth) / 2f
        heights.forEachIndexed { i, h ->
            val nailHeight = size.height * h
            val x = startX + i * (nailWidth + gap)
            val y = (size.height - nailHeight) / 2f + (if (i == 1) -size.height * 0.04f else 0f)
            drawRoundRect(
                color = Color.White.copy(alpha = 0.32f),
                topLeft = Offset(x, y),
                size = Size(nailWidth, nailHeight),
                cornerRadius = CornerRadius(nailWidth / 2f, nailWidth / 2f),
            )
        }
        // Little sparkles
        val sparkle = Color.White.copy(alpha = 0.6f)
        drawCircle(sparkle, radius = nailWidth * 0.12f, center = Offset(size.width * 0.18f, size.height * 0.2f))
        drawCircle(sparkle, radius = nailWidth * 0.08f, center = Offset(size.width * 0.82f, size.height * 0.78f))
        drawCircle(sparkle, radius = nailWidth * 0.06f, center = Offset(size.width * 0.25f, size.height * 0.85f))
    }
}

@Composable
fun SaveToggle(
    isSaved: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
    ) {
        IconToggleButton(
            checked = isSaved,
            onCheckedChange = { onToggle() },
            modifier = Modifier.size(36.dp),
        ) {
            Icon(
                imageVector = if (isSaved) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                contentDescription = if (isSaved) "Remove from saved" else "Save design",
                tint = if (isSaved) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@ThemePreviews
@Composable
private fun DesignCardPreview() {
    NailSkyTheme {
        Row(Modifier.padding(16.dp)) {
            DesignCard(
                design = sampleDesigns[1],
                isSaved = true,
                onToggleSave = {},
                modifier = Modifier.width(170.dp),
            )
        }
    }
}

@ThemePreviews
@Composable
private fun TrendingCardPreview() {
    NailSkyTheme {
        Box(Modifier.padding(16.dp)) {
            TrendingCard(design = sampleDesigns[4], isSaved = false, onToggleSave = {})
        }
    }
}
