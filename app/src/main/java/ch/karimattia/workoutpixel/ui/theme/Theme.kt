package ch.karimattia.workoutpixel.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
/*
	primary = Purple200,
	primaryVariant = Purple700,
	secondary = Purple500
*/
)

private val LightColorPalette = lightColors(
	primary = Primary,
	primaryVariant = Primary,
	secondary = Secondary,
	onPrimary = Color.White,
	onSecondary = Color.White,
	onBackground = TextBlack2Color,
	surface = Color.White,


	/* Other default colors to override
background = Color.White,
surface = Color.White,
onPrimary = Color.White,
onSecondary = Color.Black,
onBackground = Color.Black,
onSurface = Color.Black,
*/
)

@Composable
fun WorkoutPixelTheme(
	darkTheme: Boolean = isSystemInDarkTheme(),
	content: @Composable () -> Unit,
) {
	val colors = if (darkTheme) {
		DarkColorPalette
	} else {
		LightColorPalette
	}

	MaterialTheme(
		colors = colors,
		typography = Typography,
		shapes = Shapes,
		content = content
	)
}