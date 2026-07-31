package com.jidesoft.swing;

import com.jidesoft.testing.FlatLafTest;
import org.junit.jupiter.api.Test;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@FlatLafTest
class JideTabbedPaneBehaviorTest {
    @Test
    void moveSelectedTabPreservesTabProperties() {
        JideTabbedPane pane = new JideTabbedPane();
        JPanel selectedComponent = new JPanel();
        Icon icon = new ImageIcon(new BufferedImage(4, 4, BufferedImage.TYPE_INT_ARGB));
        Icon disabledIcon = new ImageIcon(new BufferedImage(5, 5, BufferedImage.TYPE_INT_ARGB));
        JLabel tabComponent = new JLabel("Custom");
        pane.addTab("Selected", icon, selectedComponent, "Tooltip");
        pane.addTab("Middle", new JPanel());
        pane.addTab("Target", new JPanel());
        pane.setDisabledIconAt(0, disabledIcon);
        pane.setEnabledAt(0, false);
        pane.setMnemonicAt(0, 'S');
        pane.setDisplayedMnemonicIndexAt(0, 0);
        pane.setForegroundAt(0, Color.BLUE);
        pane.setBackgroundAt(0, Color.ORANGE);
        pane.setTabComponentAt(0, tabComponent);
        pane.setTabClosableAt(0, false);
        pane.setSelectedIndex(0);

        pane.moveSelectedTabTo(2);

        assertEquals(2, pane.indexOfComponent(selectedComponent));
        assertEquals(2, pane.getSelectedIndex());
        assertEquals("Selected", pane.getTitleAt(2));
        assertSame(icon, pane.getIconAt(2));
        assertSame(disabledIcon, pane.getDisabledIconAt(2));
        assertEquals("Tooltip", pane.getToolTipTextAt(2));
        assertFalse(pane.isEnabledAt(2));
        assertEquals('S', pane.getMnemonicAt(2));
        assertEquals(0, pane.getDisplayedMnemonicIndexAt(2));
        assertEquals(Color.BLUE, pane.getForegroundAt(2));
        assertEquals(Color.ORANGE, pane.getBackgroundAt(2));
        assertSame(tabComponent, pane.getTabComponentAt(2));
        assertFalse(pane.isTabClosableAt(2));
    }

    @Test
    void adjacentMovePreservesPropertiesOfTheShiftedTab() {
        JideTabbedPane pane = new JideTabbedPane();
        JPanel selectedComponent = new JPanel();
        JPanel shiftedComponent = new JPanel();
        JLabel tabComponent = new JLabel("Shifted");
        pane.addTab("Selected", selectedComponent);
        pane.addTab("Shifted", shiftedComponent);
        pane.setEnabledAt(1, false);
        pane.setBackgroundAt(1, Color.MAGENTA);
        pane.setTabComponentAt(1, tabComponent);
        pane.setTabClosableAt(1, false);
        pane.setSelectedIndex(0);

        pane.moveSelectedTabTo(1);

        assertEquals(1, pane.indexOfComponent(selectedComponent));
        assertEquals(0, pane.indexOfComponent(shiftedComponent));
        assertFalse(pane.isEnabledAt(0));
        assertEquals(Color.MAGENTA, pane.getBackgroundAt(0));
        assertSame(tabComponent, pane.getTabComponentAt(0));
        assertFalse(pane.isTabClosableAt(0));
    }

    @Test
    void moveSelectedTabKeepsInheritedColorsThemeResponsive() {
        JideTabbedPane pane = new JideTabbedPane();
        JPanel selectedComponent = new JPanel();
        pane.setForeground(Color.BLUE);
        pane.setBackground(Color.WHITE);
        pane.addTab("Selected", selectedComponent);
        pane.addTab("Middle", new JPanel());
        pane.addTab("Target", new JPanel());
        pane.setSelectedIndex(0);

        pane.moveSelectedTabTo(2);
        pane.setForeground(Color.YELLOW);
        pane.setBackground(Color.BLACK);

        assertEquals(Color.YELLOW, pane.getForegroundAt(2));
        assertEquals(Color.BLACK, pane.getBackgroundAt(2));
    }

    @Test
    void setComponentAtTransfersStateAndFocusTrackingWithoutRetainingOldComponent() {
        TestTabbedPane pane = new TestTabbedPane();
        JPanel oldComponent = new JPanel();
        JPanel replacement = new JPanel();
        pane.addTab("Page", oldComponent);
        pane.setTabClosableAt(0, false);

        pane.setComponentAt(0, replacement);
        pane.addTab("Reused old component", oldComponent);

        assertFalse(pane.isTabClosableAt(0));
        assertTrue(pane.isTabClosableAt(1));
        assertTrue(pane.hasFocusTracker(replacement));
        assertTrue(pane.hasFocusTracker(oldComponent));
    }

    @Test
    void invalidMoveDoesNotChangeAutomaticFocusHandling() {
        JideTabbedPane pane = new JideTabbedPane();
        pane.addTab("First", new JPanel());
        pane.addTab("Second", new JPanel());
        pane.setSelectedIndex(0);

        assertThrows(IndexOutOfBoundsException.class, () -> pane.moveSelectedTabTo(10));

        assertTrue(pane.isAutoRequestFocus());
        assertEquals(0, pane.getSelectedIndex());
    }

    private static final class TestTabbedPane extends JideTabbedPane {
        private boolean hasFocusTracker(Component component) {
            return getPageLastFocusTrackers().containsKey(component);
        }
    }
}
