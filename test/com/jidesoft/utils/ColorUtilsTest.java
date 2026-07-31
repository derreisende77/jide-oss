package com.jidesoft.utils;

import org.junit.jupiter.api.Test;

import java.awt.Color;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ColorUtilsTest {
    @Test
    void derivesColorsWithoutChangingLegacyResults() {
        Color color = new Color(12, 34, 56);
        float[] ratios = {0f, 0.25f, 0.5f, 0.75f, 1f, -0.25f, 1.25f};
        int[] expected = {
                0xff000000,
                0xff113354,
                0xff2366a8,
                0xff81b2e4,
                0xffffffff,
                0xffefcdac,
                0xff7d4b1a
        };

        for (int i = 0; i < ratios.length; i++) {
            assertEquals(expected[i], ColorUtils.getDerivedColor(color, ratios[i]).getRGB());
        }
    }

    @Test
    void preservesPrimaryAndGrayscaleResults() {
        assertEquals(0xff7f0000, ColorUtils.getDerivedColor(Color.RED, 0.25f).getRGB());
        assertEquals(0xff7fff7f, ColorUtils.getDerivedColor(Color.GREEN, 0.75f).getRGB());
        assertEquals(0xff0000fe, ColorUtils.getDerivedColor(Color.BLUE, 0.5f).getRGB());
        assertEquals(0xff666666, ColorUtils.getDerivedColor(Color.BLACK, 0.5f).getRGB());
        assertEquals(0xfffefefe, ColorUtils.getDerivedColor(Color.WHITE, 0.5f).getRGB());
    }

    @Test
    void ignoresAlphaAsBefore() {
        Color opaque = new Color(12, 34, 56);
        Color translucent = new Color(12, 34, 56, 78);

        assertEquals(
                ColorUtils.getDerivedColor(opaque, 0.75f),
                ColorUtils.getDerivedColor(translucent, 0.75f)
        );
    }

    @Test
    void returnsNullForNullColor() {
        assertNull(ColorUtils.getDerivedColor(null, 0.5f));
    }
}
