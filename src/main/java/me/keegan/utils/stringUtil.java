package me.keegan.utils;

import java.util.HashMap;

public class stringUtil {
    private static final HashMap<String, String> pluralWords = new HashMap<String, String>(){{
        put("zombie", "zombies");
        put("creeper", "creepers");
        put("spider", "spiders");
        put("enderman", "endermen");
        put("skeleton", "skeletons");
    }};

    public static Integer findKeywordIndex(String[] splitString, String keyWord) {
        for (int i = 0; i < splitString.length; i++) {
            if (!splitString[i].contains(keyWord)) { continue; }

            return i;
        }

        return -1;
    }

    public static String upperCaseFirstLetter(String string) {
        return Character.toUpperCase(string.charAt(0)) + string.toLowerCase().substring(1);
    }

    public static String getPluralWord(String string) {
        return pluralWords.getOrDefault(string.toLowerCase(), string);
    }
}
