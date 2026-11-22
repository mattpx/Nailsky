package com.nailsky.app.data

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

enum class Category(val label: String) {
    ALL("All"),
    FRENCH("French"),
    FLORAL("Floral"),
    GLITTER("Glitter"),
    MINIMAL("Minimal"),
    OMBRE("Ombre"),
    SEASONAL("Seasonal"),
}

/**
 * A nail design. In Phase 1, [colors] define a gradient placeholder tile instead of a real image.
 *
 * @param heightRatio tile height / width, which gives the staggered grid varied heights.
 */
@Immutable
data class NailDesign(
    val id: Int,
    val title: String,
    val category: Category,
    val colors: List<Color>,
    val heightRatio: Float,
    val trending: Boolean = false,
)
