package fr.catcore.shadoknumbers;

import java.util.ArrayList;
import java.util.List;

public class ShadokNumbers {

    public static Object[] shadokify(Object[] args) {
        List<Object> newArgs = new ArrayList<>();

        for (Object arg : args) {
            if (arg instanceof Number) {
                newArgs.add(parseNumber((Number) arg));
            } else {
                newArgs.add(arg);
            }
        }

        return newArgs.toArray(new Object[0]);
    }

    public static String parseNumber(Number number) {
        String shadok = "";

        if (number instanceof Integer) {
            shadok = Integer.toString((Integer) number, 4);
        } else if (number instanceof Long) {
            shadok = Long.toString((Long) number, 4);
        } else if (number instanceof Short) {
            shadok = Integer.toString((Short) number, 4);
        } else if (number instanceof Float) {
            shadok = Integer.toString(Integer.parseInt(Float.toString((Float)number).replace(".", "")) , 4);
        } else if (number instanceof Double) {
            shadok = Integer.toString(Integer.parseInt(Double.toString((Double)number).replace(".", "")) , 4);
        }

        shadok = shadok
                .replace("0", "GA")
                .replace("1", "BU")
                .replace("2", "ZO")
                .replace("3", "MEU");

        return shadok;
    }
}
