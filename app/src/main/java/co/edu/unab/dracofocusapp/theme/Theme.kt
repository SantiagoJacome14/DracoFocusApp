package co.edu.unab.dracofocusapp.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

val DarkBlueBg = Color(0xFF0B0D1C)
val CardBorder = Color(0xFF2A2C4D)
val TextSecondary = Color(0xFFB0B2C3)
val DracoCyan = Color(0xFF22DDF2) // Color representativo de DracoFocus
val TealGradientStart = Color(0xFF00FFA3)
val BlueGradientEnd = Color(0xFF00D1FF)
val LinkColor = Color(0xFF00C2FF)

val AppColorScheme = darkColorScheme(
    primary = DracoCyan,             // Ahora el celeste Draco es el primario oficial
    onPrimary = Color.Black,
    background = DarkBlueBg,
    onBackground = Color.White,
    surface = DarkBlueBg,
    onSurface = Color.White,
    surfaceVariant = CardBorder,
    outline = CardBorder
)
