package com.jidesoft.swing;

import com.jidesoft.testing.FlatLafTest;
import org.junit.jupiter.api.Test;

import java.awt.Color;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@FlatLafTest
class JideSwingUtilitiesTest {
    @Test
    void createsTransparentBackgroundWhilePreservingColor() {
        Color background = new Color(10, 20, 30, 200);

        assertEquals(
                new Color(10, 20, 30, 0),
                JideSwingUtilities.getWindowBackground(background, false));
    }

    @Test
    void createsOpaqueBackgroundWhilePreservingColor() {
        Color background = new Color(10, 20, 30, 100);

        assertEquals(
                new Color(10, 20, 30, 255),
                JideSwingUtilities.getWindowBackground(background, true));
    }

    @Test
    void handlesDefaultWindowBackground() {
        assertEquals(
                new Color(0, 0, 0, 0),
                JideSwingUtilities.getWindowBackground(null, false));
        assertNull(JideSwingUtilities.getWindowBackground(null, true));
    }
}
