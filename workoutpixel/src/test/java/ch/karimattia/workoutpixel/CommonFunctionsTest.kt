package ch.karimattia.workoutpixel

import ch.karimattia.workoutpixel.core.timeBeautiful
import ch.karimattia.workoutpixel.core.next3Am
import ch.karimattia.workoutpixel.core.today3Am
import ch.karimattia.workoutpixel.core.last3Am
import org.junit.Assert
import org.junit.Test

class CommonFunctionsTest {
	@Test
	fun next3AmTest() {
		Assert.assertEquals(timeBeautiful(next3Am()), "03:00")
		Assert.assertEquals(timeBeautiful(today3Am()), "03:00")
		Assert.assertEquals(timeBeautiful(last3Am()), "03:00")
	}
}