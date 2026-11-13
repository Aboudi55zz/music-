package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val DarkColorScheme = darkColorScheme(
    primary = CandleGold,
    onPrimary = OnCandleGold,
    primaryContainer = GoldContainerDark,
    onPrimaryContainer = OnGoldContainerDark,
    secondary = TurquoiseAccent,
    onSecondary = EbonyBg,
    secondaryContainer = LapisContainerDark,
    onSecondaryContainer = OnLapisContainerDark,
    tertiary = SaffronAccent,
    onTertiary = EbonyBg,
    tertiaryContainer = TerracottaContainerDark,
    onTertiaryContainer = IvoryText,
    background = EbonyBg,
    onBackground = IvoryText,
    surface = EbonySurface,
    onSurface = IvoryText,
    surfaceVariant = EbonySurfaceVariant,
    onSurfaceVariant = IvoryMuted,
    outline = OutlineWarmDark
)

private val LightColorScheme = lightColorScheme(
    primary = BurnishedGold,
    onPrimary = OnBurnishedGold,
    primaryContainer = GoldContainerLight,
    onPrimaryContainer = OnGoldContainerLight,
    secondary = LapisTeal,
    onSecondary = OnBurnishedGold,
    secondaryContainer = LapisContainerLight,
    onSecondaryContainer = OnLapisContainerLight,
    tertiary = Terracotta,
    onTertiary = OnBurnishedGold,
    tertiaryContainer = TerracottaContainerLight,
    onTertiaryContainer = WalnutInk,
    background = ParchmentBg,
    onBackground = WalnutInk,
    surface = ParchmentSurface,
    onSurface = WalnutInk,
    surfaceVariant = ParchmentSurfaceVariant,
    onSurfaceVariant = WalnutMuted,
    outline = OutlineWarmLight
)

val MaqamatShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun MaqamatTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MaqamatTypography,
        shapes = MaqamatShapes,
        content = content
    )
}
