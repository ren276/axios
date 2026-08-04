package dev.sandesh.kharcha.core.designsystem.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = KharchaPrimary,
    onPrimary = KharchaOnPrimary,
    primaryContainer = KharchaPrimaryContainer,
    onPrimaryContainer = KharchaOnPrimaryContainer,
    secondary = KharchaSecondary,
    onSecondary = KharchaOnSecondary,
    secondaryContainer = KharchaSecondaryContainer,
    onSecondaryContainer = KharchaOnSecondaryContainer,
    tertiary = KharchaTertiary,
    onTertiary = KharchaOnTertiary,
    tertiaryContainer = KharchaTertiaryContainer,
    onTertiaryContainer = KharchaOnTertiaryContainer,
    error = KharchaError,
    onError = KharchaOnError,
    errorContainer = KharchaErrorContainer,
    onErrorContainer = KharchaOnErrorContainer,
    background = KharchaBackground,
    onBackground = KharchaOnBackground,
    surface = KharchaSurface,
    onSurface = KharchaOnSurface,
    surfaceVariant = KharchaSurfaceVariant,
    onSurfaceVariant = KharchaOnSurfaceVariant,
    outline = KharchaOutline,
    outlineVariant = KharchaOutlineVariant,
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF818CF8),               // Indigo-400 (lighter for dark)
    onPrimary = Color(0xFF1E1B5A),
    primaryContainer = KharchaPrimary,
    onPrimaryContainer = KharchaPrimaryContainer,
    secondary = Color(0xFF22D3EE),             // Cyan-400
    onSecondary = Color(0xFF0C4A6E),
    secondaryContainer = KharchaSecondary,
    onSecondaryContainer = KharchaSecondaryContainer,
    tertiary = Color(0xFF34D399),              // Emerald-400
    onTertiary = Color(0xFF064E3B),
    tertiaryContainer = KharchaTertiary,
    onTertiaryContainer = KharchaTertiaryContainer,
    error = Color(0xFFF87171),                 // Red-400
    onError = Color(0xFF7F1D1D),
    errorContainer = KharchaError,
    onErrorContainer = KharchaErrorContainer,
    background = KharchaBackgroundDark,
    onBackground = KharchaOnBackgroundDark,
    surface = KharchaSurfaceDark,
    onSurface = KharchaOnSurfaceDark,
    surfaceVariant = KharchaSurfaceVariantDark,
    onSurfaceVariant = KharchaOnSurfaceVariantDark,
    outline = KharchaOutlineDark,
)

/**
 * Kharcha Material 3 theme.
 * Dynamic color enabled on Android 12+ — falls back to Kharcha brand palette below.
 */
@Composable
fun KharchaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true, // Can be toggled via user setting
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = KharchaTypography,
        content = content
    )
}
