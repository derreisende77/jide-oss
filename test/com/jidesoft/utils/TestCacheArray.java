package com.jidesoft.utils;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestCacheArray {
    private static final List<String> INITIAL_VALUES = Arrays.asList(
            "10", "20", "30", "40", "50", "50", "50", "60", "70", "80", "90", "100");

    @Test
    void mutationsMatchArrayList() {
        ArrayList<String> expected = new ArrayList<>(INITIAL_VALUES);
        CachedArrayList<String> actual = new CachedArrayList<>(INITIAL_VALUES);

        expected.remove(2);
        actual.remove(2);
        expected.remove(5);
        actual.remove(5);
        expected.remove(expected.size() - 1);
        actual.remove(actual.size() - 1);
        assertEquivalent(expected, actual);

        expected.remove("20");
        actual.remove("20");
        expected.remove("50");
        actual.remove("50");
        expected.remove("1000");
        actual.remove("1000");
        assertEquivalent(expected, actual);

        List<String> additions = Arrays.asList("25", "50", "75");
        expected.addAll(0, additions);
        actual.addAll(0, additions);
        expected.addAll(4, additions);
        actual.addAll(4, additions);
        expected.add(expected.size() - 1, "50");
        actual.add(actual.size() - 1, "50");
        assertEquivalent(expected, actual);

        expected.set(1, "50");
        actual.set(1, "50");
        expected.set(expected.size() - 2, "50");
        actual.set(actual.size() - 2, "50");
        assertEquivalent(expected, actual);
    }

    @Test
    void lazyAndEagerCachingProduceTheSameResults() {
        for (boolean lazyCaching : new boolean[]{false, true}) {
            CachedArrayList<String> actual = new CachedArrayList<>();
            actual.setLazyCaching(lazyCaching);
            ArrayList<String> expected = new ArrayList<>();

            for (String value : INITIAL_VALUES) {
                expected.add(value);
                actual.add(value);
            }
            expected.add(1, "50");
            actual.add(1, "50");
            expected.remove("70");
            actual.remove("70");

            assertEquivalent(expected, actual);
        }
    }

    private static void assertEquivalent(ArrayList<String> expected, CachedArrayList<String> actual) {
        assertEquals(expected, actual);
        for (String value : expected) {
            assertEquals(expected.indexOf(value), actual.indexOf(value));
            assertEquals(expected.lastIndexOf(value), actual.lastIndexOf(value));
        }
        assertEquals(-1, actual.indexOf("missing"));
        assertEquals(-1, actual.lastIndexOf("missing"));
    }
}
