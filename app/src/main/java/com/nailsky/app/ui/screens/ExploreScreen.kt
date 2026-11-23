package com.nailsky.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nailsky.app.data.Category
import com.nailsky.app.data.ColorMood
import com.nailsky.app.data.ExploreFilter
import com.nailsky.app.data.NailDesign
import com.nailsky.app.data.NailFinish
import com.nailsky.app.data.NailShape
import com.nailsky.app.data.filterDesigns
import com.nailsky.app.data.sampleDesigns
import com.nailsky.app.data.trendingTags
import com.nailsky.app.data.weeklyTopDesigns
import com.nailsky.app.ui.components.ColorMoodStrip
import com.nailsky.app.ui.components.DesignArtwork
import com.nailsky.app.ui.components.DesignCard
import com.nailsky.app.ui.components.NailShapeStrip
import com.nailsky.app.ui.components.RankedLookCard
import com.nailsky.app.ui.components.horizontalBleed
import com.nailsky.app.ui.theme.NailSkyTheme
import com.nailsky.app.ui.theme.ThemePreviews
import androidx.compose.foundation.lazy.staggeredgrid.items as gridItems

private val ScreenPadding = 16.dp

@Composable
fun ExploreScreen(
    savedIds: List<Int>,
    onToggleSave: (Int) -> Unit,
    onDesignClick: (Int) -> Unit,
    initialQuery: String? = null,
    onConsumeInitialQuery: () -> Unit = {},
    modifier: Modifier = Modifier,
    designs: List<NailDesign> = sampleDesigns,
) {
    var query by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf(Category.ALL) }
    var selectedShape by rememberSaveable { mutableStateOf<NailShape?>(null) }
    var selectedFinish by rememberSaveable { mutableStateOf<NailFinish?>(null) }
    var selectedArtist by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedMood by rememberSaveable { mutableStateOf<ColorMood?>(null) }
    var showFilterTray by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(initialQuery) {
        if (initialQuery != null) {
            query = initialQuery
            selectedCategory = Category.ALL
            selectedShape = null
            selectedFinish = null
            selectedArtist = null
            selectedMood = null
            onConsumeInitialQuery()
        }
    }

    val filter = remember(query, selectedCategory, selectedShape, selectedFinish, selectedArtist, selectedMood) {
        ExploreFilter(
            query = query,
            category = selectedCategory,
            shape = selectedShape,
            finish = selectedFinish,
            artist = selectedArtist,
            colorMood = selectedMood,
        )
    }

    val results = remember(designs, filter) { filterDesigns(designs, filter) }
    val weeklyTop = remember(designs) { weeklyTopDesigns(designs, limit = 6) }
    val artists = remember(designs) {
        designs.groupBy { it.artist }.entries.sortedByDescending { it.value.size }
    }
    val stylePreviews = remember(designs) {
        Category.entries
            .filter { it != Category.ALL }
            .mapNotNull { cat ->
                val list = designs.filter { it.category == cat }
                list.firstOrNull()?.let { first -> Triple(cat, first, list.size) }
            }
    }

    val clearAll = {
        query = ""
        selectedCategory = Category.ALL
        selectedShape = null
        selectedFinish = null
        selectedArtist = null
        selectedMood = null
    }

    val focusManager = LocalFocusManager.current

    Column(modifier.fillMaxSize()) {
        ScreenTopBar(title = "Discover")

        // Search input + filter toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ScreenPadding)
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Search shade, vibe, artist…") },
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(Icons.Rounded.Close, contentDescription = "Clear search")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(28.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
            )

            Surface(
                onClick = { showFilterTray = !showFilterTray },
                shape = CircleShape,
                color = if (showFilterTray || filter.activeFilterCount > 0) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surface
                },
                tonalElevation = 1.dp,
                modifier = Modifier.size(52.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    BadgedBox(
                        badge = {
                            if (filter.activeFilterCount > 0) {
                                Badge(
                                    containerColor = MaterialTheme.colorScheme.tertiary,
                                    contentColor = MaterialTheme.colorScheme.onTertiary,
                                ) {
                                    Text("${filter.activeFilterCount}")
                                }
                            }
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Tune,
                            contentDescription = "Filters",
                            tint = if (showFilterTray || filter.activeFilterCount > 0) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            },
                        )
                    }
                }
            }
        }

        // Expandable Finish & Shape tray
        AnimatedVisibility(visible = showFilterTray) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ScreenPadding)
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(22.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 1.dp,
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Finish & Shape",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        if (filter.isActive) {
                            TextButton(onClick = clearAll) { Text("Reset") }
                        }
                    }

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(NailFinish.entries, key = { it.name }) { finish ->
                            FilterChip(
                                selected = selectedFinish == finish,
                                onClick = {
                                    selectedFinish = if (selectedFinish == finish) null else finish
                                },
                                label = { Text(finish.label) },
                                shape = CircleShape,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.secondary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onSecondary,
                                ),
                            )
                        }
                    }

                    NailShapeStrip(
                        selectedShape = selectedShape,
                        onSelectShape = { selectedShape = it },
                        contentPadding = PaddingValues(0.dp),
                    )
                }
            }
        }

        if (filter.isActive && results.isEmpty()) {
            EmptyState(
                icon = Icons.Rounded.SearchOff,
                title = "No matching looks",
                message = "Try another shade or clear one of your active filters.",
                actionLabel = "Clear search & filters",
                onAction = clearAll,
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
                // Glossy Shade Palette Bar (always tactile & immediate)
                item(key = "shade_header", span = StaggeredGridItemSpan.FullLine) {
                    SectionHeader(
                        title = "Pick Your Shade",
                        trailing = selectedMood?.label,
                    )
                }
                item(key = "shade_bar", span = StaggeredGridItemSpan.FullLine) {
                    ColorMoodStrip(
                        selectedMood = selectedMood,
                        onSelectMood = { selectedMood = it },
                        modifier = Modifier.horizontalBleed(ScreenPadding),
                    )
                }

                if (!filter.isActive) {
                    // Weekly Top Looks (#1, #2, #3...)
                    item(key = "top_header", span = StaggeredGridItemSpan.FullLine) {
                        SectionHeader(title = "Weekly Top Looks")
                    }
                    item(key = "top_row", span = StaggeredGridItemSpan.FullLine) {
                        LazyRow(
                            modifier = Modifier.horizontalBleed(ScreenPadding),
                            contentPadding = PaddingValues(horizontal = ScreenPadding),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            itemsIndexed(weeklyTop, key = { _, d -> d.id }) { idx, design ->
                                RankedLookCard(
                                    rank = idx + 1,
                                    design = design,
                                    isSaved = design.id in savedIds,
                                    onToggleSave = { onToggleSave(design.id) },
                                    onClick = { onDesignClick(design.id) },
                                )
                            }
                        }
                    }

                    // Browse by Style
                    item(key = "styles_header", span = StaggeredGridItemSpan.FullLine) {
                        SectionHeader(title = "Browse by Style")
                    }
                    item(key = "styles_row", span = StaggeredGridItemSpan.FullLine) {
                        LazyRow(
                            modifier = Modifier.horizontalBleed(ScreenPadding),
                            contentPadding = PaddingValues(horizontal = ScreenPadding),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            items(stylePreviews, key = { it.first.name }) { (category, previewDesign, count) ->
                                StyleCollectionCard(
                                    category = category,
                                    previewDesign = previewDesign,
                                    count = count,
                                    onClick = { selectedCategory = category },
                                )
                            }
                        }
                    }

                    // Featured Artists
                    item(key = "artists_header", span = StaggeredGridItemSpan.FullLine) {
                        SectionHeader(title = "Featured Nail Artists")
                    }
                    item(key = "artists_row", span = StaggeredGridItemSpan.FullLine) {
                        LazyRow(
                            modifier = Modifier.horizontalBleed(ScreenPadding),
                            contentPadding = PaddingValues(horizontal = ScreenPadding),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            items(artists, key = { it.key }) { (artist, artistDesigns) ->
                                ArtistChipCard(
                                    artist = artist,
                                    count = artistDesigns.size,
                                    onClick = { selectedArtist = artist },
                                )
                            }
                        }
                    }

                    // Trending Hashtags
                    item(key = "trending_tags", span = StaggeredGridItemSpan.FullLine) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            SectionHeader(title = "Trending Tags")
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                trendingTags.forEach { tag ->
                                    AssistChip(
                                        onClick = { query = tag },
                                        label = { Text("#$tag") },
                                        shape = CircleShape,
                                    )
                                }
                            }
                        }
                    }

                    item(key = "discover_header", span = StaggeredGridItemSpan.FullLine) {
                        SectionHeader(
                            title = "All Looks",
                            trailing = "${results.size} designs",
                        )
                    }
                } else {
                    item(key = "active_filter_summary", span = StaggeredGridItemSpan.FullLine) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = if (results.size == 1) "1 matching look" else "${results.size} matching looks",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onBackground,
                                )
                                TextButton(onClick = clearAll) {
                                    Text("Clear all")
                                }
                            }

                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                selectedMood?.let { mood ->
                                    RemovableFilterPill(
                                        label = mood.label,
                                        onRemove = { selectedMood = null },
                                    )
                                }
                                if (selectedCategory != Category.ALL) {
                                    RemovableFilterPill(
                                        label = selectedCategory.label,
                                        onRemove = { selectedCategory = Category.ALL },
                                    )
                                }
                                selectedShape?.let { shape ->
                                    RemovableFilterPill(
                                        label = shape.label,
                                        onRemove = { selectedShape = null },
                                    )
                                }
                                selectedFinish?.let { finish ->
                                    RemovableFilterPill(
                                        label = finish.label,
                                        onRemove = { selectedFinish = null },
                                    )
                                }
                                selectedArtist?.let { artist ->
                                    RemovableFilterPill(
                                        label = artist,
                                        onRemove = { selectedArtist = null },
                                    )
                                }
                            }
                        }
                    }
                }

                gridItems(results, key = { it.id }) { design ->
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

@Composable
private fun StyleCollectionCard(
    category: Category,
    previewDesign: NailDesign,
    count: Int,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .width(132.dp)
            .height(94.dp),
    ) {
        DesignArtwork(
            design = previewDesign,
            modifier = Modifier.fillMaxSize(),
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.68f)),
                        ),
                    )
                    .padding(horizontal = 12.dp, vertical = 10.dp),
            ) {
                Text(
                    text = category.label,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "$count looks",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.85f),
                )
            }
        }
    }
}

