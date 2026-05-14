package com.gramavaxi.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary                = Primary,
    onPrimary              = OnPrimary,
    primaryContainer       = PrimaryContainer,
    onPrimaryContainer     = OnPrimaryContainer,
    inversePrimary         = InversePrimary,
    secondary              = Secondary,
    onSecondary            = OnSecondary,
    secondaryContainer     = SecondaryContainer,
    onSecondaryContainer   = OnSecondaryContainer,
    tertiary               = Tertiary,
    onTertiary             = OnTertiary,
    tertiaryContainer      = TertiaryContainer,
    onTertiaryContainer    = OnTertiaryContainer,
    error                  = Error,
    onError                = OnError,
    errorContainer         = ErrorContainer,
    onErrorContainer       = OnErrorContainer,
    background             = Background,
    onBackground           = OnBackground,
    surface                = Surface,
    onSurface              = OnSurface,
    surfaceVariant         = SurfaceVariant,
    onSurfaceVariant       = OnSurfaceVariant,
    inverseSurface         = InverseSurface,
    inverseOnSurface       = InverseOnSurface,
    outline                = Outline,
    outlineVariant         = OutlineVariant,
    surfaceTint            = SurfaceTint
)

private val DarkColorScheme = darkColorScheme(
    primary                = PrimaryFixedDim,
    onPrimary              = OnPrimaryFixed,
    primaryContainer       = PrimaryContainer,
    onPrimaryContainer     = PrimaryFixed,
    inversePrimary         = Primary,
    secondary              = SecondaryFixedDim,
    onSecondary            = OnSecondaryFixed,
    secondaryContainer     = Secondary,
    onSecondaryContainer   = SecondaryFixed,
    tertiary               = TertiaryFixedDim,
    onTertiary             = OnTertiaryFixed,
    tertiaryContainer      = Tertiary,
    onTertiaryContainer    = TertiaryFixed,
    error                  = Color(0xFFFFB4AB),
    onError                = Color(0xFF690005),
    errorContainer         = Color(0xFF93000A),
    onErrorContainer       = Color(0xFFFFDAD6),
    background             = SurfaceDarkBg,
    onBackground           = Color(0xFFE2E2E2),
    surface                = SurfaceDark,
    onSurface              = Color(0xFFE2E2E2),
    surfaceVariant         = Color(0xFF3F4945),
    onSurfaceVariant       = Color(0xFFBEC9C3),
    inverseSurface         = Color(0xFFE2E2E2),
    inverseOnSurface       = Color(0xFF1A1C1E),
    outline                = Color(0xFF8D9390),
    outlineVariant         = Color(0xFF3F4945),
    surfaceTint            = PrimaryFixedDim
)

@Composable
fun GramaVaxiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disabled for consistent Earthy Professional branding
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else      -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = GramaVaxiTypography,
        content     = content
    )
}

// Helper to convert ComposeColor to ARGB int (for window status bar)
private fun androidx.compose.ui.graphics.Color.toArgb(): Int =
    android.graphics.Color.argb(
        (alpha * 255).toInt(),
        (red   * 255).toInt(),
        (green * 255).toInt(),
        (blue  * 255).toInt()
    )

// Workaround for Color import conflict in this file
private fun Color(value: Long) = androidx.compose.ui.graphics.Color(value)
