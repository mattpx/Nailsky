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
import androidx.compose.material3.TextButton
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
import com.nailsky.app.data.ColorMood
import com.nailsky.app.data.ExploreFilter
import com.nailsky.app.data.NailDesign
import com.nailsky.app.data.NailShape
import com.nailsky.app.data.filterDesigns
import com.nailsky.app.data.sampleDesigns
import com.nailsky.app.ui.components.CategoryChips
import com.nailsky.app.ui.components.ColorMoodStrip
import com.nailsky.app.ui.components.DesignCard
import com.nailsky.app.ui.components.EditorialHeroCard
import com.nailsky.app.ui.components.NailShapeStrip
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
    onDesignClick: (Int) -> Unit,
    onSearchClick: () -> Unit,
    onTryDesignInStudio: (NailDesign) -> Unit,
    modifier: Modifier = Modifier,
    designs: List<NailDesign> = sampleDesigns,
) {
    var selectedCategory by rememberSaveable { mutableStateOf(Category.ALL) }
    var selectedShape by rememberSaveable { mutableStateOf<NailShape?>(null) }
    var selectedMood by rememberSaveable { mutableStateOf<ColorMood?>(null) }

    val heroDesign = remember(designs) {
        designs.maxByOrNull { it.likesCount } ?: designs.first()
    }

    val filtered = remember(selectedCategory, selectedShape, selectedMood, designs) {
        filterDesigns(
            designs = designs,
            filter = ExploreFilter(
                category = selectedCategory,
                shape = selectedShape,
                colorMood = selectedMood,
            ),
        )
    }
    val trending = remember(filtered) {
        filtered.filter { it.trending && it.id != heroDesign.id }.ifEmpty { filtered.take(4) }
    }
    val hasActiveFilter = selectedCategory != Category.ALL || selectedShape != null || selectedMood != null

    Column(modifier.fillMaxSize()) {
        NailSkyTopBar(onSearchClick = onSearchClick)

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
            // Editorial Hero Look of the Week
            if (!hasActiveFilter) {
                item(key = "hero", span = StaggeredGridItemSpan.FullLine) {
                    EditorialHeroCard(
                        design = heroDesign,
                        isSaved = heroDesign.id in savedIds,
                        onToggleSave = { onToggleSave(heroDesign.id) },
                        onClick = { onDesignClick(heroDesign.id) },
                        onTryInStudio = { onTryDesignInStudio(heroDesign) },
                    )
                }
            }

            // Today's Nail Mood glossy polish bubbles
            item(key = "mood_header", span = StaggeredGridItemSpan.FullLine) {
                SectionHeader(
                    title = "Today's Nail Mood",
                    trailing = selectedMood?.label,
                )
            }
            item(key = "mood_strip", span = StaggeredGridItemSpan.FullLine) {
                ColorMoodStrip(
                    selectedMood = selectedMood,
                    onSelectMood = { selectedMood = it },
                    modifier = Modifier.horizontalBleed(ScreenPadding),
                )
            }

            // Visual Nail Shape silhouettes
            item(key = "shape_strip", span = StaggeredGridItemSpan.FullLine) {
                NailShapeStrip(
                    selectedShape = selectedShape,
                    onSelectShape = { selectedShape = it },
                    modifier = Modifier.horizontalBleed(ScreenPadding),
                )
            }

            // Category pills
            item(key = "chips", span = StaggeredGridItemSpan.FullLine) {
                CategoryChips(
                    selected = selectedCategory,
                    onSelected = { selectedCategory = it },
                    modifier = Modifier.horizontalBleed(ScreenPadding),
                )
            }

            if (!hasActiveFilter && trending.isNotEmpty()) {
                item(key = "trending_header", span = StaggeredGridItemSpan.FullLine) {
                    SectionHeader(title = "Trending Now")
                }
                item(key = "trending_row", span = StaggeredGridItemSpan.FullLine) {
                    LazyRow(
                        modifier = Modifier.horizontalBleed(ScreenPadding),
                        contentPadding = PaddingValues(horizontal = ScreenPadding),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        items(trending, key = { it.id }) { design ->
                            TrendingCard(
                                design = design,
                                isSaved = design.id in savedIds,
                                onToggleSave = { onToggleSave(design.id) },
                                onClick = { onDesignClick(design.id) },
                            )
                        }
                    }
                }
            }

            item(key = "inspiration_header", span = StaggeredGridItemSpan.FullLine) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = if (hasActiveFilter) "Curated Matches" else "Lookbook",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    if (hasActiveFilter) {
                        TextButton(
                            onClick = {
                                selectedCategory = Category.ALL
                                selectedShape = null
                                selectedMood = null
                            },
                        ) {
                            Text("Clear")
                        }
                    } else {
                        Text(
                            text = "${filtered.size} looks",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            gridItems(filtered, key = { it.id }) { design ->
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

@Composable
internal fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    trailing: String? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 6.dp),
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
            HomeScreen(
                savedIds = listOf(2, 5),
                onToggleSave = {},
                onDesignClick = {},
                onSearchClick = {},
                onTryDesignInStudio = {},
            )
        }
    }
}
