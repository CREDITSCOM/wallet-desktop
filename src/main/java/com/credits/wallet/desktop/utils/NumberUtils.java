package com.credits.wallet.desktop.utils;

import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class NumberUtils {

    public static char getDecimalSeparator() {
        return new DecimalFormatSymbols(Locale.getDefault()).getDecimalSeparator();
    }

    public static boolean checkCorrectInputNumber(String s) {
        if (s.isEmpty()) return true;

        final var ds = getDecimalSeparator();
        final var hasMoreThenOneSeparator = s.chars().filter(ch -> ch == ds).count() > 1;
        if(hasMoreThenOneSeparator) return false;

        final var hasIncorrectSymbol =  s.matches(".*[^\\d" + ds + "].*");
        if(hasIncorrectSymbol) return false;

        return s.length() < 40;
    }
}
