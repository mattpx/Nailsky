package com.nailsky.app.data

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

enum class Category(val label: String) {
    ALL("All"),
    FRENCH("French"),
    FLORAL("Floral"),
    GLITTER("Glitter"),
    MINIMAL("Minimal"),
    OMBRE("Ombre"),
    SEASONAL("Seasonal"),
}

enum class NailShape(val label: String) {
    ALMOND("Almond"),
    COFFIN("Coffin"),
    OVAL("Oval"),
    SQUARE("Square"),
    STILETTO("Stiletto"),
    SHORT("Short"),
}

enum class NailFinish(val label: String) {
    GLOSSY("Glossy"),
    MATTE("Matte"),
    CHROME("Chrome"),
    GLITTER("Glitter"),
    SHEER("Sheer"),
}

enum class Difficulty(val label: String) {
    EASY("Easy"),
    MEDIUM("Medium"),
    SALON("Salon Pro"),
}

/**
 * A nail art inspiration design.
 *
 * [imageUrl] is loaded via Coil with [colors] serving both as the color palette swatches on the
 * detail screen and as the graceful offline/loading gradient fallback artwork.
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
    val imageUrl: String = "",
    val artist: String = "@nailsky.studio",
    val shape: NailShape = NailShape.ALMOND,
    val finish: NailFinish = NailFinish.GLOSSY,
    val difficulty: Difficulty = Difficulty.EASY,
    val durationMinutes: Int = 45,
    val description: String = "",
    val tags: List<String> = emptyList(),
    val steps: List<String> = emptyList(),
    val likesCount: Int = 120,
)

/** Returns an uppercase 6-digit hex string like `#F7A8C4`. */
fun Color.toHexString(): String = String.format("#%06X", 0xFFFFFF and toArgb())
