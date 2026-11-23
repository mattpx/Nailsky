package com.nailsky.app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nailsky.app.data.NailDesign
import com.nailsky.app.data.NailPassportState
import com.nailsky.app.data.ThemeMode
import com.nailsky.app.data.currentEpochDay
import com.nailsky.app.data.sampleDesigns
import com.nailsky.app.ui.components.DesignArtwork
import com.nailsky.app.ui.theme.NailSkyTheme
import com.nailsky.app.ui.theme.ThemePreviews

@Composable
fun ProfileScreen(
    displayName: String,
    onDisplayNameChange: (String) -> Unit,
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    passport: NailPassportState,
    onLogNewManicure: (setTitle: String, cycleDays: Int) -> Unit,
    onCheckInCuticle: () -> Unit,
    savedCount: Int,
    boardsCount: Int,
    customSetsCount: Int,
    recentViewIds: List<Int>,
    onDesignClick: (Int) -> Unit,
    onOpenSaved: () -> Unit,
    onClearRecentViews: () -> Unit,
    modifier: Modifier = Modifier,
    designs: List<NailDesign> = sampleDesigns,
) {
    var showEditName by remember { mutableStateOf(false) }
    var showLogManicure by remember { mutableStateOf(false) }

    val today = remember { currentEpochDay() }
    val designById = remember(designs) { designs.associateBy { it.id } }
    val recentDesigns = remember(recentViewIds, designById) {
        recentViewIds.mapNotNull { designById[it] }
    }

    Column(modifier.fillMaxSize()) {
        ScreenTopBar(title = "Nail Passport")
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .padding(bottom = 112.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ProfileHeader(
                displayName = displayName,
                savedCount = savedCount,
                onEditClick = { showEditName = true },
            )

            // Signature Nail Passport Fill-In Countdown Ring Card
            NailPassportRingCard(
                passport = passport,
                todayEpochDay = today,
                onLogNewClick = { showLogManicure = true },
            )

            // Daily Cuticle Care Check-In Card
            CuticleCareCard(
                streak = passport.cuticleStreak,
                checkedToday = passport.isCuticleCheckedToday(today),
                onCheckIn = onCheckInCuticle,
            )

            StatsRow(
                savedCount = savedCount,
                boardsCount = boardsCount,
                studioCount = customSetsCount,
                onSavedClick = onOpenSaved,
            )

            if (recentDesigns.isNotEmpty()) {
                RecentlyViewedSection(
                    recentDesigns = recentDesigns,
                    onDesignClick = onDesignClick,
                    onClear = onClearRecentViews,
                )
            }

            AppearanceCard(themeMode = themeMode, onThemeModeChange = onThemeModeChange)

            AboutCard()
        }
    }

    if (showEditName) {
        EditNameDialog(
            currentName = displayName,
            onDismiss = { showEditName = false },
            onSave = { newName ->
                onDisplayNameChange(newName)
                showEditName = false
            },
        )
    }

    if (showLogManicure) {
        LogManicureDialog(
            currentSetTitle = passport.currentSetTitle,
            currentCycleDays = passport.fillCycleDays,
            onDismiss = { showLogManicure = false },
            onConfirm = { title, cycle ->
                onLogNewManicure(title, cycle)
                showLogManicure = false
            },
        )
    }
}

