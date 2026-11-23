package com.nailsky.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nailsky.app.data.ColorMood
import com.nailsky.app.data.NailShape
import com.nailsky.app.data.StudioEffect

/** Draws a crisp architectural silhouette of a [NailShape] (Almond, Coffin, Oval, Square, Stiletto, Short). */
fun buildNailPath(
    shape: NailShape,
    left: Float,
    top: Float,
    width: Float,
    height: Float,
): Path {
    val right = left + width
    val bottom = top + height
    val cx = left + width / 2f
    val baseCurveY = bottom - width * 0.34f

    return Path().apply {
        when (shape) {
            NailShape.ALMOND -> {
                moveTo(left + width * 0.08f, baseCurveY)
                cubicTo(
                    left + width * 0.02f, top + height * 0.38f,
                    cx - width * 0.16f, top + height * 0.04f,
                    cx, top,
                )
                cubicTo(
                    cx + width * 0.16f, top + height * 0.04f,
                    right - width * 0.02f, top + height * 0.38f,
                    right - width * 0.08f, baseCurveY,
                )
                cubicTo(right - width * 0.14f, bottom, left + width * 0.14f, bottom, left + width * 0.08f, baseCurveY)
                close()
            }

            NailShape.COFFIN -> {
                moveTo(left + width * 0.06f, baseCurveY)
                lineTo(left + width * 0.22f, top + height * 0.04f)
                quadraticTo(cx, top, right - width * 0.22f, top + height * 0.04f)
                lineTo(right - width * 0.06f, baseCurveY)
                cubicTo(right - width * 0.12f, bottom, left + width * 0.12f, bottom, left + width * 0.06f, baseCurveY)
                close()
            }

            NailShape.OVAL -> {
                moveTo(left + width * 0.06f, baseCurveY)
                cubicTo(
                    left + width * 0.04f, top + height * 0.18f,
                    left + width * 0.18f, top,
                    cx, top,
                )
                cubicTo(
                    right - width * 0.18f, top,
                    right - width * 0.04f, top + height * 0.18f,
                    right - width * 0.06f, baseCurveY,
                )
                cubicTo(right - width * 0.12f, bottom, left + width * 0.12f, bottom, left + width * 0.06f, baseCurveY)
                close()
            }

            NailShape.SQUARE -> {
                val r = width * 0.14f
                moveTo(left + width * 0.06f, baseCurveY)
                lineTo(left + width * 0.06f, top + r)
                quadraticTo(left + width * 0.06f, top, left + width * 0.06f + r, top)
                lineTo(right - width * 0.06f - r, top)
                quadraticTo(right - width * 0.06f, top, right - width * 0.06f, top + r)
                lineTo(right - width * 0.06f, baseCurveY)
                cubicTo(right - width * 0.12f, bottom, left + width * 0.12f, bottom, left + width * 0.06f, baseCurveY)
                close()
            }

            NailShape.STILETTO -> {
                moveTo(left + width * 0.06f, baseCurveY)
                cubicTo(
                    left + width * 0.04f, top + height * 0.45f,
                    cx - width * 0.10f, top + height * 0.12f,
                    cx, top,
                )
                cubicTo(
                    cx + width * 0.10f, top + height * 0.12f,
                    right - width * 0.04f, top + height * 0.45f,
                    right - width * 0.06f, baseCurveY,
                )
                cubicTo(right - width * 0.12f, bottom, left + width * 0.12f, bottom, left + width * 0.06f, baseCurveY)
                close()
            }

            NailShape.SHORT -> {
                val shortTop = top + height * 0.24f
                val r = width * 0.32f
                moveTo(left + width * 0.06f, baseCurveY)
                lineTo(left + width * 0.06f, shortTop + r)
                quadraticTo(left + width * 0.06f, shortTop, cx, shortTop)
                quadraticTo(right - width * 0.06f, shortTop, right - width * 0.06f, shortTop + r)
                lineTo(right - width * 0.06f, baseCurveY)
                cubicTo(right - width * 0.12f, bottom, left + width * 0.12f, bottom, left + width * 0.06f, baseCurveY)
                close()
            }
        }
    }
}

