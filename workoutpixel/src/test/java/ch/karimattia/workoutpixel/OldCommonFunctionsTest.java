package ch.karimattia.workoutpixel;

import ch.karimattia.workoutpixel.old.OldCommonFunctions;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OldCommonFunctionsTest {

    @Test
    public void next3AmTest() {

        Assert.assertEquals(OldCommonFunctions.timeBeautiful(OldCommonFunctions.next3Am()), "03:00");
        assertEquals(OldCommonFunctions.timeBeautiful(OldCommonFunctions.today3Am()), "03:00");
        assertEquals(OldCommonFunctions.timeBeautiful(OldCommonFunctions.last3Am()), "03:00");
    }
}