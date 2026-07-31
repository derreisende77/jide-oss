package com.jidesoft.swing;

import com.jidesoft.testing.FlatLafTest;
import org.junit.jupiter.api.Test;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@FlatLafTest
class JideToggleButtonBehaviorTest {
    @Test
    void serializationPreservesSelectionAndActionSynchronization() throws Exception {
        SerializableAction action = new SerializableAction();
        action.putValue(Action.SELECTED_KEY, true);
        JideToggleButton button = new JideToggleButton(action);

        JideToggleButton restored = roundTrip(button);
        restored.doClick(0);

        assertFalse(restored.isSelected());
        assertFalse((Boolean) restored.getAction().getValue(Action.SELECTED_KEY));
    }

    @Test
    void traversalFocusRequestsUseSelectedEligibleGroupMember() {
        TrackingToggleButton selected = new TrackingToggleButton("Selected");
        TrackingToggleButton unselected = new TrackingToggleButton("Unselected");
        ButtonGroup group = new ButtonGroup();
        group.add(selected);
        group.add(unselected);
        selected.setSelected(true);

        unselected.requestFocus(FocusEvent.Cause.TRAVERSAL_FORWARD);

        assertTrue(selected.focusRequested);
        assertFalse(unselected.focusRequested);

        selected.reset();
        unselected.requestFocus(FocusEvent.Cause.MOUSE_EVENT);

        assertFalse(selected.focusRequested);
        assertTrue(unselected.focusRequested);
    }

    @Test
    void activationFocusInWindowUsesSelectedEligibleGroupMember() {
        TrackingToggleButton selected = new TrackingToggleButton("Selected");
        TrackingToggleButton unselected = new TrackingToggleButton("Unselected");
        ButtonGroup group = new ButtonGroup();
        group.add(selected);
        group.add(unselected);
        selected.setSelected(true);
        TrackingToggleButton.lastWindowFocusTarget = null;

        boolean accepted = unselected.requestFocusInWindow(FocusEvent.Cause.ACTIVATION);

        assertTrue(accepted);
        assertSame(selected, TrackingToggleButton.lastWindowFocusTarget);
    }

    private static JideToggleButton roundTrip(JideToggleButton button) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream output = new ObjectOutputStream(bytes)) {
            output.writeObject(button);
        }
        try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            return (JideToggleButton) input.readObject();
        }
    }

    private static final class TrackingToggleButton extends JideToggleButton {
        private static TrackingToggleButton lastWindowFocusTarget;
        private boolean focusRequested;

        private TrackingToggleButton(String text) {
            super(text);
        }

        @Override
        public boolean isDisplayable() {
            return true;
        }

        @Override
        void requestFocusUnconditionally(FocusEvent.Cause cause) {
            focusRequested = true;
        }

        @Override
        boolean requestFocusInWindowUnconditionally(FocusEvent.Cause cause) {
            lastWindowFocusTarget = this;
            return true;
        }

        private void reset() {
            focusRequested = false;
        }
    }

    private static final class SerializableAction extends AbstractAction implements Serializable {
        private static final long serialVersionUID = 1L;

        private SerializableAction() {
            super("Toggle");
        }

        @Override
        public void actionPerformed(ActionEvent event) {
        }
    }
}
