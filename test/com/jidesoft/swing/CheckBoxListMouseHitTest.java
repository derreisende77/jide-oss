package com.jidesoft.swing;

import com.jidesoft.testing.FlatLafTest;
import org.junit.jupiter.api.Test;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@FlatLafTest
class CheckBoxListMouseHitTest {
    private static final Color ICON_COLOR = new Color(197, 17, 211);
    private static final Icon TEST_ICON = new SolidIcon(13, 11, ICON_COLOR);

    @Test
    void everyPaintedIconCornerTogglesAndConsumesThePress() throws Exception {
        onEventDispatchThread(() -> {
            CheckBoxList list = createList(ComponentOrientation.LEFT_TO_RIGHT, false);
            Rectangle icon = paintedIconBounds(list, 0);
            Point[] corners = {
                    new Point(icon.x, icon.y),
                    new Point(icon.x + icon.width - 1, icon.y),
                    new Point(icon.x, icon.y + icon.height - 1),
                    new Point(icon.x + icon.width - 1, icon.y + icon.height - 1)
            };

            for (Point corner : corners) {
                list.clearCheckBoxListSelection();
                MouseEvent event = press(list, corner);

                assertTrue(list.getCheckBoxListSelectionModel().isSelectedIndex(0),
                        "painted icon corner should toggle: " + corner);
                assertTrue(event.isConsumed(), "painted icon corner should be consumed: " + corner);
            }
        });
    }

    @Test
    void pixelsImmediatelyOutsidePaintedIconDoNotToggleOrConsume() throws Exception {
        onEventDispatchThread(() -> {
            CheckBoxList list = createList(ComponentOrientation.LEFT_TO_RIGHT, false);
            Rectangle icon = paintedIconBounds(list, 0);
            int centerX = icon.x + icon.width / 2;
            int centerY = icon.y + icon.height / 2;
            Point[] outsidePoints = {
                    new Point(icon.x - 1, centerY),
                    new Point(icon.x + icon.width, centerY),
                    new Point(centerX, icon.y - 1),
                    new Point(centerX, icon.y + icon.height)
            };

            int oldHotspot = new JCheckBox().getPreferredSize().width;
            assertTrue(outsidePoints[0].x < list.getCellBounds(0, 0).x + oldHotspot,
                    "test setup must exercise a point accepted by the old fixed hotspot");

            for (Point point : outsidePoints) {
                list.clearCheckBoxListSelection();
                MouseEvent event = press(list, point);

                assertFalse(list.getCheckBoxListSelectionModel().isSelectedIndex(0),
                        "point outside painted icon should not toggle: " + point);
                assertFalse(event.isConsumed(), "point outside painted icon should not be consumed: " + point);
            }
        });
    }

    @Test
    void pointOutsideCellDoesNotToggleNearestItem() throws Exception {
        onEventDispatchThread(() -> {
            CheckBoxList list = createList(ComponentOrientation.LEFT_TO_RIGHT, false);
            Rectangle cell = list.getCellBounds(0, 0);
            Rectangle icon = paintedIconBounds(list, 0);
            Point outsideCell = new Point(
                    icon.x + icon.width / 2,
                    cell.y + cell.height + 1);

            MouseEvent event = press(list, outsideCell);

            assertFalse(list.getCheckBoxListSelectionModel().isSelectedIndex(0));
            assertFalse(event.isConsumed());
        });
    }

    @Test
    void rightToLeftIconBoundsAreUsed() throws Exception {
        onEventDispatchThread(() -> {
            CheckBoxList list = createList(ComponentOrientation.RIGHT_TO_LEFT, false);
            Rectangle cell = list.getCellBounds(0, 0);
            Rectangle icon = paintedIconBounds(list, 0);
            assertTrue(icon.x > cell.x + cell.width / 2,
                    "RTL renderer should place the checkbox on the trailing side");

            MouseEvent inside = press(list, new Point(icon.x, icon.y));
            assertTrue(list.getCheckBoxListSelectionModel().isSelectedIndex(0));
            assertTrue(inside.isConsumed());

            list.clearCheckBoxListSelection();
            MouseEvent outside = press(list,
                    new Point(icon.x - 1, icon.y + icon.height / 2));
            assertFalse(list.getCheckBoxListSelectionModel().isSelectedIndex(0));
            assertFalse(outside.isConsumed());
        });
    }

    @Test
    void clickInCheckBoxOnlyFalseStillTogglesOutsideIcon() throws Exception {
        onEventDispatchThread(() -> {
            CheckBoxList list = createList(ComponentOrientation.LEFT_TO_RIGHT, false);
            list.setClickInCheckBoxOnly(false);
            Rectangle cell = list.getCellBounds(0, 0);
            Rectangle icon = paintedIconBounds(list, 0);
            Point labelPoint = new Point(
                    cell.x + cell.width - 2,
                    cell.y + cell.height / 2);
            assertFalse(icon.contains(labelPoint));

            MouseEvent event = press(list, labelPoint);

            assertTrue(list.getCheckBoxListSelectionModel().isSelectedIndex(0));
            assertFalse(event.isConsumed());
        });
    }

