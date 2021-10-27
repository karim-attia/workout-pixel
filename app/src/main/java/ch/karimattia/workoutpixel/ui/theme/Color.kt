package ch.karimattia.workoutpixel.ui.theme

import android.graphics.Color.rgb
import androidx.compose.ui.graphics.Color
import ch.karimattia.workoutpixel.core.colorToInt

val Purple200 = Color(0xFFBB86FC)
val Purple500 = Color(0xFF6200EE)
val Purple700 = Color(0xFF3700B3)
val InfoColor: Int = rgb(227, 242, 253)

val Primary = Color(0xff0d47a1)
val PrimaryLight = Color(0xff1e88e5)
val Secondary = Color(0xff00c853)

val PrimaryInt = colorToInt(Primary)
val SecondaryInt = colorToInt(Secondary)

/*@JvmField
val Green: Int = rgb(56, 142, 60)

@JvmField
val Blue: Int = rgb(25, 118, 210)

@JvmField
val Red: Int = rgb(211, 47, 47)*/

val Green: Int = SecondaryInt
val Blue: Int = colorToInt(PrimaryLight)
val Red: Int = colorToInt(Color(0xffffa726   ))
val Grey: Int = colorToInt(Color(0xffbdbdbd))

@JvmField
val Purple: Int = rgb(123, 31, 162)

@JvmField
val TextBlack: Int = rgb(128, 128, 128)

@JvmField
val TextBlack2: Int = rgb(80, 80, 80)
val TextBlack2Color: Color = Color(TextBlack2)

val GreenTest = Color(0xFF3DDC84)

val Green1 = Color(0xff7affb4)
val Green2 = Color(0xff3ddc84)
val Green3 = Color(0xff00a956)


/*
val GreenLight = Color(0xff5472d3)
val GreenDark = Color(0xff002171)
*/


val GrayBackground: Int = rgb(250, 250, 250)
val GrayBackgroundColor: Color = Color(GrayBackground)
