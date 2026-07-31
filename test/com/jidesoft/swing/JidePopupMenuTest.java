package com.jidesoft.swing;

import com.jidesoft.testing.FlatLafTest;
import com.jidesoft.plaf.basic.BasicJidePopupMenuUI;
import org.junit.jupiter.api.Test;

import javax.swing.JMenuItem;
import javax.swing.Popup;
import javax.swing.ToolTipManager;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@FlatLafTest
class JidePopupMenuTest {
    @Test
    void overlappingMenusRestoreSharedTooltipStateAfterLastMenuCloses() {
        ToolTipManager manager = ToolTipManager.sharedInstance();
        boolean originalState = manager.isLightWeightPopupEnabled();
        TestPopupMenu first = new TestPopupMenu();
        TestPopupMenu second = new TestPopupMenu();
        try {
            manager.setLightWeightPopupEnabled(true);

            first.fireWillBecomeVisible();
            second.fireWillBecomeVisible();
            first.fireWillBecomeInvisible();

            assertFalse(manager.isLightWeightPopupEnabled());

            second.fireWillBecomeInvisible();

            assertTrue(manager.isLightWeightPopupEnabled());
        }
        finally {
            first.fireWillBecomeInvisible();
            second.fireWillBecomeInvisible();
            manager.setLightWeightPopupEnabled(originalState);
        }
    }

    @Test
    void scrollingPopupRemovesMenuItemListenersWhenHidden() {
        JidePopupMenu menu = new FixedViewportPopupMenu();
        JMenuItem item = new JMenuItem("first");
        menu.add(item);
        menu.add(new JMenuItem("second"));
        int initialListenerCount = item.getChangeListeners().length;

        Popup firstPopup = BasicJidePopupMenuUI.addScrollPaneIfNecessary(menu, 0, 0);

        assertNotNull(firstPopup);
        assertEquals(initialListenerCount + 1, item.getChangeListeners().length);
        firstPopup.hide();
        assertEquals(initialListenerCount, item.getChangeListeners().length);

        Popup secondPopup = BasicJidePopupMenuUI.addScrollPaneIfNecessary(menu, 0, 0);

        assertNotNull(secondPopup);
        assertEquals(initialListenerCount + 1, item.getChangeListeners().length);
        secondPopup.hide();
        assertEquals(initialListenerCount, item.getChangeListeners().length);
    }

    @Test
    void visibleMenuItemCountFiresJavaBeansPropertyName() {
        JidePopupMenu menu = new JidePopupMenu();
        AtomicReference<PropertyChangeEvent> event = new AtomicReference<>();
        menu.addPropertyChangeListener(
                JidePopupMenu.PROPERTY_VISIBLE_MENU_ITEM_COUNT, event::set);

        menu.setVisibleMenuItemCount(5);

        assertNotNull(event.get());
        assertEquals(JidePopupMenu.PROPERTY_VISIBLE_MENU_ITEM_COUNT,
                event.get().getPropertyName());
        assertEquals(0, event.get().getOldValue());
        assertEquals(5, event.get().getNewValue());
    }

    private static class TestPopupMenu extends JidePopupMenu {
        void fireWillBecomeVisible() {
            super.firePopupMenuWillBecomeVisible();
        }

        void fireWillBecomeInvisible() {
            super.firePopupMenuWillBecomeInvisible();
        }
    }

    private static final class FixedViewportPopupMenu extends JidePopupMenu {
        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return new Dimension(100, 10);
        }
    }
}
