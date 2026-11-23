package com.nailsky.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Bookmarks
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nailsky.app.data.BoardWithDesigns
import com.nailsky.app.data.CustomNailSet
import com.nailsky.app.data.NailDesign
import com.nailsky.app.data.sampleDesigns
import com.nailsky.app.ui.components.CreateBoardDialog
import com.nailsky.app.ui.components.DesignArtwork
import com.nailsky.app.ui.components.DesignCard
import com.nailsky.app.ui.components.MiniSetPreview
import com.nailsky.app.ui.components.horizontalBleed
import com.nailsky.app.ui.theme.NailSkyTheme
import com.nailsky.app.ui.theme.ThemePreviews
import androidx.compose.foundation.lazy.staggeredgrid.items as gridItems

private val ScreenPadding = 16.dp

@Composable
fun SavedScreen(
    savedIds: List<Int>,
    boards: List<BoardWithDesigns>,
    customSets: List<CustomNailSet>,
    onToggleSave: (Int) -> Unit,
    onDesignClick: (Int) -> Unit,
    onCreateBoard: (name: String, emoji: String) -> Unit,
    onDeleteBoard: (Long) -> Unit,
    onOpenCustomSet: (CustomNailSet) -> Unit,
    onDeleteCustomSet: (Long) -> Unit,
    modifier: Modifier = Modifier,
    designs: List<NailDesign> = sampleDesigns,
) {
    var selectedBoardId by rememberSaveable { mutableStateOf<Long?>(null) }
    var showCreateBoard by remember { mutableStateOf(false) }
    var boardToDelete by remember { mutableStateOf<BoardWithDesigns?>(null) }

    val selectedBoard = remember(boards, selectedBoardId) {
        boards.firstOrNull { it.id == selectedBoardId }
    }

    LaunchedEffect(boards, selectedBoardId) {
        if (selectedBoardId != null && boards.none { it.id == selectedBoardId }) {
            selectedBoardId = null
        }
    }

    val designById = remember(designs) { designs.associateBy { it.id } }

    val displayedDesigns = remember(savedIds, selectedBoard, designById) {
        val ids = selectedBoard?.designIds ?: savedIds
        ids.mapNotNull { designById[it] }
    }

    Column(modifier.fillMaxSize()) {
        ScreenTopBar(
            title = "Moodboards",
            actions = {
                IconButton(onClick = { showCreateBoard = true }) {
                    Icon(Icons.Rounded.Add, contentDescription = "Create board")
                }
            },
        )

        if (savedIds.isEmpty() && boards.isEmpty() && customSets.isEmpty()) {
            EmptyState(
                icon = Icons.Rounded.FavoriteBorder,
                title = "Your nail moodboard awaits",
                message = "Save looks you love, organize them into boards, or craft your own set in the Studio.",
                actionLabel = "Create your first board",
                onAction = { showCreateBoard = true },
            )
        } else {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = ScreenPadding,
                    end = ScreenPadding,
                    top = 4.dp,
                    bottom = 112.dp,
                ),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalItemSpacing = 14.dp,
            ) {
                // 2x2 Photo-Collage Moodboards Row
                item(key = "boards_carousel", span = StaggeredGridItemSpan.FullLine) {
                    LazyRow(
                        modifier = Modifier.horizontalBleed(ScreenPadding),
                        contentPadding = PaddingValues(horizontal = ScreenPadding),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        item(key = "all_collage") {
                            MoodboardCollageCard(
                                title = "All Saved",
                                emoji = "💖",
                                count = savedIds.size,
                                previewDesigns = savedIds.take(4).mapNotNull { designById[it] },
                                selected = selectedBoardId == null,
                                onClick = { selectedBoardId = null },
                            )
                        }

                        items(boards, key = { it.id }) { board ->
                            MoodboardCollageCard(
                                title = board.name,
                                emoji = board.emoji,
                                count = board.designIds.size,
                                previewDesigns = board.designIds.take(4).mapNotNull { designById[it] },
                                selected = selectedBoardId == board.id,
                                onClick = { selectedBoardId = board.id },
                            )
                        }

                        item(key = "new_board_tile") {
                            Surface(
                                onClick = { showCreateBoard = true },
                                shape = RoundedCornerShape(20.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                modifier = Modifier
                                    .width(126.dp)
                                    .height(158.dp),
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Add,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(28.dp),
                                    )
                                    Spacer(Modifier.height(6.dp))
                                    Text(
                                        text = "New Board",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.onSurface,
                                    )
                                }
                            }
                        }
                    }
                }

                // My Studio Sets (custom 5-finger designs created in Studio)
                if (selectedBoard == null && customSets.isNotEmpty()) {
                    item(key = "studio_sets_header", span = StaggeredGridItemSpan.FullLine) {
                        SectionHeader(
                            title = "My Studio Sets",
                            trailing = "${customSets.size} custom",
                        )
                    }
                    item(key = "studio_sets_row", span = StaggeredGridItemSpan.FullLine) {
                        LazyRow(
                            modifier = Modifier.horizontalBleed(ScreenPadding),
                            contentPadding = PaddingValues(horizontal = ScreenPadding),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            items(customSets, key = { it.id }) { set ->
                                CustomSetCard(
                                    set = set,
                                    onClick = { onOpenCustomSet(set) },
                                    onDelete = { onDeleteCustomSet(set.id) },
                                )
                            }
                        }
                    }
                }

                // Active board banner or section header
                item(key = "saved_section_header", span = StaggeredGridItemSpan.FullLine) {
                    if (selectedBoard != null) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 1.dp,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Row(
                                modifier = Modifier.padding(start = 14.dp, end = 6.dp, top = 6.dp, bottom = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(
                                    text = "${selectedBoard.emoji} ${selectedBoard.name} (${displayedDesigns.size})",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                                IconButton(onClick = { boardToDelete = selectedBoard }) {
                                    Icon(
                                        imageVector = Icons.Rounded.DeleteOutline,
                                        contentDescription = "Delete board",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        }
                    } else {
                        SectionHeader(
                            title = "Saved Looks",
                            trailing = "${displayedDesigns.size} designs",
                        )
                    }
                }

                if (displayedDesigns.isEmpty()) {
                    item(key = "board_empty", span = StaggeredGridItemSpan.FullLine) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Bookmarks,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(32.dp),
                                )
                                Text(
                                    text = if (selectedBoard != null) {
                                        "${selectedBoard.name} has no looks yet"
                                    } else {
                                        "No saved looks yet"
                                    },
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                                Text(
                                    text = "Tap the heart on any design or choose Add to Board from a detail page.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                } else {
                    gridItems(displayedDesigns, key = { it.id }) { design ->
                        DesignCard(
                            design = design,
                            isSaved = design.id in savedIds,
                            onToggleSave = { onToggleSave(design.id) },
                            onClick = { onDesignClick(design.id) },
                            modifier = Modifier.animateItem(),
                        )
                    }
                }
            }
        }
    }

    if (showCreateBoard) {
        CreateBoardDialog(
            onDismiss = { showCreateBoard = false },
            onCreate = { name, emoji ->
                onCreateBoard(name, emoji)
                showCreateBoard = false
            },
        )
    }

    boardToDelete?.let { target ->
        AlertDialog(
            onDismissRequest = { boardToDelete = null },
            title = { Text("Delete \"${target.name}\"?") },
            text = { Text("Your saved designs will still remain in All Saved.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteBoard(target.id)
                        boardToDelete = null
                    },
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { boardToDelete = null }) {
                    Text("Cancel")
                }
            },
        )
    }
}

