package ch.karimattia.workoutpixel.ui.theme

import android.graphics.Color.rgb
import androidx.compose.ui.graphics.Color
import ch.karimattia.workoutpixel.core.colorToInt

// Widget colors
val Green: Int = colorToInt(Color(0xffa6c36f))
val Blue: Int = colorToInt(Color(0xffc4bbaf))
val Red: Int = colorToInt(Color(0xffff8a65))
val Grey: Int = colorToInt(Color(0xffbdbdbd))

// Colors for title and navigation background
// App messages in chat
val Primary = Color(64, 61, 57)

// User messages in chat
val Secondary = Color(0xff00c853)


// Black (line)
@JvmField
val TextBlack: Int = rgb(128, 128, 128)

// Black (text)
@JvmField
val TextBlack2: Int = rgb(80, 80, 80)
val TextBlack2Color: Color = Color(TextBlack2)


// Infobox color, i.e. for infobox when there is no widget for a goal
val InfoColor: Int = rgb(227, 242, 253)

// Green button color, i.e. for button to add widget if there is no widget for a goal
val GreenTest = Color(0xFF3DDC84)

// Red button color is currently red widget color

// "White" background
val GrayBackground: Int = rgb(250, 250, 250)
val GrayBackgroundColor: Color = Color(GrayBackground)