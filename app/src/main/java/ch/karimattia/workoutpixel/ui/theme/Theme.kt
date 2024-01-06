package ch.karimattia.workoutpixel.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/*
private val DarkColorPalette = darkColors(
	primary = Purple200,
	primaryVariant = Purple700,
	secondary = Purple500
)
*/

/*

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
*/
/*
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
		// colorScheme = colors,
		typography = Typography,
		// shapes = Shapes,
		content = content
	)
}*/

@Composable
fun WorkoutPixelTheme(
	content: @Composable () -> Unit
) {
	/*MaterialTheme(
		colorScheme = lightColorScheme(
			primary = Primary,
			onPrimary = Color.White,
			secondary = Secondary,
			onSecondary = Color.White,
			// Define other colors (error, background, etc.) as needed
		),
		*//*typography = Typography(
			// Define typography styles (h1, body1, etc.) as needed
		),
		shapes = Shapes(
			// Define shapes (small, medium, large) as needed
		),*//*
		content = content
	)*/
	MaterialTheme(
		colorScheme = dynamicLightColorScheme(LocalContext.current),
		content = content
	)
}


