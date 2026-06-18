package com.proyecto.puntoventa.ui.theme

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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// 1. Configuramos nuestro esquema de colores pasteles
private val EsquemaPastel = lightColorScheme(
    primary = MentaPastel,
    onPrimary = TextoOscuro, // El texto sobre los botones menta será oscuro para poder leerse
    primaryContainer = MentaPastel,
    onPrimaryContainer = TextoOscuro,
    secondary = DuraznoSuave,
    onSecondary = TextoOscuro,
    tertiary = LavandaPastel,
    background = FondoClaro,
    onBackground = TextoOscuro,
    surface = BlancoCartas,
    onSurface = TextoOscuro
)

// (Opcional) Si quieres que el modo oscuro mantenga los colores oscuros por defecto, déjalo así.
// Si no, puedes aplicar la misma paleta pastel borrando esta variable y usando EsquemaPastel abajo.
private val DarkColorScheme = darkColorScheme(
    // ... colores oscuros por defecto
)

@Composable
fun PuntoVentaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // PONER EN FALSE: Esto evita que Android 12+ sobreescriba tus colores con los del fondo de pantalla del usuario
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> EsquemaPastel // <- Aquí aplicamos nuestra paleta pastel
    }

    // Cambia el color de la barra de estado del teléfono para que combine
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}