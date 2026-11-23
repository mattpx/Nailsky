package com.nailsky.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.nailsky.app.data.CustomNailSet
import com.nailsky.app.data.NailShape
import com.nailsky.app.data.StudioEffect
import com.nailsky.app.ui.components.LacquerDroplet
import com.nailsky.app.ui.components.NailShapeIcon
import com.nailsky.app.ui.components.buildNailPath
import com.nailsky.app.ui.theme.LocalNailSkyDarkTheme
import com.nailsky.app.ui.theme.NailSkyTheme
import com.nailsky.app.ui.theme.StudioPolishes
import com.nailsky.app.ui.theme.StudioSkinTones
import com.nailsky.app.ui.theme.ThemePreviews
import kotlin.random.Random

private val FingerNames = listOf("Thumb", "Index", "Middle", "Ring", "Pinky")

/**
 * Interactive Hand Studio: tap individual fingernails (or paint all 5 at once), morph nail shape
 * and length live, test finishes (Glossy, Chrome, French, Aura, Glitter, Matte), adjust skin tone,
 * and save custom sets to Room.
 */
@Composable
fun StudioScreen(
    preset: CustomNailSet?,
    onConsumePreset: () -> Unit,
    onSaveCustomSet: (CustomNailSet) -> Unit,
    modifier: Modifier = Modifier,
) {
    val haptics = LocalHapticFeedback.current
    val dark = LocalNailSkyDarkTheme.current

    var selectedShape by remember { mutableStateOf(NailShape.ALMOND) }
    var lengthFactor by remember { mutableFloatStateOf(0.55f) }
    var selectedEffect by remember { mutableStateOf(StudioEffect.CHROME) }
    var skinToneIndex by remember { mutableIntStateOf(1) }
    var paintAllNails by remember { mutableStateOf(true) }
    var activeFingerIndex by remember { mutableIntStateOf(2) }
    val nailColors = remember {
        mutableStateListOf(
            StudioPolishes[0].color,
            StudioPolishes[0].color,
            StudioPolishes[1].color,
            StudioPolishes[0].color,
            StudioPolishes[0].color,
        )
    }

    var showSaveDialog by remember { mutableStateOf(false) }

    LaunchedEffect(preset) {
        if (preset != null) {
            selectedShape = preset.shape
            lengthFactor = preset.lengthFactor.coerceIn(0f, 1f)
            selectedEffect = preset.effect
            skinToneIndex = preset.skinToneIndex.coerceIn(0, StudioSkinTones.lastIndex)
            for (i in 0 until 5) {
                nailColors[i] = preset.nailColors.getOrElse(i) { StudioPolishes[0].color }
            }
            onConsumePreset()
        }
    }

    val applyPolish: (Color) -> Unit = { color ->
        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        if (paintAllNails) {
            for (i in 0 until 5) nailColors[i] = color
        } else {
            nailColors[activeFingerIndex] = color
        }
    }

    val randomizeLook = {
        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        val primary = StudioPolishes[Random.nextInt(StudioPolishes.size)].color
        val accent = StudioPolishes[Random.nextInt(StudioPolishes.size)].color
        selectedShape = NailShape.entries[Random.nextInt(NailShape.entries.size)]
        selectedEffect = StudioEffect.entries[Random.nextInt(StudioEffect.entries.size)]
        lengthFactor = listOf(0.25f, 0.5f, 0.72f, 0.9f).random()
        for (i in 0 until 5) {
            nailColors[i] = if (i == 2 || i == 3) accent else primary
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    if (dark) {
                        listOf(Color(0xFF141B2E), MaterialTheme.colorScheme.background)
                    } else {
                        listOf(Color(0xFFEEF7FE), Color(0xFFFDF1F6), MaterialTheme.colorScheme.background)
                    },
                ),
            )
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 112.dp),
    ) {
        // Studio top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(
                    text = "Nail Studio",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = if (paintAllNails) {
                        "Painting all 5 nails · tap a finger for accent"
                    } else {
                        "Customizing ${FingerNames[activeFingerIndex]} finger"
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f),
                ) {
                    IconButton(onClick = randomizeLook, modifier = Modifier.size(40.dp)) {
                        Icon(
                            imageVector = Icons.Rounded.Shuffle,
                            contentDescription = "Randomize nail set",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
                Button(
                    onClick = { showSaveDialog = true },
                    shape = CircleShape,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                ) {
                    Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Save Set")
                }
            }
        }

        // Skin tone selector + All/Single nail toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StudioSkinTones.forEachIndexed { index, tone ->
                    val selected = index == skinToneIndex
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(tone)
                            .border(
                                width = if (selected) 2.5.dp else 1.dp,
                                color = if (selected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.outlineVariant
                                },
                                shape = CircleShape,
                            )
                            .clickable { skinToneIndex = index },
                    )
                }
            }

            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            ) {
                Row(modifier = Modifier.padding(3.dp)) {
                    StudioModeChip(
                        label = "All",
                        selected = paintAllNails,
                        onClick = { paintAllNails = true },
                    )
                    StudioModeChip(
                        label = "Single",
                        selected = !paintAllNails,
                        onClick = { paintAllNails = false },
                    )
                }
            }
        }

        // Interactive Hand Canvas
        InteractiveHandCanvas(
            skinTone = StudioSkinTones[skinToneIndex],
            shape = selectedShape,
            lengthFactor = lengthFactor,
            effect = selectedEffect,
            nailColors = nailColors.toList(),
            highlightedFinger = if (paintAllNails) -1 else activeFingerIndex,
            onFingerTapped = { index ->
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                activeFingerIndex = index
                paintAllNails = false
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(310.dp)
                .padding(horizontal = 16.dp),
        )

        // Controls Dock Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            tonalElevation = 2.dp,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)),
        ) {
            Column(
                modifier = Modifier.padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                // Polish shade selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Polish Shade",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    val activeColor = nailColors[activeFingerIndex]
                    val polishName = StudioPolishes.firstOrNull { it.color == activeColor }?.name ?: "Custom"
                    Text(
                        text = polishName,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(StudioPolishes, key = { it.name }) { polish ->
                        val activeColor = if (paintAllNails) nailColors[2] else nailColors[activeFingerIndex]
                        LacquerDroplet(
                            color = polish.color,
                            size = 38.dp,
                            selected = polish.color == activeColor,
                            onClick = { applyPolish(polish.color) },
                        )
                    }
                }

                // Effect / Finish selector
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(StudioEffect.entries, key = { it.name }) { effect ->
                        FilterChip(
                            selected = selectedEffect == effect,
                            onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                selectedEffect = effect
                            },
                            label = { Text(effect.label) },
                            shape = CircleShape,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            ),
                        )
                    }
                }

                // Shape silhouettes
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Nail Shape",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = selectedShape.label,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(NailShape.entries, key = { it.name }) { shape ->
                        val isSelected = selectedShape == shape
                        Surface(
                            onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                selectedShape = shape
                            },
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                            },
                        ) {
                            Column(
                                modifier = Modifier
                                    .width(56.dp)
                                    .padding(vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                NailShapeIcon(shape = shape, selected = isSelected, size = 24.dp)
                                Text(
                                    text = shape.label,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) {
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                )
                            }
                        }
                    }
                }

                // Length slider
                Column(Modifier.padding(horizontal = 16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = "Nail Length",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        val lengthLabel = when {
                            lengthFactor < 0.25f -> "Short"
                            lengthFactor < 0.55f -> "Medium"
                            lengthFactor < 0.82f -> "Long"
                            else -> "XL Salon"
                        }
                        Text(
                            text = lengthLabel,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                    Slider(
                        value = lengthFactor,
                        onValueChange = { lengthFactor = it },
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                        ),
                    )
                }
            }
        }
    }

    if (showSaveDialog) {
        SaveStudioSetDialog(
            defaultName = "${selectedEffect.label} ${selectedShape.label}",
            onDismiss = { showSaveDialog = false },
            onSave = { setName ->
                onSaveCustomSet(
                    CustomNailSet(
                        name = setName,
                        shape = selectedShape,
                        lengthFactor = lengthFactor,
                        effect = selectedEffect,
                        skinToneIndex = skinToneIndex,
                        nailColors = nailColors.toList(),
                    ),
                )
                showSaveDialog = false
            },
        )
    }
}

