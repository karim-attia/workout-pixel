package com.example.workoutpixel;

import com.example.workoutpixel.Core.CommonFunctions;

public class TestUtils {
    public static long now = System.currentTimeMillis();
    public static String date = CommonFunctions.dateBeautiful(now);
    public static String time = CommonFunctions.timeBeautiful(now);

}
