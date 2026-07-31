package com.jidesoft.utils;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MathUtilsTest {
    @Test
    void calculatesPopulationAndSampleVariance() {
        List<Number> values = List.of(1, 2, 3, 4);

        assertEquals(1.25, MathUtils.var(values, false), 0.0);
        assertEquals(5.0 / 3.0, MathUtils.var(values, true), 1.0e-15);
    }

    @Test
    void preservesVarianceEdgeCases() {
        assertTrue(Double.isNaN(MathUtils.var(List.of(), false)));
        assertEquals(0.0, MathUtils.var(List.of(42), false), 0.0);
    }
}
