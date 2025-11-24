package com.alra.sof.chickin.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = MoonlightYellow, // 🐔 Moonlit chicken yellow
    onPrimary = NightCoop,
    primaryContainer = CoopShadow,
    onPrimaryContainer = StarGlow,
    secondary = ChickenOrangeLight,
    onSecondary = NightCoop,
    secondaryContainer = DarkStraw,
    onSecondaryContainer = HayGold,
    tertiary = CombRed,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF3E2723),
    onTertiaryContainer = WattleRed,
    background = DarkNest,
    onBackground = StarGlow,
    surface = DarkStraw,
    onSurface = StarGlow,
    surfaceVariant = CoopShadow,
    onSurfaceVariant = Color(0xFFD4C4B0),
    surfaceTint = MoonlightYellow,
    inverseSurface = CreamyEgg,
    inverseOnSurface = DarkNest,
    error = FoxDanger,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = ChickenYellow, // 🐔 Bright chicken yellow
    onPrimary = FeatherBrown,
    primaryContainer = ChickenYellowLight,
    onPrimaryContainer = BarnWood,
    secondary = ChickenOrange, // 🧡 Beak orange
    onSecondary = Color.White,
    secondaryContainer = StrawYellow,
    onSecondaryContainer = BarnWood,
    tertiary = CombRed, // ❤️ Comb red
    onTertiary = Color.White,
    tertiaryContainer = DawnPink,
    onTertiaryContainer = BarnRed,
    background = LightNest, // 🥚 Nest background
    onBackground = FeatherBrown,
    surface = EggShell,
    onSurface = FeatherBrown,
    surfaceVariant = EggWhite,
    onSurfaceVariant = BarnWood,
    surfaceTint = ChickenYellow,
    inverseSurface = BarnWood,
    inverseOnSurface = EggWhite,
    outline = FeatherLight,
    outlineVariant = WheatGold,
    scrim = Color(0x80000000),
    error = FoxDanger,
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)
)

@Composable
fun ChickAlarmTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color disabled to use custom theme
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}