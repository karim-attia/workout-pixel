package ch.karimattia.workoutpixel;

import ch.karimattia.workoutpixel.core.CommonFunctions;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CommonFunctionsTest {

    @Test
    public void next3AmTest() {

        Assert.assertEquals(CommonFunctions.timeBeautiful(CommonFunctions.next3Am()), "03:00");
        assertEquals(CommonFunctions.timeBeautiful(CommonFunctions.today3Am()), "03:00");
        assertEquals(CommonFunctions.timeBeautiful(CommonFunctions.last3Am()), "03:00");
    }
}