/** 2×2 photo-collage moodboard cover card inspired by luxury beauty apps. */
@Composable
private fun MoodboardCollageCard(
    title: String,
    emoji: String,
    count: Int,
    previewDesigns: List<NailDesign>,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 3.dp else 1.dp),
        modifier = Modifier.width(136.dp),
    ) {
        Column(Modifier.padding(8.dp)) {
            // 2x2 photo grid
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(14.dp)),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    CollageCell(previewDesigns.getOrNull(0), Modifier.weight(1f))
                    CollageCell(previewDesigns.getOrNull(1), Modifier.weight(1f))
                }
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    CollageCell(previewDesigns.getOrNull(2), Modifier.weight(1f))
                    CollageCell(previewDesigns.getOrNull(3), Modifier.weight(1f))
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = "$emoji $title",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = if (count == 1) "1 look" else "$count looks",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun CollageCell(design: NailDesign?, modifier: Modifier = Modifier) {
    if (design != null) {
        DesignArtwork(design = design, modifier = modifier.fillMaxSize())
    } else {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
        )
    }
}

@Composable
private fun CustomSetCard(
    set: CustomNailSet,
    onClick: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.width(172.dp),
    ) {
        Column(Modifier.padding(12.dp)) {
            MiniSetPreview(
                shape = set.shape,
                effect = set.effect,
                colors = set.nailColors,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(62.dp),
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = set.name,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "${set.shape.label} · ${set.effect.label}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.DeleteOutline,
                        contentDescription = "Delete custom set",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }
    }
}

@ThemePreviews
@Composable
private fun SavedScreenPreview() {
    NailSkyTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            SavedScreen(
                savedIds = listOf(1, 3, 5, 9),
                boards = listOf(
                    BoardWithDesigns(1, "Wedding", "💖", listOf(1, 9)),
                    BoardWithDesigns(2, "Summer", "☁️", listOf(5)),
                ),
                customSets = emptyList(),
                onToggleSave = {},
                onDesignClick = {},
                onCreateBoard = { _, _ -> },
                onDeleteBoard = {},
                onOpenCustomSet = {},
                onDeleteCustomSet = {},
            )
        }
    }
}
