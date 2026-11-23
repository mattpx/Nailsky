package com.nailsky.app.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

// Brand palette
val SkyBlue = Color(0xFF7EC8F2)
val BlushPink = Color(0xFFF7A8C4)
val DeepNavy = Color(0xFF1E2A4A)
val CloudWhite = Color(0xFFFFFFFF)

// Light atelier supporting tones (warm pearl & misty sky)
val SkyMist = Color(0xFFF8F6FA)
val PearlSurface = Color(0xFFFFFFFF)
val SkyContainer = Color(0xFFDDF1FC)
val BlushContainer = Color(0xFFFDE4EE)
val HeartPink = Color(0xFFE0679A)
val RoseGold = Color(0xFFC58D84)
val SlateText = Color(0xFF636B82)
val SoftOutline = Color(0xFFD4DAE6)

// Glazed obsidian dark palette
val NightBackground = Color(0xFF0C101B)
val NightSurface = Color(0xFF161C2E)
val NightPrimary = Color(0xFF8FD3FF)
val NightSecondary = Color(0xFFFFB3CF)
val NightOnSurface = Color(0xFFECEFF7)
val NightSurfaceHigh = Color(0xFF1F273E)
val NightSurfaceHighest = Color(0xFF28324E)
val NightOnSurfaceVariant = Color(0xFFA7B1C8)
val NightOutline = Color(0xFF34405D)

// Studio hand skin tones (fair to deep rich)
val StudioSkinTones = listOf(
    Color(0xFFF6DBC8),
    Color(0xFFEAC2A5),
    Color(0xFFD6A17C),
    Color(0xFFB17853),
    Color(0xFF7C4E34),
)

// Studio curated nail polish lacquers
data class StudioPolish(val name: String, val color: Color)

val StudioPolishes = listOf(
    StudioPolish("Ballet Blush", Color(0xFFF7B8CF)),
    StudioPolish("Sky Mist", Color(0xFF8CD3FA)),
    StudioPolish("Milky Cloud", Color(0xFFF9F3EE)),
    StudioPolish("Lavender Haze", Color(0xFFCBB8F7)),
    StudioPolish("Rose Syrup", Color(0xFFE87A9E)),
    StudioPolish("Peach Aura", Color(0xFFFFB79B)),
    StudioPolish("Cherry Glaze", Color(0xFFC8284B)),
    StudioPolish("Bordeaux", Color(0xFF6C1E33)),
    StudioPolish("Rose Gold", Color(0xFFD99A8A)),
    StudioPolish("Mint Sorbet", Color(0xFFAEEED1)),
    StudioPolish("Buttercup", Color(0xFFFFE387)),
    StudioPolish("Warm Oat", Color(0xFFE7CFC0)),
    StudioPolish("Espresso", Color(0xFF4A312C)),
    StudioPolish("Midnight", Color(0xFF1E2A4A)),
)

/** Deeper variant of a placeholder gradient color for dark mode. */
fun Color.deepen(amount: Float = 0.45f): Color = lerp(this, NightBackground, amount)
