package com.nailsky.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Bookmarks
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.unit.dp
import com.nailsky.app.data.BoardWithDesigns
import com.nailsky.app.data.NailDesign
import com.nailsky.app.data.sampleDesigns
import com.nailsky.app.ui.components.CreateBoardDialog
import com.nailsky.app.ui.components.DesignCard
import com.nailsky.app.ui.theme.NailSkyTheme
import com.nailsky.app.ui.theme.ThemePreviews

@Composable
fun SavedScreen(
    savedIds: List<Int>,
    boards: List<BoardWithDesigns>,
    onToggleSave: (Int) -> Unit,
    onDesignClick: (Int) -> Unit,
    onCreateBoard: (name: String, emoji: String) -> Unit,
    onDeleteBoard: (Long) -> Unit,
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
            title = "Saved",
            actions = {
                IconButton(onClick = { showCreateBoard = true }) {
                    Icon(Icons.Rounded.Add, contentDescription = "Create board")
                }
            },
        )

        // Boards filter bar
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item(key = "all_saved") {
                FilterChip(
                    selected = selectedBoardId == null,
                    onClick = { selectedBoardId = null },
                    label = { Text("All (${savedIds.size})") },
                    shape = CircleShape,
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                )
            }

            items(boards, key = { it.id }) { board ->
                FilterChip(
                    selected = selectedBoardId == board.id,
                    onClick = { selectedBoardId = board.id },
                    label = { Text("${board.emoji} ${board.name} (${board.designIds.size})") },
                    shape = CircleShape,
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                )
            }

            item(key = "new_board") {
                AssistChip(
                    onClick = { showCreateBoard = true },
                    label = { Text("New board") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                        )
                    },
                    shape = CircleShape,
                )
            }
        }

        // Selected board action banner
        if (selectedBoard != null) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 1.dp,
            ) {
                Row(
                    modifier = Modifier.padding(start = 14.dp, end = 6.dp, top = 6.dp, bottom = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "${selectedBoard.emoji} ${selectedBoard.name}",
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
        }

        if (displayedDesigns.isEmpty()) {
            if (selectedBoard != null) {
                EmptyState(
                    icon = Icons.Rounded.Bookmarks,
                    title = "${selectedBoard.name} is empty",
                    message = "Open any nail design and tap \"Boards\" to collect looks here.",
                )
            } else {
                EmptyState(
                    icon = Icons.Rounded.FavoriteBorder,
                    title = "No saved designs yet",
                    message = "Tap the heart on any design to keep it here — your saves stay stored on this device.",
                )
            }
        } else {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalItemSpacing = 16.dp,
            ) {
                items(displayedDesigns, key = { it.id }) { design ->
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
                onToggleSave = {},
                onDesignClick = {},
                onCreateBoard = { _, _ -> },
                onDeleteBoard = {},
            )
        }
    }
}

@ThemePreviews
@Composable
private fun SavedScreenEmptyPreview() {
    NailSkyTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            SavedScreen(
                savedIds = emptyList(),
                boards = emptyList(),
                onToggleSave = {},
                onDesignClick = {},
                onCreateBoard = { _, _ -> },
                onDeleteBoard = {},
            )
        }
    }
}
