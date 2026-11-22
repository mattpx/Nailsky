package com.nailsky.app.data

import androidx.compose.ui.graphics.Color

private fun gradient(vararg argb: Long): List<Color> = argb.map { Color(it) }

val sampleDesigns: List<NailDesign> = listOf(
    NailDesign(1, "Classic French Tip", Category.FRENCH, gradient(0xFFFFF1F5, 0xFFF7A8C4), 1.25f, trending = true),
    NailDesign(2, "Cherry Blossom", Category.FLORAL, gradient(0xFFF7A8C4, 0xFFFFD6E5, 0xFFFFFFFF), 1.5f, trending = true),
    NailDesign(3, "Starry Night Glitter", Category.GLITTER, gradient(0xFF1E2A4A, 0xFF4A5C9A, 0xFF7EC8F2), 1.1f, trending = true),
    NailDesign(4, "Milky Nude", Category.MINIMAL, gradient(0xFFF5E6DA, 0xFFE8CFC0), 1.3f),
    NailDesign(5, "Sky Ombre", Category.OMBRE, gradient(0xFF7EC8F2, 0xFFF7A8C4), 1.6f, trending = true),
    NailDesign(6, "Autumn Leaves", Category.SEASONAL, gradient(0xFFE8A87C, 0xFFC0605A), 1.2f),
    NailDesign(7, "Lavender French", Category.FRENCH, gradient(0xFFE9E1FF, 0xFFB9A6F2), 1.45f),
    NailDesign(8, "Daisy Garden", Category.FLORAL, gradient(0xFFFFF6C7, 0xFFFFE07A, 0xFFFFFFFF), 1.15f),
    NailDesign(9, "Rose Gold Sparkle", Category.GLITTER, gradient(0xFFF6D1C1, 0xFFD99A8A, 0xFFF7A8C4), 1.55f, trending = true),
    NailDesign(10, "Negative Space Lines", Category.MINIMAL, gradient(0xFFFFFFFF, 0xFFD9E6F2), 1.2f),
    NailDesign(11, "Sunset Fade", Category.OMBRE, gradient(0xFFFFC48A, 0xFFF77F9C, 0xFF9D7BE0), 1.35f),
    NailDesign(12, "Winter Frost", Category.SEASONAL, gradient(0xFFE6F6FF, 0xFF9FD4F5, 0xFFFFFFFF), 1.5f, trending = true),
    NailDesign(13, "Midnight Micro French", Category.FRENCH, gradient(0xFFFDF7F2, 0xFF1E2A4A), 1.1f),
    NailDesign(14, "Tropical Hibiscus", Category.FLORAL, gradient(0xFFFF9A8B, 0xFFFF6A88, 0xFFFFC3A0), 1.4f),
    NailDesign(15, "Holo Chrome", Category.GLITTER, gradient(0xFFC9F0FF, 0xFFF3D1FF, 0xFFFFF5C2), 1.25f),
    NailDesign(16, "Mint Ombre", Category.OMBRE, gradient(0xFFC8F7E1, 0xFF7EC8F2), 1.3f),
    NailDesign(17, "Single Dot", Category.MINIMAL, gradient(0xFFFDE8EF, 0xFFFFFFFF), 1.6f),
    NailDesign(18, "Pumpkin Spice", Category.SEASONAL, gradient(0xFFF9C784, 0xFFE07A5F), 1.15f),
)
