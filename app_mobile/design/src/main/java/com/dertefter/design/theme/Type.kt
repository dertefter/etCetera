package com.dertefter.design.theme

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.dertefter.design.R

val GoogleSans = FontFamily(
    Font(
        resId = R.font.google_sans,
    )
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
val Typography = Typography(
    displayLarge = Typography().displayLarge.copy(fontFamily = GoogleSans),
    displayMedium = Typography().displayMedium.copy(fontFamily = GoogleSans),
    displaySmall = Typography().displaySmall.copy(fontFamily = GoogleSans),
    headlineLarge = Typography().headlineLarge.copy(fontFamily = GoogleSans),
    headlineMedium = Typography().headlineMedium.copy(fontFamily = GoogleSans),
    headlineSmall = Typography().headlineSmall.copy(fontFamily = GoogleSans),
    titleLarge = Typography().titleLarge.copy(fontFamily = GoogleSans),
    titleMedium = Typography().titleMedium.copy(fontFamily = GoogleSans),
    titleSmall = Typography().titleSmall.copy(fontFamily = GoogleSans),
    bodyLarge = Typography().bodyLarge.copy(fontFamily = GoogleSans),
    bodyMedium = Typography().bodyMedium.copy(fontFamily = GoogleSans),
    bodySmall = Typography().bodySmall.copy(fontFamily = GoogleSans),
    labelLarge = Typography().labelLarge.copy(fontFamily = GoogleSans),
    labelMedium = Typography().labelMedium.copy(fontFamily = GoogleSans),
    labelSmall = Typography().labelSmall.copy(fontFamily = GoogleSans),

    // Emphasized
    displayLargeEmphasized = TextStyle(
        fontFamily = GoogleSans,
        fontWeight = FontWeight.Medium,
        fontSize =  57.sp,
        lineHeight =  64.0.sp,
        letterSpacing =  0.sp,
    ),

    displayMediumEmphasized = TextStyle(
        fontFamily = GoogleSans,
        fontWeight = FontWeight.Medium,
        fontSize =  45.sp,
        lineHeight = 52.0.sp,
        letterSpacing =  0.sp,
    ),


    displaySmallEmphasized = TextStyle(
        fontFamily = GoogleSans,
        fontWeight =  FontWeight.Medium,
        fontSize =  36.sp,
        lineHeight =  44.0.sp,
        letterSpacing = 0.sp,
    ),

    headlineLargeEmphasized = TextStyle(
        fontFamily = GoogleSans,
        fontWeight = FontWeight.Medium,
        fontSize = 32.sp,
        lineHeight = 40.0.sp,
        letterSpacing = 0.sp,
    ),


    headlineMediumEmphasized = TextStyle(
        fontFamily = GoogleSans,
        fontWeight = FontWeight.Medium,
        fontSize = 28.sp,
        lineHeight = 36.0.sp,
        letterSpacing = 0.sp,
    ),

    headlineSmallEmphasized = TextStyle(
        fontFamily = GoogleSans,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp,
        lineHeight = 32.0.sp,
        letterSpacing = 0.sp,
    ),
    titleLargeEmphasized = TextStyle(
        fontFamily = GoogleSans,
        fontWeight = FontWeight.Medium,
        fontSize =  22.sp,
        lineHeight = 28.0.sp,
        letterSpacing =  0.sp,
    ),

    titleMediumEmphasized = TextStyle(
        fontFamily = GoogleSans,
        fontWeight =  FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 24.0.sp,
        letterSpacing = 0.15.sp,
    ),

    titleSmallEmphasized = TextStyle(
        fontFamily = GoogleSans,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 20.0.sp,
        letterSpacing = 0.1.sp,
    ),
    bodyLargeEmphasized = TextStyle(
        fontFamily = GoogleSans,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.0.sp,
        letterSpacing = 0.15.sp,
    ),
    bodyMediumEmphasized = TextStyle(
        fontFamily = GoogleSans,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.0.sp,
        letterSpacing = 0.25.sp,
    ),
    bodySmallEmphasized = TextStyle(
        fontFamily = GoogleSans,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.0.sp,
        letterSpacing = 0.4.sp,
    ),
    labelLargeEmphasized = TextStyle(
        fontFamily = GoogleSans,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 20.0.sp,
        letterSpacing = 0.1.sp,
    ),
    labelMediumEmphasized = TextStyle(
        fontFamily = GoogleSans,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        lineHeight = 16.0.sp,
        letterSpacing = 0.5.sp,
    ),
    labelSmallEmphasized = TextStyle(
        fontFamily = GoogleSans,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        lineHeight = 16.0.sp,
        letterSpacing = 0.5.sp,
    ),
)