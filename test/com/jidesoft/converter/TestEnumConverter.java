package com.jidesoft.converter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestEnumConverter {
    public enum Rank {
        DEUCE, THREE, FOUR, FIVE, SIX,
        SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, ACE
    }

    @Test
    public void testEnumConverterEnum() {
        ObjectConverter converter1 = new EnumConverter("Rank", Rank.values(), new String[]{
                Rank.DEUCE.toString(),
                Rank.THREE.toString(),
                Rank.FOUR.toString(),
                Rank.FIVE.toString(),
                Rank.SIX.toString(),
                Rank.SEVEN.toString(),
                Rank.EIGHT.toString(),
                Rank.NINE.toString(),
                Rank.TEN.toString(),
                Rank.JACK.toString(),
                Rank.QUEEN.toString(),
                Rank.KING.toString(),
                Rank.ACE.toString(),
        });

        ObjectConverter converter2 = new EnumConverter("Rank", Rank.values(), EnumConverter.toStrings(Rank.values()));

        assertAll(
                () -> assertEquals(Rank.DEUCE.toString(), converter1.toString(Rank.DEUCE, null)),
                () -> assertEquals(Rank.DEUCE.toString(), converter2.toString(Rank.DEUCE, null))
        );
    }

    @Test
    public void createsConverterFromEnumClass() {
        EnumConverter converter = new EnumConverter(Rank.class);

        assertAll(
                () -> assertEquals("Rank", converter.getName()),
                () -> assertEquals(Rank.class, converter.getType()),
                () -> assertArrayEquals(Rank.values(), converter.getObjects()),
                () -> assertArrayEquals(EnumConverter.toStrings(Rank.values()), converter.getStrings()),
                () -> assertEquals(Rank.DEUCE, converter.fromString("DEUCE", null)),
                () -> assertEquals("DEUCE", converter.toString(Rank.DEUCE, null))
        );
    }
}
