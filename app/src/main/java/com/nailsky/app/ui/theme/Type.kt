package com.nailsky.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Base = Typography()

/**
 * Luxury editorial pairing: refined Serif display/section headings paired with crisp Sans-Serif
 * body and metadata controls.
 */
val NailSkyTypography = Typography(
    displaySmall = Base.displaySmall.copy(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Medium,
        letterSpacing = (-0.5).sp,
    ),
    headlineMedium = Base.headlineMedium.copy(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = (-0.3).sp,
    ),
    headlineSmall = Base.headlineSmall.copy(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = (-0.2).sp,
    ),
    titleLarge = Base.titleLarge.copy(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.sp,
    ),
    titleMedium = Base.titleMedium.copy(fontWeight = FontWeight.SemiBold),
    titleSmall = Base.titleSmall.copy(fontWeight = FontWeight.SemiBold),
    bodyLarge = Base.bodyLarge,
    bodyMedium = Base.bodyMedium,
    bodySmall = Base.bodySmall,
    labelLarge = Base.labelLarge.copy(fontWeight = FontWeight.SemiBold, letterSpacing = 0.2.sp),
    labelMedium = Base.labelMedium.copy(letterSpacing = 0.3.sp),
    labelSmall = Base.labelSmall.copy(letterSpacing = 0.3.sp),
)

val NailSkyShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(26.dp),
    extraLarge = RoundedCornerShape(34.dp),
)
