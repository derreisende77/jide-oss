package com.jidesoft.popup;

import com.jidesoft.testing.FlatLafTest;
import com.jidesoft.swing.ResizablePanel;
import org.junit.jupiter.api.Test;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@FlatLafTest
class JidePopupBehaviorTest {
    @Test
    void hidingPreservesConfiguredOwner() throws Exception {
        onEventDispatchThread(() -> {
            JidePopup popup = new JidePopup();
            Component owner = new JPanel();
            popup.setOwner(owner);

            popup.hidePopupImmediately();

            assertSame(owner, popup.getOwner());
            assertTrue(popup.isExcludedComponent(owner));
        });
    }

    @Test
    void popupTypeCannotChangeWhileAContainerExists() throws Exception {
        onEventDispatchThread(() -> {
            JidePopup popup = new JidePopup();
            popup.setPopupType(JidePopup.LIGHT_WEIGHT_POPUP);
            popup._panel = new ResizablePanel();

            assertThrows(IllegalStateException.class,
                    () -> popup.setPopupType(JidePopup.HEAVY_WEIGHT_POPUP));
            assertDoesNotThrow(() -> popup.setPopupType(JidePopup.LIGHT_WEIGHT_POPUP));
        });
    }

    @Test
    void reentrantHideFiresLifecycleEventsOnlyOnce() throws Exception {
        onEventDispatchThread(() -> {
            JidePopup popup = new JidePopup();
            popup.setPopupType(JidePopup.LIGHT_WEIGHT_POPUP);
            popup._panel = new ResizablePanel();
            popup._panel.add(popup);
            popup._panel.setVisible(true);
            AtomicInteger canceled = new AtomicInteger();
            AtomicInteger invisible = new AtomicInteger();
            popup.addPopupMenuListener(new PopupMenuListener() {
                @Override
                public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                }

                @Override
                public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                    invisible.incrementAndGet();
                    popup.hidePopupImmediately();
                }

                @Override
                public void popupMenuCanceled(PopupMenuEvent e) {
                    canceled.incrementAndGet();
                    popup.hidePopupImmediately(true);
                }
            });

            popup.hidePopupImmediately(true);

            assertEquals(1, canceled.get());
            assertEquals(1, invisible.get());
            assertNull(popup._panel);
        });
    }

    @Test
    void hidingStopsTimeoutTimer() throws Exception {
        onEventDispatchThread(() -> {
            JidePopup popup = new JidePopup();
            popup.setTimeout(60_000);
            Timer timer = getTimer(popup);
            assertNotNull(timer);

            popup.hidePopupImmediately();

            assertFalse(timer.isRunning());
            assertNull(getTimer(popup));
        });
    }

    @Test
    void detachedChangeUsesDocumentedPropertyName() throws Exception {
        onEventDispatchThread(() -> {
            JidePopup popup = new JidePopup();
            AtomicReference<PropertyChangeEvent> event = new AtomicReference<>();
            popup.addPropertyChangeListener(JidePopup.DETACHED_PROPERTY, event::set);

            popup.setDetached(true);

            PropertyChangeEvent detachedEvent = event.get();
            assertNotNull(detachedEvent);
            assertEquals(JidePopup.DETACHED_PROPERTY, detachedEvent.getPropertyName());
        });
    }

    @Test
    void lightweightPopupWithoutLayeredPaneIsNotCreatedOrReportedVisible() throws Exception {
        onEventDispatchThread(() -> {
            JidePopup popup = new JidePopup();
            popup.setPopupType(JidePopup.LIGHT_WEIGHT_POPUP);

            popup.createWindow(null, 0, 0);
            popup.showPopupImmediately();

            assertNull(popup._panel);
            assertFalse(popup.isPopupVisible());
        });
    }

    private static Timer getTimer(JidePopup popup) {
        try {
            Field field = JidePopup.class.getDeclaredField("_timer");
            field.setAccessible(true);
            return (Timer) field.get(popup);
        }
        catch (ReflectiveOperationException ex) {
            throw new AssertionError("Could not inspect popup timer", ex);
        }
    }

    private static void onEventDispatchThread(Runnable test) throws Exception {
        SwingUtilities.invokeAndWait(test);
    }
}
