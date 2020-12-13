package se.gory_moon.chargers;

public class Utils {

    public static String clean(String in) {
        return in.replaceAll("\u00A0", " ");
    }

}