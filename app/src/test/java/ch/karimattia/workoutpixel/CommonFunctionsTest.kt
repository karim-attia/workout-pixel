package ch.karimattia.workoutpixel

import ch.karimattia.workoutpixel.core.timeBeautiful
import ch.karimattia.workoutpixel.core.next3Am
import ch.karimattia.workoutpixel.core.today3Am
import ch.karimattia.workoutpixel.core.last3Am
import org.junit.Assert
import org.junit.Test
import java.util.*

class CommonFunctionsTest {
	@Test
	fun next3AmTest() {
		val locale: Locale = Locale("ch", "de")
		Assert.assertEquals(timeBeautiful(next3Am(), locale = locale), "03:00")
		Assert.assertEquals(timeBeautiful(today3Am(), locale = locale), "03:00")
		Assert.assertEquals(timeBeautiful(last3Am(), locale = locale), "03:00")
	}
}