/*
 * @(#)TestFontUtils.java 9/9/2009
 *
 * Copyright 2002 - 2009 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.utils;

import com.jidesoft.swing.FontUtils;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public class TestFontUtils {
    @Test
    public void testAddFont() {
        Font font = UIManager.getFont("Label.font");
        Font first = FontUtils.getCachedDerivedFont(font, Font.BOLD, 500);
        assertSame(first, FontUtils.getCachedDerivedFont(font, Font.BOLD, 500));

        int initialSize = FontUtils.getDerivedFontCacheSize();
        for (int i = 0; i < 100; i++) {
            FontUtils.getCachedDerivedFont(font, Font.BOLD, 1000 + i);
        }
        assertEquals(initialSize + 100, FontUtils.getDerivedFontCacheSize());
    }
}
