package se.gory_moon.chargers;

import java.text.NumberFormat;

public class Utils {

    public static String clean(String in) {
        return in.replaceAll("\u00A0", " ");
    }

    public static String formatAndClean(int in)
    {
        return clean(NumberFormat.getInstance().format(in));
    }
}