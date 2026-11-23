package com.nailsky.app.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.rounded.AutoAwesome
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.nailsky.app.data.NailDesign
import com.nailsky.app.data.sampleDesigns
import com.nailsky.app.ui.theme.LocalNailSkyDarkTheme
import com.nailsky.app.ui.theme.NailSkyTheme
import com.nailsky.app.ui.theme.ThemePreviews
import com.nailsky.app.ui.theme.deepen
import kotlinx.coroutines.delay

private val CardShape = RoundedCornerShape(22.dp)

/**
 * Borderless photo-first lookbook card — eliminates boxy white web footers in favor of
 * full-bleed photography with a subtle gradient scrim and floating glass heart pill.
 */
@Composable
fun DesignCard(
    design: NailDesign,
    isSaved: Boolean,
    onToggleSave: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = CardShape,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        DesignArtwork(
            design = design,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f / design.heightRatio),
        ) {
            // Top-left subtle shape pill
            Surface(
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.34f),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp),
            ) {
                Text(
                    text = design.shape.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                )
            }

            SaveToggle(
                isSaved = isSaved,
                onToggle = onToggleSave,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
            )

            // Bottom gradient overlay with title & mini color swatches
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.68f),
                            ),
                        ),
                    )
                    .padding(start = 12.dp, end = 12.dp, top = 28.dp, bottom = 11.dp),
            ) {
                Text(
                    text = design.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(3.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = design.finish.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.82f),
                        maxLines = 1,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy((-3).dp)) {
                        design.colors.take(3).forEach { swatch ->
                            Box(
                                modifier = Modifier
                                    .size(11.dp)
                                    .clip(CircleShape)
                                    .background(swatch)
                                    .border(1.dp, Color.White.copy(alpha = 0.85f), CircleShape),
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Editorial "Look of the Week" full-bleed hero card for the top of the Lookbook Home screen.
 */
@Composable
fun EditorialHeroCard(
    design: NailDesign,
    isSaved: Boolean,
    onToggleSave: () -> Unit,
    onClick: () -> Unit,
    onTryInStudio: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(236.dp),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        DesignArtwork(
            design = design,
            modifier = Modifier.fillMaxSize(),
        ) {
            // Frosted pill badge
            Surface(
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.42f),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(14.dp),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AutoAwesome,
                        contentDescription = null,
                        tint = Color(0xFFFFD6E5),
                        modifier = Modifier.size(13.dp),
                    )
                    Text(
                        text = "Look of the Week",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                    )
                }
            }

            SaveToggle(
                isSaved = isSaved,
                onToggle = onToggleSave,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp),
            )

            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.72f)),
                        ),
                    )
                    .padding(start = 18.dp, end = 14.dp, top = 36.dp, bottom = 16.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = design.title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "${design.shape.label} · ${design.finish.label} · ${design.artist}",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.85f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(Modifier.width(10.dp))
                Surface(
                    onClick = onTryInStudio,
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.92f),
                ) {
                    Text(
                        text = "Try Studio",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF1E2A4A),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    )
                }
            }
        }
    }
}

/** Ranked card (`#1`, `#2`, `#3`) for Explore's Weekly Top Looks strip. */
@Composable
fun RankedLookCard(
    rank: Int,
    design: NailDesign,
    isSaved: Boolean,
    onToggleSave: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .width(148.dp)
            .height(192.dp),
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        DesignArtwork(
            design = design,
            modifier = Modifier.fillMaxSize(),
        ) {
            Surface(
                shape = CircleShape,
                color = when (rank) {
                    1 -> MaterialTheme.colorScheme.tertiary
                    2 -> MaterialTheme.colorScheme.primary
                    else -> Color.Black.copy(alpha = 0.55f)
                },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp),
            ) {
                Text(
                    text = "#$rank",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (rank == 2) MaterialTheme.colorScheme.onPrimary else Color.White,
                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                )
            }

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
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.72f)),
                        ),
                    )
                    .padding(start = 12.dp, end = 12.dp, top = 24.dp, bottom = 10.dp),
            ) {
                Text(
                    text = design.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "${design.likesCount} saves",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.82f),
                )
            }
        }
    }
}

/** Wide card for the "Trending" carousel. */
@Composable
fun TrendingCard(
    design: NailDesign,
    isSaved: Boolean,
    onToggleSave: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier.width(216.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        DesignArtwork(
            design = design,
            modifier = Modifier
                .fillMaxWidth()
                .height(152.dp),
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
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.65f)),
                        ),
                    )
                    .padding(start = 14.dp, end = 14.dp, top = 28.dp, bottom = 12.dp),
            ) {
                Text(
                    text = design.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "${design.shape.label} · ${design.artist}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.85f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

/**
 * Loads the real nail photo via Coil over [GradientTile] so cards always show graceful NailSky
 * gradient artwork while loading, in offline mode, or inside Compose Previews.
 */
@Composable
fun DesignArtwork(
    design: NailDesign,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit = {},
) {
    val isPreview = LocalInspectionMode.current
    val context = LocalContext.current

    GradientTile(design = design, modifier = modifier) {
        if (!isPreview && design.imageUrl.isNotBlank()) {
            val request = remember(design.imageUrl) {
                ImageRequest.Builder(context)
                    .data(design.imageUrl)
                    .crossfade(300)
                    .build()
            }
            AsyncImage(
                model = request,
                contentDescription = design.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
        content()
    }
}

/**
 * Fallback & placeholder artwork: a soft gradient with stylised nail shapes. Uses deeper tones in dark mode.
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
    val haptics = LocalHapticFeedback.current
    var pulsing by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pulsing) 1.25f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium,
        ),
        label = "heart_scale",
    )

    LaunchedEffect(pulsing) {
        if (pulsing) {
            delay(140)
            pulsing = false
        }
    }

    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.82f),
    ) {
        IconToggleButton(
            checked = isSaved,
            onCheckedChange = {
                pulsing = true
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onToggle()
            },
            modifier = Modifier.size(34.dp),
        ) {
            Icon(
                imageVector = if (isSaved) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                contentDescription = if (isSaved) "Remove from saved" else "Save design",
                tint = if (isSaved) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .size(19.dp)
                    .scale(scale),
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
                onClick = {},
                modifier = Modifier.width(170.dp),
            )
        }
    }
}
