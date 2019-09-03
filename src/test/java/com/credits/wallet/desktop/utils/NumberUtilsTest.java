package com.credits.wallet.desktop.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;

import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.stream.Stream;

import static com.credits.wallet.desktop.utils.NumberUtils.checkCorrectInputNumber;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.of;


public class NumberUtilsTest {

    private static Stream<Arguments> getParams() {
        final var ds = Character.toString(new DecimalFormatSymbols(Locale.getDefault()).getDecimalSeparator());
        return Stream.of(
                of("", true),
                of("1", true),
                of(ds + "1", true),
                of(ds + ds + "1", false),
                of("1" + ds + "0", true),
                of("1" + ds + "0" + ds, false));
    }

    @Test
    void correctNumTestAllLocales() {
        for (Locale locale : Locale.getAvailableLocales()) {
            final var params = getParams();

            params.forEach(args -> {
                final var input = (String) args.get()[0];
                final var expected = (boolean) args.get()[1];

                assertEquals(expected,
                             checkCorrectInputNumber(input),
                             "input: " + input + " expected: " + expected + " using locale: " + locale.toLanguageTag());
            });
        }
    }
}
