package net.jp.hellparadise.retake.util;

public class Utils {

    /**
     * Check if checkList contains value or not
     * @param value value to check
     * @param checkList check list
     * @return true if contains, else false
     */
    public static boolean String$isAnyEqual(String value, String... checkList) {
        for (String toCheck : checkList) {
            if (toCheck.equals(value)) return true;
        }

        return false;
    }

    /**
     * Type to field desc
     * @return desc version
     */
    public static String ASM$asObjectDesc(String... multiple) {
        StringBuilder builder = new StringBuilder();
        for (String type : multiple) {
            builder.append(ASM$asObjectDesc(type));
        }
        return builder.toString();
    }

    /**
     * Type to field desc
     * @return desc version
     */
    public static String ASM$asObjectDesc(String type) {
        return "L" + type + ";";
    }

}
