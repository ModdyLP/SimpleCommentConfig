import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by N.Hartmann on 14.03.2018.
 * Copyright 2017
 */
public class Serilizer {
    public static Object getObjectfromLine(String value) {
        if (value.contains("\'")) {
            return value.replaceAll("\'", "");
        } else if (isNumeric(value)) {
            return Double.parseDouble(value);
        } else if (isBoolean(value)) {
            return Boolean.parseBoolean(value);
        } else {
            System.err.println("Serializer Error: "+value);
            return null;
        }
    }
    public static String convertObjecttoString(Object value, String indent) {
        if (value instanceof String) {
            return "\'"+value.toString()+"\'";
        } else if (value instanceof Double) {
            return Double.toString((Double)value);
        }else if (value instanceof Integer) {
            return Double.toString((Integer)value);
        } else if (value instanceof Boolean) {
            return Boolean.toString((Boolean)value);
        } else if (value instanceof ArrayList) {
            return convertObjecttoString(((ArrayList) value).toArray(), indent);
        } else if (value instanceof Object[]) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("\n");
            List<Object> objects = Arrays.asList((Object[]) value);
            for (int i = 0; i < objects.size(); i++) {
                if (objects.size() == i+1) {
                    stringBuilder.append(indent).append("    - ").append(convertObjecttoString(objects.get(i), ""));
                } else  {
                    stringBuilder.append(indent).append("    - ").append(convertObjecttoString(objects.get(i), "")).append("\n");
                }

            }
            return stringBuilder.toString();
        }
        else {
            return null;
        }
    }

    public static boolean isNumeric(String str) {
        try {
            double d = Double.parseDouble(str);
            return (d == d + 0);
        } catch (Exception nfe) {
            return false;
        }
    }
    public static boolean isBoolean(String str) {
        try {
            if (str.contains("true") || str.contains("false")) {
                return true;
            }
        } catch (Exception nfe) {
            return false;
        }
        return false;
    }
}
