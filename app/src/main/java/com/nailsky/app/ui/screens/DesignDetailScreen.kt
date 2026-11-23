package com.nailsky.app.ui.screens

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.BookmarkAdd
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.nailsky.app.data.BoardWithDesigns
import com.nailsky.app.data.NailDesign
import com.nailsky.app.data.relatedDesigns
import com.nailsky.app.data.sampleDesigns
import com.nailsky.app.data.toHexString
import com.nailsky.app.ui.components.AddToBoardSheet
import com.nailsky.app.ui.components.CreateBoardDialog
import com.nailsky.app.ui.components.DesignArtwork
import com.nailsky.app.ui.components.DesignCard
import com.nailsky.app.ui.components.LacquerDroplet
import com.nailsky.app.ui.components.NailShapeIcon
import com.nailsky.app.ui.components.SaveToggle
import com.nailsky.app.ui.theme.NailSkyTheme
import com.nailsky.app.ui.theme.ThemePreviews

@Composable
fun DesignDetailScreen(
    design: NailDesign,
    isSaved: Boolean,
    savedIds: List<Int>,
    savedNote: String,
    boards: List<BoardWithDesigns>,
    onBack: () -> Unit,
    onToggleSave: (Int) -> Unit,
    onSaveNote: (String) -> Unit,
    onToggleBoard: (BoardWithDesigns, Boolean) -> Unit,
    onCreateBoard: (name: String, emoji: String) -> Unit,
    onTryInStudio: (NailDesign) -> Unit,
    onTagClick: (String) -> Unit,
    onDesignClick: (Int) -> Unit,
    onShowMessage: (String) -> Unit,
    modifier: Modifier = Modifier,
    allDesigns: List<NailDesign> = sampleDesigns,
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

    var showBoardSheet by remember { mutableStateOf(false) }
    var showCreateBoard by remember { mutableStateOf(false) }
    var showNailTechCard by remember { mutableStateOf(false) }
    var noteDraft by remember(design.id, savedNote) { mutableStateOf(savedNote) }

    val related = remember(design, allDesigns) { relatedDesigns(design, allDesigns) }
    val memberBoardCount = remember(boards, design.id) {
        boards.count { design.id in it.designIds }
    }

    val shareDesign = {
        val hexPalette = design.colors.joinToString(", ") { it.toHexString() }
        val shareText = buildString {
            append("${design.title} — ${design.category.label} nail art by ${design.artist}\n")
            append("Shape: ${design.shape.label} · Finish: ${design.finish.label}\n")
            append("Palette: $hexPalette\n")
            if (design.description.isNotBlank()) {
                append("\n${design.description}\n")
            }
            append("\nShared from NailSky 💅☁️")
        }
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "NailSky: ${design.title}")
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        try {
            context.startActivity(Intent.createChooser(intent, "Share nail design"))
        } catch (_: ActivityNotFoundException) {
            onShowMessage("No share app available")
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding(),
    ) {
        // Edge-to-edge macro hero header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)),
        ) {
            DesignArtwork(
                design = design,
                modifier = Modifier.fillMaxSize(),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Black.copy(alpha = 0.36f), Color.Transparent),
                            ),
                        ),
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    FloatingIconButton(
                        onClick = onBack,
                        icon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onSurface,
                            )
                        },
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FloatingIconButton(
                            onClick = shareDesign,
                            icon = {
                                Icon(
                                    imageVector = Icons.Rounded.Share,
                                    contentDescription = "Share design",
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(20.dp),
                                )
                            },
                        )
                        SaveToggle(
                            isSaved = isSaved,
                            onToggle = { onToggleSave(design.id) },
                        )
                    }
                }

                // Floating Try in Studio pill at bottom-right of hero
                Surface(
                    onClick = { onTryInStudio(design) },
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AutoAwesome,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(16.dp),
                        )
                        Text(
                            text = "Try in Studio",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            // Title, artist, and likes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = design.title,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "${design.category.label} · by ${design.artist}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Favorite,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(16.dp),
                        )
                        Text(
                            text = "${design.likesCount + if (isSaved) 1 else 0}",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    }
                }
            }

            // Signature 1-tap "Show My Nail Tech" Primary CTA
            Button(
                onClick = { showNailTechCard = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            ) {
                Icon(Icons.Rounded.Spa, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Show My Nail Tech", style = MaterialTheme.typography.titleSmall)
            }

            // Secondary actions: Save + Add to Board
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                FilledTonalButton(
                    onClick = { onToggleSave(design.id) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = if (isSaved) {
                        ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    } else {
                        ButtonDefaults.filledTonalButtonColors()
                    },
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(if (isSaved) "Saved" else "Save")
                }

                OutlinedButton(
                    onClick = { showBoardSheet = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.BookmarkAdd,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(if (memberBoardCount > 0) "Boards ($memberBoardCount)" else "Add to Board")
                }
            }

            // Visual specs bar with real nail silhouette
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 1.dp,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        NailShapeIcon(shape = design.shape, selected = true, size = 30.dp)
                        Column {
                            Text(
                                text = "${design.shape.label} · ${design.finish.label}",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = design.difficulty.label,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Schedule,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Text(
                                text = "${design.durationMinutes} min",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }

            if (design.description.isNotBlank()) {
                Text(
                    text = design.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }

            // Glossy 3D Polish Formula Droplets (tap to copy hex)
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Polish Formula",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    design.colors.forEach { swatch ->
                        val hex = remember(swatch) { swatch.toHexString() }
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 1.dp,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(18.dp))
                                .clickable {
                                    clipboard.setText(AnnotatedString(hex))
                                    onShowMessage("Copied $hex")
                                },
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                LacquerDroplet(color = swatch, size = 38.dp)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                                ) {
                                    Text(
                                        text = hex,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                    Icon(
                                        imageVector = Icons.Rounded.ContentCopy,
                                        contentDescription = "Copy $hex",
                                        modifier = Modifier.size(11.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Tags
            if (design.tags.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    design.tags.forEach { tag ->
                        AssistChip(
                            onClick = { onTagClick(tag) },
                            label = { Text("#$tag") },
                            shape = CircleShape,
                        )
                    }
                }
            }

            // Salon Request & How-To Steps
            if (design.steps.isNotEmpty()) {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            text = "How to Get This Look",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        design.steps.forEachIndexed { index, step ->
                            Row(
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = "${index + 1}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    )
                                }
                                Text(
                                    text = step,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                    }
                }
            }

            // Personal Salon Note (persisted in Room)
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(
                        text = "Personal Salon Note",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    OutlinedTextField(
                        value = noteDraft,
                        onValueChange = { noteDraft = it },
                        placeholder = { Text("e.g. Ask for shorter almond, shade #204…") },
                        minLines = 2,
                        maxLines = 4,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth(),
                    )
                    val isDirty = noteDraft.trim() != savedNote.trim()
                    if (isDirty) {
                        FilledTonalButton(
                            onClick = {
                                onSaveNote(noteDraft)
                                onShowMessage("Salon note saved")
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.align(Alignment.End),
                        ) {
                            Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Save note")
                        }
                    }
                }
            }

            if (related.isNotEmpty()) {
                Text(
                    text = "More Like This",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
        }

        if (related.isNotEmpty()) {
            LazyRow(
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 28.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                items(related, key = { it.id }) { relatedDesign ->
                    DesignCard(
                        design = relatedDesign,
                        isSaved = relatedDesign.id in savedIds,
                        onToggleSave = { onToggleSave(relatedDesign.id) },
                        onClick = { onDesignClick(relatedDesign.id) },
                        modifier = Modifier.width(156.dp),
                    )
                }
            }
        }
    }

    if (showNailTechCard) {
        ShowNailTechDialog(
            design = design,
            note = noteDraft.trim(),
            onDismiss = { showNailTechCard = false },
        )
    }

    if (showBoardSheet) {
        AddToBoardSheet(
            designTitle = design.title,
            designId = design.id,
            boards = boards,
            onToggleBoard = { board, currentlyInBoard ->
                onToggleBoard(board, currentlyInBoard)
            },
            onCreateBoardClick = {
                showBoardSheet = false
                showCreateBoard = true
            },
            onDismiss = { showBoardSheet = false },
        )
    }

    if (showCreateBoard) {
        CreateBoardDialog(
            onDismiss = { showCreateBoard = false },
            onCreate = { name, emoji ->
                onCreateBoard(name, emoji)
                showCreateBoard = false
                showBoardSheet = true
            },
        )
    }
}

/**
 * Full-screen uncluttered salon reference brief designed to hand straight to your nail technician.
 */
@Composable
private fun ShowNailTechDialog(
    design: NailDesign,
    note: String,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text(
                            text = "Salon Brief",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                        Text(
                            text = "Show this screen to your nail tech",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    ) {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Rounded.Close, contentDescription = "Close salon brief")
                        }
                    }
                }

                // Large crisp photo
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(270.dp)
                        .clip(RoundedCornerShape(26.dp)),
                ) {
                    DesignArtwork(design = design, modifier = Modifier.fillMaxSize())
                }

                // Shape & Finish callout card
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        NailShapeIcon(shape = design.shape, selected = true, size = 38.dp)
                        Column {
                            Text(
                                text = design.title,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = "${design.shape.label} Shape · ${design.finish.label} Finish · ~${design.durationMinutes} min",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }

                // Glossy Polish Formula droplets
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    design.colors.forEach { swatch ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            LacquerDroplet(color = swatch, size = 46.dp)
                            Text(
                                text = swatch.toHexString(),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                        }
                    }
                }

                // Personal Note or Steps
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = if (note.isNotBlank()) "Client Note" else "Technique Summary",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                        if (note.isNotBlank()) {
                            Text(
                                text = note,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                        } else {
                            design.steps.forEachIndexed { idx, step ->
                                Text(
                                    text = "${idx + 1}. $step",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onBackground,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FloatingIconButton(
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f),
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(36.dp),
        ) {
            icon()
        }
    }
}

@ThemePreviews
@Composable
private fun DesignDetailScreenPreview() {
    NailSkyTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            DesignDetailScreen(
                design = sampleDesigns[0],
                isSaved = true,
                savedIds = listOf(1, 5),
                savedNote = "Ask for sheer pink base with thin white tips",
                boards = listOf(BoardWithDesigns(1, "Wedding", "💖", listOf(1))),
                onBack = {},
                onToggleSave = {},
                onSaveNote = {},
                onToggleBoard = { _, _ -> },
                onCreateBoard = { _, _ -> },
                onTryInStudio = {},
                onTagClick = {},
                onDesignClick = {},
                onShowMessage = {},
            )
        }
    }
}
