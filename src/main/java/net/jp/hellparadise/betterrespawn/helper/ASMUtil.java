package net.jp.hellparadise.betterrespawn.helper;

public class ASMUtil {

    public static String asObjectType(String className) {
        return "L" + className + ";";
    }

    public static String asObjectType(String... classList) {
        StringBuilder builder = new StringBuilder();
        for (String className : classList) {
            builder.append(asObjectType(className));
        }
        return builder.toString();
    }

    public static boolean isAnyStringEqual(String toCompare, String... compareValues) {
        for (String string : compareValues) {
            if (toCompare.equals(string)) return true;
        }
        return false;
    }

}
