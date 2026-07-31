package com.jidesoft.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestCachedArrayList {
    CachedArrayList<String> cachedList;
    ActiveCachedArrayList<String> activeCachedList;
    ArrayList<String> list;
    public static final int SIZE = 1000;

    @BeforeEach
    void setUp() {
        list = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            list.add(0, String.valueOf(i));
        }

        cachedList = new CachedArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            cachedList.add(0, String.valueOf(i));
        }

        activeCachedList = new ActiveCachedArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            activeCachedList.add(0, String.valueOf(i));
        }
    }

    @Test
    public void testIndexOf() {
        assertEquals(list.indexOf(list.get(SIZE - 1)), cachedList.indexOf(cachedList.get(SIZE - 1)));
        assertEquals(list.indexOf(list.get(1)), cachedList.indexOf(cachedList.get(1)));
        cachedList.invalidateCache();
    }

    @Test
    public void cachedIndexesMatchArrayList() {
        for (int i = 0; i < SIZE; i++) {
            assertEquals(list.indexOf(list.get(i)), cachedList.indexOf(cachedList.get(i)));
            assertEquals(list.indexOf(list.get(i)), activeCachedList.indexOf(activeCachedList.get(i)));
        }
        cachedList.invalidateCache();
    }

    @Test
    public void testAddRemove() {
        CachedArrayList<String> list = new CachedArrayList<>();

        list.setLazyCaching(false);
        list.add("1");
        list.add("2");
        list.add("3");
        assertEquals(3, list.size());
        list.add("3");
        assertEquals(4, list.size());
        assertEquals(2, list.indexOf("3"));

        list.add(1, "3");
        assertEquals(5, list.size());
        assertEquals(1, list.indexOf("3"));

        list.clear();
        assertEquals(0, list.size());

        list.setLazyCaching(true);
        list.add("1");
        list.add("2");
        list.add("3");
        assertEquals(3, list.size());
        list.add("3");
        assertEquals(4, list.size());
        assertEquals(2, list.indexOf("3"));

        list.add(1, "3");
        assertEquals(5, list.size());
        assertEquals(1, list.indexOf("3"));

        list.clear();
        assertEquals(0, list.size());
    }

    @Test
    void cacheAllRetainsTheFirstIndexForDuplicateValues() {
        CachedArrayList<String> arrayList = new CachedArrayList<>();
        arrayList.add("first");
        arrayList.add("duplicate");
        arrayList.add("duplicate");
        arrayList.cacheAll();

        CachedVector<String> vector = new CachedVector<>();
        vector.add("first");
        vector.add("duplicate");
        vector.add("duplicate");
        vector.cacheAll();

        assertEquals(1, arrayList.indexOf("duplicate"));
        assertEquals(1, vector.indexOf("duplicate"));
    }
}
