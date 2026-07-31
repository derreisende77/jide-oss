package com.jidesoft.swing;

import com.jidesoft.testing.FlatLafTest;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@FlatLafTest
class RangeSliderBehaviorTest {
    @Test
    void setLowValuePreservesHighEndpoint() {
        RangeSlider slider = new RangeSlider(0, 100, 20, 80);
        AtomicInteger highValueEvents = new AtomicInteger();
        slider.addPropertyChangeListener(RangeSlider.PROPERTY_HIGH_VALUE,
                event -> highValueEvents.incrementAndGet());

        slider.setLowValue(41);

        assertEquals(41, slider.getLowValue());
        assertEquals(80, slider.getHighValue());
        assertEquals(0, highValueEvents.get());
    }

    @Test
    void setLowValueClampsToRangeAndHighEndpoint() {
        RangeSlider slider = new RangeSlider(0, 100, 20, 80);

        slider.setLowValue(-10);
        assertEquals(0, slider.getLowValue());
        assertEquals(80, slider.getHighValue());

        slider.setLowValue(90);
        assertEquals(80, slider.getLowValue());
        assertEquals(80, slider.getHighValue());
    }
}
