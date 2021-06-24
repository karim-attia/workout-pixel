package com.karim.workoutpixel;

import com.karim.workoutpixel.core.CommonFunctions;

public class TestUtils {
    public static final long now = System.currentTimeMillis();
    public static final String date = CommonFunctions.dateBeautiful(now);
    public static final String time = CommonFunctions.timeBeautiful(now);

}
