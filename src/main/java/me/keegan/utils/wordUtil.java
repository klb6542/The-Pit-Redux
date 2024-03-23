package me.keegan.utils;

/*
 * Copyright (c) 2024. Created by klb.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class wordUtil {
    private static final List<String> words = new ArrayList<>(Arrays.asList(
            "first", "second", "third", "fourth", "fifth", "sixth", "seventh", "eighth", "ninth", "tenth"
    ));

    public static String integerToWord(int number) {
        return (number >= 10 || number < 1) ? "zero" : words.get(number - 1);
    }
}
