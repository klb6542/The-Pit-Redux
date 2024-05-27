package me.keegan.utils;

/*
 * Copyright (c) 2024. Created by klb.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class wordUtil {
    private static final List<String> ordinalWords = new ArrayList<>(Arrays.asList(
            "first", "second", "third", "fourth", "fifth", "sixth", "seventh", "eighth", "ninth", "tenth"
    ));

    private static final List<String> numberWords = new ArrayList<>(Arrays.asList(
            "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten"
    ));

    private static final List<String> numberMultipliers = new ArrayList<>(Arrays.asList(
            "solo", "double", "triple", "quadruple", "quintuple", "sextuple", "septuple", "octuple", "nonuple", "decuple"
    ));


    public static String integerToOrdinal(int number) {
        return (number >= 10 || number < 1) ? "zeroth" : ordinalWords.get(number - 1);
    }

    public static String integerToWord(int number) {
        return (number >= 10 || number < 1) ? "zero" : numberWords.get(number - 1);
    }

    public static String integerToMultiplier(int number) {
        return (number >= 10 || number < 1) ? "none" : numberMultipliers.get(number - 1);
    }
}