@Composable
private fun StudioModeChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
        )
    }
}

@Composable
private fun InteractiveHandCanvas(
    skinTone: Color,
    shape: NailShape,
    lengthFactor: Float,
    effect: StudioEffect,
    nailColors: List<Color>,
    highlightedFinger: Int,
    onFingerTapped: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val highlightColor = MaterialTheme.colorScheme.primary
    val hitTargets = remember { MutableList(5) { Rect.Zero } }

    Canvas(
        modifier = modifier.pointerInput(Unit) {
            detectTapGestures { tapOffset ->
                val hitIndex = hitTargets.indexOfFirst { it.inflate(24f).contains(tapOffset) }
                if (hitIndex in 0 until 5) {
                    onFingerTapped(hitIndex)
                }
            }
        },
    ) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val skinShadow = lerp(skinTone, Color.Black, 0.16f)
        val skinHighlight = lerp(skinTone, Color.White, 0.14f)

        // Soft ambient glow behind the hand
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    highlightColor.copy(alpha = 0.18f),
                    Color.Transparent,
                ),
                center = Offset(cx, h * 0.44f),
                radius = w * 0.46f,
            ),
            radius = w * 0.46f,
            center = Offset(cx, h * 0.44f),
        )

        // Palm silhouette rising from bottom center
        val palmWidth = w * 0.48f
        val palmHeight = h * 0.44f
        val palmTop = h * 0.62f
        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(skinHighlight, skinTone, skinShadow),
                startY = palmTop,
                endY = h,
            ),
            topLeft = Offset(cx - palmWidth / 2f, palmTop),
            size = Size(palmWidth, palmHeight),
            cornerRadius = CornerRadius(palmWidth * 0.42f, palmWidth * 0.42f),
        )

        // 5 Finger geometry: Thumb, Index, Middle, Ring, Pinky
        val fingerXFractions = floatArrayOf(0.21f, 0.35f, 0.50f, 0.65f, 0.78f)
        val fingerTopFractions = floatArrayOf(0.50f, 0.25f, 0.18f, 0.25f, 0.37f)
        val fingerWidthFractions = floatArrayOf(0.115f, 0.105f, 0.110f, 0.102f, 0.088f)

        for (i in 0 until 5) {
            val fx = w * fingerXFractions[i]
            val fTop = h * fingerTopFractions[i]
            val fWidth = w * fingerWidthFractions[i]
            val fHeight = (h * 0.78f) - fTop

            // Finger body
            drawRoundRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(skinShadow, skinHighlight, skinTone, skinShadow),
                    startX = fx - fWidth / 2f,
                    endX = fx + fWidth / 2f,
                ),
                topLeft = Offset(fx - fWidth / 2f, fTop),
                size = Size(fWidth, fHeight),
                cornerRadius = CornerRadius(fWidth * 0.48f, fWidth * 0.48f),
            )

            // Subtle knuckle crease
            drawLine(
                color = skinShadow.copy(alpha = 0.32f),
                start = Offset(fx - fWidth * 0.28f, fTop + fHeight * 0.46f),
                end = Offset(fx + fWidth * 0.28f, fTop + fHeight * 0.46f),
                strokeWidth = 1.5.dp.toPx(),
                cap = StrokeCap.Round,
            )

            // Nail geometry reacting live to shape & lengthFactor
            val nailWidth = fWidth * 0.76f
            val baseNailH = nailWidth * 1.18f
            val extension = nailWidth * (0.85f * lengthFactor)
            val nailHeight = baseNailH + extension
            val nailLeft = fx - nailWidth / 2f
            val nailBottom = fTop + baseNailH * 0.96f
            val nailTop = nailBottom - nailHeight

            hitTargets[i] = Rect(nailLeft, nailTop, nailLeft + nailWidth, nailBottom)

            val polishColor = nailColors.getOrElse(i) { Color(0xFFF7B8CF) }
            val nailPath = buildNailPath(shape, nailLeft, nailTop, nailWidth, nailHeight)

            // Selection halo if Single-Nail mode is focused on this finger
            if (highlightedFinger == i) {
                drawCircle(
                    color = highlightColor.copy(alpha = 0.25f),
                    radius = nailWidth * 0.92f,
                    center = Offset(fx, nailTop + nailHeight * 0.5f),
                )
                drawCircle(
                    color = highlightColor,
                    radius = nailWidth * 0.92f,
                    center = Offset(fx, nailTop + nailHeight * 0.5f),
                    style = Stroke(width = 2.dp.toPx()),
                )
            }

            // Nail lacquer fill
            val polishShaded = lerp(polishColor, Color.Black, 0.14f)
            val polishLit = lerp(polishColor, Color.White, 0.16f)
            drawPath(
                path = nailPath,
                brush = Brush.horizontalGradient(
                    colors = listOf(polishShaded, polishLit, polishColor, polishShaded),
                    startX = nailLeft,
                    endX = nailLeft + nailWidth,
                ),
            )

            // Live art / finish overlay
            when (effect) {
                StudioEffect.GLOSSY -> {
                    drawLine(
                        color = Color.White.copy(alpha = 0.65f),
                        start = Offset(nailLeft + nailWidth * 0.30f, nailTop + nailHeight * 0.22f),
                        end = Offset(nailLeft + nailWidth * 0.30f, nailTop + nailHeight * 0.68f),
                        strokeWidth = nailWidth * 0.12f,
                        cap = StrokeCap.Round,
                    )
                }

                StudioEffect.CHROME -> {
                    drawPath(
                        path = nailPath,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.62f),
                                Color(0xFF9CD8FF).copy(alpha = 0.28f),
                                Color(0xFFFFC5DF).copy(alpha = 0.36f),
                                Color.White.copy(alpha = 0.55f),
                            ),
                            start = Offset(nailLeft, nailTop),
                            end = Offset(nailLeft + nailWidth, nailBottom),
                        ),
                    )
                    drawLine(
                        color = Color.White.copy(alpha = 0.85f),
                        start = Offset(nailLeft + nailWidth * 0.34f, nailTop + nailHeight * 0.18f),
                        end = Offset(nailLeft + nailWidth * 0.34f, nailTop + nailHeight * 0.72f),
                        strokeWidth = nailWidth * 0.14f,
                        cap = StrokeCap.Round,
                    )
                }

                StudioEffect.FRENCH -> {
                    val tipHeight = nailHeight * 0.24f
                    drawRoundRect(
                        color = Color.White.copy(alpha = 0.90f),
                        topLeft = Offset(nailLeft + nailWidth * 0.14f, nailTop + nailHeight * 0.03f),
                        size = Size(nailWidth * 0.72f, tipHeight),
                        cornerRadius = CornerRadius(nailWidth * 0.36f, nailWidth * 0.36f),
                    )
                    drawLine(
                        color = Color.White.copy(alpha = 0.48f),
                        start = Offset(nailLeft + nailWidth * 0.30f, nailTop + nailHeight * 0.32f),
                        end = Offset(nailLeft + nailWidth * 0.30f, nailTop + nailHeight * 0.68f),
                        strokeWidth = nailWidth * 0.10f,
                        cap = StrokeCap.Round,
                    )
                }

                StudioEffect.AURA -> {
                    drawPath(
                        path = nailPath,
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.75f),
                                Color(0xFFFFB3CF).copy(alpha = 0.42f),
                                Color.Transparent,
                            ),
                            center = Offset(fx, nailTop + nailHeight * 0.48f),
                            radius = nailWidth * 0.62f,
                        ),
                    )
                }

                StudioEffect.GLITTER -> {
                    val dots = listOf(
                        Offset(0.32f, 0.26f) to 0.09f,
                        Offset(0.64f, 0.36f) to 0.07f,
                        Offset(0.45f, 0.54f) to 0.08f,
                        Offset(0.28f, 0.68f) to 0.06f,
                        Offset(0.68f, 0.72f) to 0.07f,
                        Offset(0.50f, 0.20f) to 0.06f,
                    )
                    dots.forEach { (pos, rad) ->
                        drawCircle(
                            color = Color.White.copy(alpha = 0.88f),
                            radius = nailWidth * rad,
                            center = Offset(nailLeft + nailWidth * pos.x, nailTop + nailHeight * pos.y),
                        )
                    }
                }

                StudioEffect.MATTE -> {
                    drawPath(
                        path = nailPath,
                        color = Color.White.copy(alpha = 0.08f),
                    )
                }
            }

            // Clean cuticle rim outline
            drawPath(
                path = nailPath,
                color = Color.Black.copy(alpha = 0.14f),
                style = Stroke(width = 1.dp.toPx()),
            )
        }
    }
}

@Composable
private fun SaveStudioSetDialog(
    defaultName: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
) {
    var name by remember { mutableStateOf(defaultName) }
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Rounded.AutoAwesome, contentDescription = null) },
        title = { Text("Save Studio Set") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { if (it.length <= 28) name = it },
                label = { Text("Set name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(name.trim().ifBlank { defaultName }) },
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}

@ThemePreviews
@Composable
private fun StudioScreenPreview() {
    NailSkyTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            StudioScreen(
                preset = null,
                onConsumePreset = {},
                onSaveCustomSet = {},
            )
        }
    }
}
