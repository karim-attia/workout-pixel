package com.example.workoutpixel;

import com.example.workoutpixel.Core.CommonFunctions;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CommonFunctionsTest {

    @Test
    public void next3AmTest() {

        assertEquals(CommonFunctions.timeBeautiful(CommonFunctions.next3am()), "03:00");
        assertEquals(CommonFunctions.dateBeautiful(CommonFunctions.next3am()), "03:00");
    }
}