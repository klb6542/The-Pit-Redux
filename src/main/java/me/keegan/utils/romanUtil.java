package me.keegan.utils;

public class romanUtil {
    public static String integerToRoman(Integer number, Boolean removeRomanOne) {
        String[] thousands = {"", "M", "MM", "MMM"};
        String[] hundreds = {"", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"};
        String[] tens = {"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"};
        String[] units = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};

        String romanNumber = thousands[number / 1000] + hundreds[(number % 1000) / 100] + tens[(number % 100) / 10] + units[number % 10];

        return (romanNumber.equals("I") && removeRomanOne) ? "" : romanNumber;
    }

    public static Integer romanToInteger(String romanNumber) {

        switch (romanNumber) {
            case "II":
                return 2;
            case "III":
                return 3;
            default:
                return 1;
        }

    }
}
