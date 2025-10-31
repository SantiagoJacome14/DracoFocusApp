package co.edu.unab.dracofocusapp.theme


import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color


val DarkBlueBg = Color(0xFF0B0D1C)
val CardBorder = Color(0xFF2A2C4D)
val TextSecondary = Color(0xFFB0B2C3)
val TealGradientStart = Color(0xFF00FFA3)
val BlueGradientEnd = Color(0xFF00D1FF)
val LinkColor = Color(0xFF00C2FF)


val AppColorScheme = darkColorScheme(
    primary = TealGradientStart,       // Color principal
    onPrimary = Color.Black,         // Color del texto primario
    background = DarkBlueBg,         // Color de fondo general
    onBackground = Color.White,      // Color del texto sobre el fondo
    surface = DarkBlueBg,            // Color de superficies
    onSurface = Color.White,         // Color del texto sobre superficies
    surfaceVariant = CardBorder,     // Color para bordes
    outline = CardBorder             // Color para outlines
)

