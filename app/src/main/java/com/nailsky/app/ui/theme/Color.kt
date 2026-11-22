package com.nailsky.app.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

// Brand palette
val SkyBlue = Color(0xFF7EC8F2)
val BlushPink = Color(0xFFF7A8C4)
val DeepNavy = Color(0xFF1E2A4A)
val CloudWhite = Color(0xFFFFFFFF)

// Light supporting tones
val SkyMist = Color(0xFFF7FAFD)
val SkyContainer = Color(0xFFDDF1FC)
val BlushContainer = Color(0xFFFDE4EE)
val HeartPink = Color(0xFFE0679A)
val SlateText = Color(0xFF5A6580)
val SoftOutline = Color(0xFFC9D3E0)

// Dark palette
val NightBackground = Color(0xFF0F1626)
val NightSurface = Color(0xFF1A2338)
val NightPrimary = Color(0xFF8FD3FF)
val NightSecondary = Color(0xFFFFB3CF)
val NightOnSurface = Color(0xFFE8ECF5)
val NightSurfaceHigh = Color(0xFF232D45)
val NightSurfaceHighest = Color(0xFF2B3651)
val NightOnSurfaceVariant = Color(0xFFA9B3C8)
val NightOutline = Color(0xFF3A4662)

/** Deeper variant of a placeholder gradient color for dark mode. */
fun Color.deepen(amount: Float = 0.45f): Color = lerp(this, NightBackground, amount)