@Composable
private fun ArtistChipCard(
    artist: String,
    count: Int,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.secondaryContainer,
                            ),
                        ),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = artist.removePrefix("@").take(1).uppercase(),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
            Spacer(Modifier.width(10.dp))
            Column {
                Text(
                    text = artist,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "$count looks",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun RemovableFilterPill(
    label: String,
    onRemove: () -> Unit,
) {
    FilterChip(
        selected = true,
        onClick = onRemove,
        label = { Text(label) },
        trailingIcon = {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = "Remove $label filter",
                modifier = Modifier.size(16.dp),
            )
        },
        shape = CircleShape,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
            selectedTrailingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
    )
}

@Composable
internal fun ScreenTopBar(
    title: String,
    modifier: Modifier = Modifier,
    actions: @Composable () -> Unit = {},
) {
    TopAppBar(
        modifier = modifier,
        title = { Text(title, style = MaterialTheme.typography.headlineSmall) },
        actions = { actions() },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            scrolledContainerColor = MaterialTheme.colorScheme.background,
        ),
    )
}

@Composable
internal fun EmptyState(
    icon: ImageVector,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.secondaryContainer,
                            ),
                        ),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(44.dp),
                )
                Icon(
                    imageVector = Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(14.dp)
                        .size(16.dp),
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            if (actionLabel != null && onAction != null) {
                FilledTonalButton(onClick = onAction) {
                    Text(actionLabel)
                }
            }
        }
    }
}

@ThemePreviews
@Composable
private fun ExploreScreenPreview() {
    NailSkyTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            ExploreScreen(
                savedIds = listOf(1, 5),
                onToggleSave = {},
                onDesignClick = {},
            )
        }
    }
}
