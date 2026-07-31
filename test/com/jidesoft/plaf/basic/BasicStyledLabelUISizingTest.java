package com.jidesoft.plaf.basic;

import com.jidesoft.testing.FlatLafTest;
import com.jidesoft.swing.StyledLabel;
import org.junit.jupiter.api.Test;

import javax.swing.SwingUtilities;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;

import static org.junit.jupiter.api.Assertions.assertEquals;

@FlatLafTest
class BasicStyledLabelUISizingTest {
    @Test
    void measuresMultilineTextUsingTheWidestLine() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            Font font = new Font(Font.DIALOG, Font.PLAIN, 12);
            StyledLabel label = new StyledLabel("WW\nI");
            label.setFont(font);
            BasicStyledLabelUI ui = new BasicStyledLabelUI();
            FontMetrics metrics = label.getFontMetrics(font);

            Dimension size = ui.getPreferredSize(label);

            assertEquals(Math.max(metrics.stringWidth("WW"), metrics.stringWidth("I")), size.width);
            assertEquals((metrics.getHeight() + 1) * 2, size.height);
        });
    }
}
