package me.keegan.utils;

public class stringUtil {
    public static Integer findKeywordIndex(String[] splitString, String keyWord) {
        for (int i = 0; i < splitString.length; i++) {
            if (!splitString[i].contains(keyWord)) { continue; }

            return i;
        }

        return -1;
    }
}
