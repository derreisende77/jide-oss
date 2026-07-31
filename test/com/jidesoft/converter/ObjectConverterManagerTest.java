package com.jidesoft.converter;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class ObjectConverterManagerTest {
    @AfterEach
    void restoreDefaultConverters() {
        ObjectConverterManager.unregisterAllConverters();
        ObjectConverterManager.resetInit();
        ObjectConverterManager.initDefaultConverter();
    }

    @Test
    public void testInit() throws InterruptedException {
        assertInstanceOf(IntegerConverter.class, ObjectConverterManager.getConverter(Integer.class));
        ObjectConverterManager.unregisterAllConverters();
        ObjectConverterManager.resetInit();
        ObjectConverterManager.registerConverter(Integer.class, new DefaultObjectConverter());
        assertInstanceOf(DefaultObjectConverter.class, ObjectConverterManager.getConverter(Integer.class));
    }
}
