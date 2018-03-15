/**
 * Created by N.Hartmann on 14.03.2018.
 * Copyright 2017
 */
public class ConfigUtils {
    public static void appendNewLine(StringBuilder builder) {
        if (builder.length() > 3 && !builder.substring(builder.length() -2, builder.length()).contains("\n\n")) {
            builder.append("\n");
        }
    }
    public static String createFiller(int filler) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i <= filler; i++) {
            builder.append(" ");
        }
        return builder.toString();
    }
    public static String createLine(int filler) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i <= filler; i++) {
            builder.append("_");
        }
        return builder.toString();
    }
}