@Composable
fun NailShapeIcon(
    shape: NailShape,
    selected: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 28.dp,
) {
    val fill = if (selected) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.28f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
    }
    val strokeColor = if (selected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Canvas(modifier = modifier.size(width = size * 0.72f, height = size)) {
        val pad = 2.dp.toPx()
        val path = buildNailPath(
            shape = shape,
            left = pad,
            top = pad,
            width = this.size.width - pad * 2,
            height = this.size.height - pad * 2,
        )
        drawPath(path = path, color = fill)
        drawPath(
            path = path,
            color = strokeColor,
            style = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round),
        )
        // Subtle glossy reflection arc on selected nail silhouette
        if (selected) {
            drawLine(
                color = Color.White.copy(alpha = 0.75f),
                start = Offset(this.size.width * 0.36f, this.size.height * 0.32f),
                end = Offset(this.size.width * 0.36f, this.size.height * 0.62f),
                strokeWidth = 1.5.dp.toPx(),
                cap = StrokeCap.Round,
            )
        }
    }
}

/** Tactile horizontal strip of visual nail-shape silhouettes (replaces boring text filter chips). */
@Composable
fun NailShapeStrip(
    selectedShape: NailShape?,
    onSelectShape: (NailShape?) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp),
) {
    LazyRow(
        modifier = modifier,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(NailShape.entries, key = { it.name }) { shape ->
            val selected = selectedShape == shape
            val container by animateColorAsState(
                targetValue = if (selected) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surface
                },
                label = "shape_container",
            )
            Surface(
                onClick = { onSelectShape(if (selected) null else shape) },
                shape = RoundedCornerShape(18.dp),
                color = container,
                border = BorderStroke(
                    width = if (selected) 1.5.dp else 1.dp,
                    color = if (selected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.outlineVariant
                    },
                ),
            ) {
                Column(
                    modifier = Modifier
                        .width(64.dp)
                        .padding(vertical = 10.dp, horizontal = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    NailShapeIcon(shape = shape, selected = selected, size = 28.dp)
                    Text(
                        text = shape.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (selected) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    )
                }
            }
        }
    }
}

/**
 * Glossy 3D nail-polish droplet swatch painted with specular highlight & rim shading.
 */
@Composable
fun LacquerDroplet(
    color: Color,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val ringColor = MaterialTheme.colorScheme.primary
    val borderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)

    Box(
        modifier = modifier
            .size(size + if (selected) 8.dp else 4.dp)
            .clip(CircleShape)
            .then(
                if (selected) {
                    Modifier.border(2.dp, ringColor, CircleShape)
                } else {
                    Modifier
                },
            )
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(size)) {
            drawGlossyDroplet(color, borderColor)
        }
    }
}

private fun DrawScope.drawGlossyDroplet(base: Color, rimColor: Color) {
    val r = size.minDimension / 2f
    val center = Offset(size.width / 2f, size.height / 2f)
    val deeper = lerp(base, Color.Black, 0.22f)
    val lighter = lerp(base, Color.White, 0.35f)

    // Base sphere gradient
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(lighter, base, deeper),
            center = Offset(center.x - r * 0.25f, center.y - r * 0.25f),
            radius = r * 1.25f,
        ),
        radius = r - 1f,
        center = center,
    )
    // Specular glossy gleam
    drawOval(
        color = Color.White.copy(alpha = 0.65f),
        topLeft = Offset(center.x - r * 0.48f, center.y - r * 0.56f),
        size = Size(r * 0.52f, r * 0.30f),
    )
    // Tiny secondary catchlight
    drawCircle(
        color = Color.White.copy(alpha = 0.45f),
        radius = r * 0.10f,
        center = Offset(center.x + r * 0.34f, center.y + r * 0.38f),
    )
    // Hairline rim
    drawCircle(
        color = rimColor,
        radius = r - 1f,
        center = center,
        style = Stroke(width = 1.dp.toPx()),
    )
}

