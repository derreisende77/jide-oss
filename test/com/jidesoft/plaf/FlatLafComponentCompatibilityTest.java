package com.jidesoft.plaf;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.jideoss.ui.FlatJideSplitButtonUI;
import com.formdev.flatlaf.jideoss.ui.FlatJideTabbedPaneUI;
import com.formdev.flatlaf.ui.FlatComboBoxUI;
import com.jidesoft.swing.JideComboBox;
import com.jidesoft.swing.JideSplitButton;
import com.jidesoft.swing.JideTabbedPane;
import com.jidesoft.testing.FlatLafTest;
import org.junit.jupiter.api.Test;

import javax.swing.JPanel;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@FlatLafTest
class FlatLafComponentCompatibilityTest {
    @Test
    void componentsUseFlatLafDelegates() {
        assertEquals("dark".equals(System.getProperty(FlatLafTest.THEME_PROPERTY, "light")),
                FlatLaf.isLafDark());
        assertInstanceOf(FlatComboBoxUI.class, new JideComboBox().getUI());
        assertInstanceOf(FlatJideSplitButtonUI.class, new JideSplitButton().getUI());
        JideTabbedPane tabbedPane = new JideTabbedPane();
        assertInstanceOf(FlatJideTabbedPaneUI.class, tabbedPane.getUI());
        assertEquals(JideTabbedPane.SHAPE_BOX, tabbedPane.getTabShape());
        assertEquals(JideTabbedPane.COLOR_THEME_DEFAULT, tabbedPane.getColorTheme());

        tabbedPane.setTabShape(JideTabbedPane.SHAPE_VSNET);
        tabbedPane.setColorTheme(JideTabbedPane.COLOR_THEME_WINXP);
        assertEquals(JideTabbedPane.SHAPE_BOX, tabbedPane.getTabShape());
        assertEquals(JideTabbedPane.COLOR_THEME_DEFAULT, tabbedPane.getColorTheme());

        tabbedPane.addTab("FlatLaf", new JPanel());
        tabbedPane.setSize(320, 200);
        tabbedPane.doLayout();
        BufferedImage image = new BufferedImage(320, 200, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        try {
            tabbedPane.paint(graphics);
        }
        finally {
            graphics.dispose();
        }
    }
}