    @Test
    void clickInCheckBoxOnlyFalseDoesNotToggleOutsideCell() throws Exception {
        onEventDispatchThread(() -> {
            CheckBoxList list = createList(ComponentOrientation.LEFT_TO_RIGHT, false);
            list.setClickInCheckBoxOnly(false);
            Rectangle cell = list.getCellBounds(0, 0);
            Point outsideCell = new Point(
                    cell.x + cell.width / 2,
                    cell.y + cell.height + 1);

            MouseEvent event = press(list, outsideCell);

            assertFalse(list.getCheckBoxListSelectionModel().isSelectedIndex(0));
            assertFalse(event.isConsumed());
        });
    }

    @Test
    void disabledCheckboxDoesNotToggle() throws Exception {
        onEventDispatchThread(() -> {
            CheckBoxList list = createList(ComponentOrientation.LEFT_TO_RIGHT, true);
            Rectangle icon = paintedIconBounds(list, 0);

            MouseEvent event = press(list,
                    new Point(icon.x + icon.width / 2, icon.y + icon.height / 2));

            assertFalse(list.getCheckBoxListSelectionModel().isSelectedIndex(0));
            assertTrue(event.isConsumed());
        });
    }

    private static CheckBoxList createList(ComponentOrientation orientation, boolean disableRow) {
        CheckBoxList list = disableRow
                ? new CheckBoxList(new Object[]{"row"}) {
                    @Override
                    public boolean isCheckBoxEnabled(int index) {
                        return false;
                    }
                }
                : new CheckBoxList(new Object[]{"row"});
        list._listCellRenderer = new NestedCheckBoxRenderer();
        list.applyComponentOrientation(orientation);
        list.setFixedCellHeight(44);
        list.setFixedCellWidth(220);
        list.setSize(260, 100);
        list.doLayout();
        for (MouseListener listener : list.getMouseListeners()) {
            if (listener != list._handler) {
                list.removeMouseListener(listener);
            }
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    private static Rectangle paintedIconBounds(CheckBoxList list, int index) {
        Rectangle cell = list.getCellBounds(index, index);
        Component renderer = list.getCellRenderer().getListCellRendererComponent(
                list,
                list.getModel().getElementAt(index),
                index,
                list.isSelectedIndex(index),
                false);
        renderer.setBounds(0, 0, cell.width, cell.height);
        layoutRecursively(renderer);

        BufferedImage image = new BufferedImage(cell.width, cell.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        try {
            renderer.paint(graphics);
        }
        finally {
            graphics.dispose();
        }

        int expectedRgb = ICON_COLOR.getRGB();
        int minX = image.getWidth();
        int minY = image.getHeight();
        int maxX = -1;
        int maxY = -1;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if (image.getRGB(x, y) == expectedRgb) {
                    minX = Math.min(minX, x);
                    minY = Math.min(minY, y);
                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);
                }
            }
        }
        assertTrue(maxX >= minX && maxY >= minY, "test icon was not painted");
        return new Rectangle(
                cell.x + minX,
                cell.y + minY,
                maxX - minX + 1,
                maxY - minY + 1);
    }

    private static void layoutRecursively(Component component) {
        if (component instanceof Container) {
            Container container = (Container) component;
            container.doLayout();
            for (Component child : container.getComponents()) {
                layoutRecursively(child);
            }
        }
    }

    private static MouseEvent press(CheckBoxList list, Point point) {
        MouseEvent event = new MouseEvent(
                list,
                MouseEvent.MOUSE_PRESSED,
                System.currentTimeMillis(),
                0,
                point.x,
                point.y,
                1,
                false,
                MouseEvent.BUTTON1);
        list.dispatchEvent(event);
        return event;
    }

    private static void onEventDispatchThread(Runnable test) throws Exception {
        SwingUtilities.invokeAndWait(test);
    }

    private static class NestedCheckBoxRenderer extends CheckBoxListCellRenderer {
        NestedCheckBoxRenderer() {
            _checkBox.setIcon(TEST_ICON);
            _checkBox.setSelectedIcon(TEST_ICON);
            _checkBox.setDisabledIcon(TEST_ICON);
            _checkBox.setDisabledSelectedIcon(TEST_ICON);
            _checkBox.setBorder(BorderFactory.createEmptyBorder(0, 7, 0, 9));

            remove(_checkBox);
            javax.swing.JPanel nested = new javax.swing.JPanel(new BorderLayout());
            nested.setOpaque(false);
            nested.setBorder(BorderFactory.createEmptyBorder(3, 5, 4, 6));
            nested.add(_checkBox, BorderLayout.CENTER);
            add(nested, BorderLayout.BEFORE_LINE_BEGINS);
        }
    }

    private static class SolidIcon implements Icon {
        private final int width;
        private final int height;
        private final Color color;

        SolidIcon(int width, int height, Color color) {
            this.width = width;
            this.height = height;
            this.color = color;
        }

        @Override
        public void paintIcon(Component component, Graphics graphics, int x, int y) {
            graphics.setColor(color);
            graphics.fillRect(x, y, width, height);
        }

        @Override
        public int getIconWidth() {
            return width;
        }

        @Override
        public int getIconHeight() {
            return height;
        }
    }
}
