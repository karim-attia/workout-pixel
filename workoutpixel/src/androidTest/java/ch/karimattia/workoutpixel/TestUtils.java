package ch.karimattia.workoutpixel;

import static ch.karimattia.workoutpixel.core.CommonFunctionsKt.dateBeautiful;
import static ch.karimattia.workoutpixel.core.CommonFunctionsKt.timeBeautiful;

public class TestUtils {
    public static final long now = System.currentTimeMillis();
    public static final String date = dateBeautiful(now);
    public static final String time = timeBeautiful(now);

}
