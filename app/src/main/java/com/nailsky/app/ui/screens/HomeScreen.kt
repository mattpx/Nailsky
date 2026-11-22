package com.nailsky.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nailsky.app.data.Category
import com.nailsky.app.data.NailDesign
import com.nailsky.app.data.sampleDesigns
import com.nailsky.app.ui.components.CategoryChips
import com.nailsky.app.ui.components.DesignCard
import com.nailsky.app.ui.components.NailSkyTopBar
import com.nailsky.app.ui.components.TrendingCard
import com.nailsky.app.ui.components.horizontalBleed
import com.nailsky.app.ui.theme.NailSkyTheme
import com.nailsky.app.ui.theme.ThemePreviews
import androidx.compose.foundation.lazy.staggeredgrid.items as gridItems

private val ScreenPadding = 16.dp

@Composable
fun HomeScreen(
    savedIds: List<Int>,
    onToggleSave: (Int) -> Unit,
    modifier: Modifier = Modifier,
    designs: List<NailDesign> = sampleDesigns,
) {
    var selectedCategory by rememberSaveable { mutableStateOf(Category.ALL) }

    val filtered = remember(selectedCategory, designs) {
        if (selectedCategory == Category.ALL) designs
        else designs.filter { it.category == selectedCategory }
    }
    val trending = remember(filtered) {
        filtered.filter { it.trending }.ifEmpty { filtered.take(4) }
    }

    Column(modifier.fillMaxSize()) {
        NailSkyTopBar(onSearchClick = { /* Search arrives in a later phase */ })

        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = ScreenPadding,
                end = ScreenPadding,
                bottom = ScreenPadding,
            ),
            horizontalArrangement = Arrangement.spacedBy(ScreenPadding),
            verticalItemSpacing = ScreenPadding,
        ) {
            item(key = "chips", span = StaggeredGridItemSpan.FullLine) {
                CategoryChips(
                    selected = selectedCategory,
                    onSelected = { selectedCategory = it },
                    modifier = Modifier.horizontalBleed(ScreenPadding),
                )
            }

            if (trending.isNotEmpty()) {
                item(key = "trending_header", span = StaggeredGridItemSpan.FullLine) {
                    SectionHeader(title = "Trending")
                }
                item(key = "trending_row", span = StaggeredGridItemSpan.FullLine) {
                    LazyRow(
                        modifier = Modifier.horizontalBleed(ScreenPadding),
                        contentPadding = PaddingValues(horizontal = ScreenPadding),
                        horizontalArrangement = Arrangement.spacedBy(ScreenPadding),
                    ) {
                        items(trending, key = { it.id }) { design ->
                            TrendingCard(
                                design = design,
                                isSaved = design.id in savedIds,
                                onToggleSave = { onToggleSave(design.id) },
                            )
                        }
                    }
                }
            }

            item(key = "inspiration_header", span = StaggeredGridItemSpan.FullLine) {
                SectionHeader(
                    title = "Inspiration",
                    trailing = "${filtered.size} designs",
                )
            }

            gridItems(filtered, key = { it.id }) { design ->
                DesignCard(
                    design = design,
                    isSaved = design.id in savedIds,
                    onToggleSave = { onToggleSave(design.id) },
                )
            }
        }
    }
}

@Composable
internal fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    trailing: String? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
        if (trailing != null) {
            Text(
                text = trailing,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@ThemePreviews
@Composable
private fun HomeScreenPreview() {
    NailSkyTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            HomeScreen(savedIds = listOf(2, 5), onToggleSave = {})
        }
    }
}
