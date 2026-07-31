package com.jidesoft.swing;

import com.jidesoft.testing.FlatLafTest;
import org.junit.jupiter.api.Test;

import java.awt.Cursor;
import java.awt.Dimension;
import javax.swing.SwingConstants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@FlatLafTest
class JideButtonTest {
    @Test
    void restoresCursorAfterHyperlinkContentChanges() {
        JideButton button = new JideButton("old");
        Cursor originalCursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
        button.setCursor(originalCursor);

        button.setButtonStyle(JideButton.HYPERLINK_STYLE);
        assertEquals(Cursor.HAND_CURSOR, button.getCursor().getType());

        button.setText("new");
        assertEquals(Cursor.HAND_CURSOR, button.getCursor().getType());

        button.setButtonStyle(JideButton.TOOLBAR_STYLE);

        assertSame(originalCursor, button.getCursor());
    }

    @Test
    void verticalHtmlMaximumSizeExpandsHeight() {
        JideButton button = new JideButton(
                "<html>one two three four five six seven eight nine ten</html>");
        button.setOrientation(SwingConstants.VERTICAL);

        Dimension preferredSize = button.getPreferredSize();
        Dimension maximumSize = button.getMaximumSize();

        assertEquals(preferredSize.width, maximumSize.width);
        assertTrue(maximumSize.height > preferredSize.height);
    }
}
