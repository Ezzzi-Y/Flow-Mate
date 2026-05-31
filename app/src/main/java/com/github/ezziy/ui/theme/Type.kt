package com.github.ezziy.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.github.ezziy.R

private val AppFontFamily = FontFamily(
    Font(R.font.font_light, FontWeight.Light),
    Font(R.font.font, FontWeight.Normal),
    Font(R.font.font_medium, FontWeight.Medium),
    Font(R.font.font_bold, FontWeight.Bold)
)

private val BaseTypography = Typography()

private fun withAppFont(style: TextStyle) = style.copy(fontFamily = AppFontFamily)

val Typography = Typography(
    displayLarge = withAppFont(BaseTypography.displayLarge),
    displayMedium = withAppFont(BaseTypography.displayMedium),
    displaySmall = withAppFont(BaseTypography.displaySmall),
    headlineLarge = withAppFont(BaseTypography.headlineLarge),
    headlineMedium = withAppFont(BaseTypography.headlineMedium),
    headlineSmall = withAppFont(BaseTypography.headlineSmall),
    titleLarge = withAppFont(BaseTypography.titleLarge),
    titleMedium = withAppFont(BaseTypography.titleMedium),
    titleSmall = withAppFont(BaseTypography.titleSmall),
    bodyLarge = withAppFont(BaseTypography.bodyLarge),
    bodyMedium = withAppFont(BaseTypography.bodyMedium),
    bodySmall = withAppFont(BaseTypography.bodySmall),
    labelLarge = withAppFont(BaseTypography.labelLarge),
    labelMedium = withAppFont(BaseTypography.labelMedium),
    labelSmall = withAppFont(BaseTypography.labelSmall)
)