package com.jidesoft.range;

import org.junit.jupiter.api.Test;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class RangePropertyChangeTest {
    @Test
    void booleanRangeReportsBoxedOldAndNewValuesInOrder() {
        BooleanRange range = new BooleanRange(false, true);
        List<PropertyChangeEvent> events = new ArrayList<>();
        range.addPropertyChangeListener(events::add);

        range.adjust(true, false);

        assertEquals(2, events.size());
        assertPropertyChange(events.get(0), Range.PROPERTY_MIN, false, true, Boolean.class);
        assertPropertyChange(events.get(1), Range.PROPERTY_MAX, true, false, Boolean.class);
    }

    @Test
    void categoryRangeReportsStoredDoubleValues() {
        CategoryRange<String> range = new CategoryRange<>();
        List<PropertyChangeEvent> events = new ArrayList<>();
        range.addPropertyChangeListener(events::add);

        range.setMinimum(1.25);
        range.setMaximum(3.75);

        assertEquals(2, events.size());
        assertPropertyChange(events.get(0), Range.PROPERTY_MIN, null, 1.25, Double.class);
        assertPropertyChange(events.get(1), Range.PROPERTY_MAX, null, 3.75, Double.class);
    }

    private static void assertPropertyChange(PropertyChangeEvent event, String property,
                                             Object oldValue, Object newValue, Class<?> valueType) {
        assertEquals(property, event.getPropertyName());
        assertEquals(oldValue, event.getOldValue());
        assertEquals(newValue, event.getNewValue());
        if (oldValue != null) {
            assertInstanceOf(valueType, event.getOldValue());
        }
        assertInstanceOf(valueType, event.getNewValue());
    }
}
