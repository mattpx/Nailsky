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

/** Tactile color-mood filter buckets displayed as glossy lacquer droplets. */
enum class ColorMood(val label: String, val swatch: Color, val keywords: List<String>) {
    BLUSH("Blush", Color(0xFFF7A8C4), listOf("pink", "blush", "sakura", "rose", "coral", "rosegold", "hibiscus", "cherry")),
    SKY("Sky", Color(0xFF7EC8F2), listOf("sky", "blue", "ice", "winter", "mint", "holo", "frost")),
    NUDE("Milky", Color(0xFFF2DFCE), listOf("nude", "milky", "clean", "classic", "french", "sheer", "minimal")),
    LAVENDER("Lilac", Color(0xFFCBB8F7), listOf("lavender", "lilac", "violet", "sunset", "holo", "pastel")),
    WARM("Amber", Color(0xFFE89A6C), listOf("autumn", "fall", "pumpkin", "warm", "yellow", "daisy", "coral")),
    MIDNIGHT("Midnight", Color(0xFF25345C), listOf("navy", "night", "dark", "midnight", "celestial")),
}

/** Studio art effect applied on top of polish colors in the Interactive Hand Studio. */
enum class StudioEffect(val label: String) {
    GLOSSY("Glossy"),
    CHROME("Chrome"),
    FRENCH("French"),
    AURA("Aura"),
    GLITTER("Glitter"),
    MATTE("Matte"),
}

/**
 * A nail art inspiration design.
 *
 * [imageUrl] is loaded via Coil with [colors] serving both as the glossy color palette droplets
 * and as the graceful offline/loading gradient fallback artwork.
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

/** A user-created custom 5-finger nail set built inside the Interactive Hand Studio. */
@Immutable
data class CustomNailSet(
    val id: Long = 0L,
    val name: String,
    val shape: NailShape,
    val lengthFactor: Float,
    val effect: StudioEffect,
    val skinToneIndex: Int,
    val nailColors: List<Color>,
    val createdAt: Long = 0L,
)

/** Returns an uppercase 6-digit hex string like `#F7A8C4`. */
fun Color.toHexString(): String = String.format("#%06X", 0xFFFFFF and toArgb())

fun List<Color>.encodeColorCsv(): String = joinToString(",") { it.toArgb().toString() }

fun decodeColorCsv(csv: String, fallback: Color = Color(0xFFF7B8CF)): List<Color> {
    val parsed = csv.split(",")
        .mapNotNull { it.trim().toIntOrNull()?.let { argb -> Color(argb) } }
    return if (parsed.size == 5) parsed else List(5) { parsed.getOrElse(it) { fallback } }
}
