package com.nailsky.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nailsky.app.data.NailDesign
import com.nailsky.app.data.sampleDesigns
import com.nailsky.app.ui.components.DesignCard
import com.nailsky.app.ui.theme.NailSkyTheme
import com.nailsky.app.ui.theme.ThemePreviews

@Composable
fun SavedScreen(
    savedIds: List<Int>,
    onToggleSave: (Int) -> Unit,
    modifier: Modifier = Modifier,
    designs: List<NailDesign> = sampleDesigns,
) {
    val saved = remember(savedIds.toList(), designs) {
        designs.filter { it.id in savedIds }
    }

    Column(modifier.fillMaxSize()) {
        ScreenTopBar(title = "Saved")
        if (saved.isEmpty()) {
            EmptyState(
                icon = Icons.Rounded.FavoriteBorder,
                title = "No saved designs yet",
                message = "Tap the heart on any design to keep it here.",
            )
        } else {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalItemSpacing = 16.dp,
            ) {
                items(saved, key = { it.id }) { design ->
                    DesignCard(
                        design = design,
                        isSaved = true,
                        onToggleSave = { onToggleSave(design.id) },
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
            SavedScreen(savedIds = listOf(1, 3, 5, 9), onToggleSave = {})
        }
    }
}

@ThemePreviews
@Composable
private fun SavedScreenEmptyPreview() {
    NailSkyTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            SavedScreen(savedIds = emptyList(), onToggleSave = {})
        }
    }
}
