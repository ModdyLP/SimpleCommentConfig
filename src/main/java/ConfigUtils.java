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
}
