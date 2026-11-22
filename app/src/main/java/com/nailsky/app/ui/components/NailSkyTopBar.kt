package com.nailsky.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.nailsky.app.R
import com.nailsky.app.ui.theme.LocalNailSkyDarkTheme
import com.nailsky.app.ui.theme.NailSkyTheme
import com.nailsky.app.ui.theme.ThemePreviews

@Composable
fun NailSkyTopBar(
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.nailsky_logo),
                    contentDescription = "NailSky logo",
                    modifier = Modifier.size(36.dp),
                )
                Spacer(Modifier.width(12.dp))
                NailSkyWordmark()
            }
        },
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(Icons.Rounded.Search, contentDescription = "Search")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            scrolledContainerColor = MaterialTheme.colorScheme.background,
        ),
    )
}

@Composable
fun NailSkyWordmark(modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    // Light mode uses the pink heart tone so "Sky" stays legible on a white background.
    val skyColor = if (LocalNailSkyDarkTheme.current) colors.primary else colors.tertiary
    Text(
        text = buildAnnotatedString {
            withStyle(SpanStyle(color = colors.onBackground)) { append("Nail") }
            withStyle(SpanStyle(color = skyColor)) { append("Sky") }
        },
        style = MaterialTheme.typography.titleLarge,
        modifier = modifier,
    )
}

@ThemePreviews
@Composable
private fun NailSkyTopBarPreview() {
    NailSkyTheme {
        NailSkyTopBar(onSearchClick = {})
    }
}