/** Tactile "Today's Nail Mood" row of glossy polish color droplets. */
@Composable
fun ColorMoodStrip(
    selectedMood: ColorMood?,
    onSelectMood: (ColorMood?) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp),
) {
    LazyRow(
        modifier = modifier,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(ColorMood.entries, key = { it.name }) { mood ->
            val selected = selectedMood == mood
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onSelectMood(if (selected) null else mood) }
                    .padding(horizontal = 4.dp, vertical = 2.dp),
            ) {
                LacquerDroplet(
                    color = mood.swatch,
                    size = 38.dp,
                    selected = selected,
                )
                Text(
                    text = mood.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (selected) {
                        MaterialTheme.colorScheme.onBackground
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                )
            }
        }
    }
}

/** Compact 5-nail visual preview of a saved Custom Studio Set. */
@Composable
fun MiniSetPreview(
    shape: NailShape,
    effect: StudioEffect,
    colors: List<Color>,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val count = 5
        val gap = size.width * 0.045f
        val nailW = (size.width - gap * (count + 1)) / count
        val scaleHeights = floatArrayOf(0.72f, 0.88f, 0.96f, 0.86f, 0.68f)

        for (i in 0 until count) {
            val h = size.height * scaleHeights[i]
            val x = gap + i * (nailW + gap)
            val y = size.height - h
            val color = colors.getOrElse(i) { Color(0xFFF7B8CF) }
            val path = buildNailPath(shape, x, y, nailW, h)

            drawPath(path = path, color = color)

            when (effect) {
                StudioEffect.CHROME -> {
                    drawPath(
                        path = path,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.55f),
                                Color.Transparent,
                                Color(0xFF9AD9FF).copy(alpha = 0.35f),
                            ),
                            start = Offset(x, y),
                            end = Offset(x + nailW, y + h),
                        ),
                    )
                }

                StudioEffect.AURA -> {
                    drawPath(
                        path = path,
                        brush = Brush.radialGradient(
                            colors = listOf(Color.White.copy(alpha = 0.62f), Color.Transparent),
                            center = Offset(x + nailW / 2f, y + h * 0.45f),
                            radius = nailW * 0.72f,
                        ),
                    )
                }

                StudioEffect.FRENCH -> {
                    drawRoundRect(
                        color = Color.White.copy(alpha = 0.85f),
                        topLeft = Offset(x + nailW * 0.16f, y + h * 0.04f),
                        size = Size(nailW * 0.68f, h * 0.18f),
                        cornerRadius = CornerRadius(nailW * 0.35f, nailW * 0.35f),
                    )
                }

                StudioEffect.GLITTER -> {
                    drawCircle(Color.White.copy(alpha = 0.85f), radius = nailW * 0.08f, center = Offset(x + nailW * 0.35f, y + h * 0.3f))
                    drawCircle(Color.White.copy(alpha = 0.75f), radius = nailW * 0.06f, center = Offset(x + nailW * 0.68f, y + h * 0.55f))
                    drawCircle(Color.White.copy(alpha = 0.8f), radius = nailW * 0.05f, center = Offset(x + nailW * 0.42f, y + h * 0.72f))
                }

                StudioEffect.GLOSSY -> {
                    drawLine(
                        color = Color.White.copy(alpha = 0.6f),
                        start = Offset(x + nailW * 0.32f, y + h * 0.24f),
                        end = Offset(x + nailW * 0.32f, y + h * 0.62f),
                        strokeWidth = nailW * 0.11f,
                        cap = StrokeCap.Round,
                    )
                }

                StudioEffect.MATTE -> Unit
            }

            drawPath(
                path = path,
                color = Color.Black.copy(alpha = 0.12f),
                style = Stroke(width = 1.dp.toPx()),
            )
        }
    }
}
