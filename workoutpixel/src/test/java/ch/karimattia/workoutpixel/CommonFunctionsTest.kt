package ch.karimattia.workoutpixel;

import static org.junit.Assert.assertEquals;
import static ch.karimattia.workoutpixel.core.CommonFunctionsKt.last3Am;
import static ch.karimattia.workoutpixel.core.CommonFunctionsKt.next3Am;
import static ch.karimattia.workoutpixel.core.CommonFunctionsKt.timeBeautiful;
import static ch.karimattia.workoutpixel.core.CommonFunctionsKt.today3Am;

import org.junit.Assert;
import org.junit.Test;

public class OldCommonFunctionsTest {

    @Test
    public void next3AmTest() {

        Assert.assertEquals(timeBeautiful(next3Am()), "03:00");
        assertEquals(timeBeautiful(today3Am()), "03:00");
        assertEquals(timeBeautiful(last3Am()), "03:00");
    }
}