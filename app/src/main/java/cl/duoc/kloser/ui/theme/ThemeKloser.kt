package cl.duoc.kloser.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable


private val LightColors = lightColors(
    primary = androidx.compose.ui.graphics.Color(0xFF1E88E5),
    secondary = androidx.compose.ui.graphics.Color(0xFF8E24AA)
)


private val DarkColors = darkColors(
    primary = androidx.compose.ui.graphics.Color(0xFF90CAF9)
)


@Composable
fun KloserTheme(darkTheme: Boolean = false, content: @Composable () -> Unit) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(colors = colors, content = content)
}