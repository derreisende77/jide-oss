package com.jidesoft.plaf.basic;

import com.jidesoft.testing.FlatLafTest;
import com.jidesoft.swing.JideLabel;
import org.junit.jupiter.api.Test;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.SwingConstants;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@FlatLafTest
class BasicJideLabelUITest {
    @Test
    void verticalPreferredSizeKeepsInsetsOnTheirOriginalAxes() {
        JideLabel label = createLabel("MMMM");
        Insets insets = new Insets(1, 2, 7, 13);
        label.setBorder(BorderFactory.createEmptyBorder(
                insets.top, insets.left, insets.bottom, insets.right));
        Dimension horizontal = label.getPreferredSize();

        label.setOrientation(SwingConstants.VERTICAL);

        assertEquals(new Dimension(
                horizontal.height - insets.top - insets.bottom + insets.left + insets.right,
                horizontal.width - insets.left - insets.right + insets.top + insets.bottom),
                label.getPreferredSize());
    }

    @Test
    void verticalPaintingRotatesAsymmetricInsetsWithContent() {
        RecordingIcon icon = new RecordingIcon();
        JideLabel label = createLabel(icon);
        label.setBorder(BorderFactory.createEmptyBorder(1, 2, 7, 13));
        label.setSize(100, 60);
        label.setVerticalAlignment(SwingConstants.TOP);
        label.setHorizontalAlignment(SwingConstants.LEFT);
        label.setOrientation(SwingConstants.VERTICAL);

        paint(label);
        assertEquals(87, icon.deviceX);
        assertEquals(1, icon.deviceY);

        label.setClockwise(false);
        paint(label);
        assertEquals(2, icon.deviceX);
        assertEquals(53, icon.deviceY);
    }

    @Test
    void orientationChangeRevalidatesAndRepaints() {
        TrackingLabel label = new TrackingLabel();
        label.setUI(new BasicJideLabelUI());
        label.revalidates = 0;
        label.repaints = 0;

        label.setOrientation(SwingConstants.VERTICAL);

        assertEquals(1, label.revalidates);
        assertEquals(1, label.repaints);
    }

    @Test
    void rejectsUnsupportedOrientation() {
        JideLabel label = createLabel("text");

        assertThrows(IllegalArgumentException.class, () -> label.setOrientation(42));
        assertEquals(SwingConstants.HORIZONTAL, label.getOrientation());
    }

    private static JideLabel createLabel(String text) {
        JideLabel label = new JideLabel(text);
        label.setUI(new BasicJideLabelUI());
        return label;
    }

    private static JideLabel createLabel(Icon icon) {
        JideLabel label = new JideLabel(icon);
        label.setUI(new BasicJideLabelUI());
        return label;
    }

    private static void paint(JideLabel label) {
        BufferedImage image = new BufferedImage(label.getWidth(), label.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = image.getGraphics();
        try {
            label.paint(graphics);
        }
        finally {
            graphics.dispose();
        }
    }

    private static class RecordingIcon implements Icon {
        private int deviceX;
        private int deviceY;

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Point2D point = ((Graphics2D) g).getTransform().transform(
                    new Point2D.Double(x, y), null);
            deviceX = (int) Math.round(point.getX());
            deviceY = (int) Math.round(point.getY());
        }

        @Override
        public int getIconWidth() {
            return 1;
        }

        @Override
        public int getIconHeight() {
            return 1;
        }
    }

    private static class TrackingLabel extends JideLabel {
        private int revalidates;
        private int repaints;

        @Override
        public void revalidate() {
            revalidates++;
            super.revalidate();
        }

        @Override
        public void repaint() {
            repaints++;
            super.repaint();
        }
    }
}