@Composable
private fun NailPassportRingCard(
    passport: NailPassportState,
    todayEpochDay: Long,
    onLogNewClick: () -> Unit,
) {
    val currentDay = passport.currentDay(todayEpochDay)
    val daysLeft = passport.daysUntilFill(todayEpochDay)
    val animatedProgress by animateFloatAsState(
        targetValue = passport.progress(todayEpochDay),
        animationSpec = tween(700),
        label = "passport_ring",
    )
    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary

    Card(
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                modifier = Modifier.size(164.dp),
                contentAlignment = Alignment.Center,
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 12.dp.toPx()
                    drawArc(
                        color = trackColor,
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                    )
                    drawArc(
                        brush = Brush.sweepGradient(
                            colors = listOf(primaryColor, secondaryColor, primaryColor),
                        ),
                        startAngle = -90f,
                        sweepAngle = 360f * animatedProgress,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Day $currentDay of ${passport.fillCycleDays}",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = if (daysLeft == 0) "Time for a fill-in!" else "Fill in $daysLeft days",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Current Set · ${passport.currentSetTitle}",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            FilledTonalButton(
                onClick = onLogNewClick,
                shape = CircleShape,
            ) {
                Icon(Icons.Rounded.Spa, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Log Fresh Manicure")
            }
        }
    }
}

@Composable
private fun CuticleCareCard(
    streak: Int,
    checkedToday: Boolean,
    onCheckIn: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.WaterDrop,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Daily Cuticle Care",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = if (streak == 0) {
                            "Keep cuticles hydrated for longer-lasting gel"
                        } else {
                            "$streak-day hydration streak ✨"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Spacer(Modifier.width(8.dp))
            FilledTonalButton(
                onClick = onCheckIn,
                enabled = !checkedToday,
                shape = CircleShape,
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
            ) {
                if (checkedToday) {
                    Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Done")
                } else {
                    Text("Check In")
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    displayName: String,
    savedCount: Int,
    onEditClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary),
                    ),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Rounded.Person,
                contentDescription = "Avatar",
                tint = Color.White,
                modifier = Modifier.size(34.dp),
            )
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = displayName,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = if (savedCount == 1) "1 saved look" else "$savedCount saved looks",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        IconButton(onClick = onEditClick) {
            Icon(
                imageVector = Icons.Rounded.Edit,
                contentDescription = "Edit display name",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun StatsRow(
    savedCount: Int,
    boardsCount: Int,
    studioCount: Int,
    onSavedClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        StatCard(
            value = savedCount.toString(),
            label = "Saved",
            onClick = onSavedClick,
            modifier = Modifier.weight(1f),
        )
        StatCard(
            value = boardsCount.toString(),
            label = "Boards",
            onClick = onSavedClick,
            modifier = Modifier.weight(1f),
        )
        StatCard(
            value = studioCount.toString(),
            label = "Studio Sets",
            onClick = onSavedClick,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun StatCard(
    value: String,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun RecentlyViewedSection(
    recentDesigns: List<NailDesign>,
    onDesignClick: (Int) -> Unit,
    onClear: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(vertical = 14.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.History,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "Recently Viewed",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
                TextButton(onClick = onClear) {
                    Text("Clear")
                }
            }
            Spacer(Modifier.height(8.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(recentDesigns, key = { it.id }) { design ->
                    Card(
                        onClick = { onDesignClick(design.id) },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .width(108.dp)
                            .height(126.dp),
                    ) {
                        DesignArtwork(
                            design = design,
                            modifier = Modifier.fillMaxSize(),
                        ) {
                            Text(
                                text = design.title,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .fillMaxWidth()
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.65f)),
                                        ),
                                    )
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AppearanceCard(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.Palette,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "Appearance",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Switch between Warm Pearl Light and Glazed Obsidian Dark.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(16.dp))
            val modes = ThemeMode.entries
            SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                modes.forEachIndexed { index, mode ->
                    SegmentedButton(
                        selected = mode == themeMode,
                        onClick = { onThemeModeChange(mode) },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = modes.size),
                        colors = SegmentedButtonDefaults.colors(
                            activeContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            activeContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        ),
                    ) {
                        Text(mode.label)
                    }
                }
            }
        }
    }
}

@Composable
private fun AboutCard() {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Rounded.AutoAwesome,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary,
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    text = "NailSky v0.3.0 · Studio Edition",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "Interactive Hand Studio, Nail Passport, and local Room moodboards.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun LogManicureDialog(
    currentSetTitle: String,
    currentCycleDays: Int,
    onDismiss: () -> Unit,
    onConfirm: (setTitle: String, cycleDays: Int) -> Unit,
) {
    var title by remember { mutableStateOf(currentSetTitle) }
    var cycleDays by remember { mutableIntStateOf(currentCycleDays) }
    val options = listOf(14, 21, 28)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Fresh Manicure") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { if (it.length <= 32) title = it },
                    label = { Text("Set name or shade") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = "Fill-in cycle",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    options.forEach { days ->
                        FilterChip(
                            selected = cycleDays == days,
                            onClick = { cycleDays = days },
                            label = { Text("$days days") },
                            shape = CircleShape,
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(title, cycleDays) }) {
                Text("Start Day 1")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}

@Composable
private fun EditNameDialog(
    currentName: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
) {
    var name by remember { mutableStateOf(currentName) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Display name") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { if (it.length <= 28) name = it },
                singleLine = true,
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(name) },
                enabled = name.isNotBlank(),
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
private fun ProfileScreenPreview() {
    NailSkyTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            ProfileScreen(
                displayName = "Nail Lover",
                onDisplayNameChange = {},
                themeMode = ThemeMode.SYSTEM,
                onThemeModeChange = {},
                passport = NailPassportState(),
                onLogNewManicure = { _, _ -> },
                onCheckInCuticle = {},
                savedCount = 5,
                boardsCount = 2,
                customSetsCount = 3,
                recentViewIds = listOf(1, 2, 5),
                onDesignClick = {},
                onOpenSaved = {},
                onClearRecentViews = {},
            )
        }
    }
}
