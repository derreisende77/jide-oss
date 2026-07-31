package com.jidesoft.swing;

import com.jidesoft.testing.FlatLafTest;
import org.junit.jupiter.api.Test;

import javax.accessibility.AccessibleState;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultButtonModel;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@FlatLafTest
class JideToggleSplitButtonBehaviorTest {
    @Test
    void itemEventsReportButtonSelectionState() {
        JideToggleSplitButton button = new JideToggleSplitButton("Toggle");
        List<Integer> states = new ArrayList<>();
        button.addItemListener(event -> states.add(event.getStateChange()));

        button.setButtonSelected(true);
        button.setButtonSelected(false);

        assertEquals(List.of(ItemEvent.SELECTED, ItemEvent.DESELECTED), states);
    }

    @Test
    void accessibleCheckedStateTracksButtonSelection() {
        JideToggleSplitButton button = new JideToggleSplitButton("Toggle");

        button.setButtonSelected(true);
        assertTrue(button.getAccessibleContext().getAccessibleStateSet().contains(AccessibleState.CHECKED));

        button.setButtonSelected(false);
        assertFalse(button.getAccessibleContext().getAccessibleStateSet().contains(AccessibleState.CHECKED));
    }

    @Test
    void actionSelectionHandlesInitialFalseAndRemovedValues() {
        SerializableAction action = new SerializableAction();
        action.putValue(Action.SELECTED_KEY, false);
        JideToggleSplitButton button = new JideToggleSplitButton("Toggle", true);

        button.setAction(action);
        assertFalse(button.isButtonSelected());

        action.putValue(Action.SELECTED_KEY, true);
        assertTrue(button.isButtonSelected());

        action.putValue(Action.SELECTED_KEY, null);
        assertFalse(button.isButtonSelected());
    }

    @Test
    void actionSelectionSupportsCustomSplitButtonModels() {
        SerializableAction action = new SerializableAction();
        action.putValue(Action.SELECTED_KEY, false);
        JideToggleSplitButton button = new JideToggleSplitButton("Toggle");
        CustomSplitButtonModel model = new CustomSplitButtonModel();
        button.setModel(model);
        button.setAction(action);

        action.putValue(Action.SELECTED_KEY, true);

        assertTrue(model.isButtonSelected());
    }

    @Test
    void serializationPreservesActionBackedSelectionSynchronization() throws Exception {
        SerializableAction action = new SerializableAction();
        action.putValue(Action.SELECTED_KEY, true);
        JideToggleSplitButton button = new JideToggleSplitButton(action);

        JideToggleSplitButton restored = roundTrip(button);
        restored.setButtonSelected(false);

        assertFalse(restored.isButtonSelected());
        assertFalse((Boolean) restored.getAction().getValue(Action.SELECTED_KEY));
    }

    @Test
    void splitButtonGroupReportsAndClearsSelection() {
        JideToggleSplitButton selected = new JideToggleSplitButton("Selected", true);
        JideToggleSplitButton other = new JideToggleSplitButton("Other");
        SplitButtonGroup group = new SplitButtonGroup();
        group.add(selected);
        group.add(other);

        assertSame(selected.getModel(), group.getSelection());

        group.clearSelection();

        assertNull(group.getSelection());
        assertFalse(selected.isButtonSelected());
    }

    @Test
    void unselectedActionClearsSplitButtonGroupSelection() {
        SerializableAction action = new SerializableAction();
        action.putValue(Action.SELECTED_KEY, true);
        JideToggleSplitButton button = new JideToggleSplitButton(action);
        SplitButtonGroup group = new SplitButtonGroup();
        group.add(button);

        action.putValue(Action.SELECTED_KEY, false);

        assertFalse(button.isButtonSelected());
        assertNull(group.getSelection());
    }

    private static JideToggleSplitButton roundTrip(JideToggleSplitButton button)
            throws IOException, ClassNotFoundException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream output = new ObjectOutputStream(bytes)) {
            output.writeObject(button);
        }
        try (ObjectInputStream input = new ObjectInputStream(
                new ByteArrayInputStream(bytes.toByteArray()))) {
            return (JideToggleSplitButton) input.readObject();
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

    private static final class CustomSplitButtonModel extends DefaultButtonModel implements SplitButtonModel {
        private boolean buttonSelected;
        private boolean buttonEnabled = true;
        private boolean buttonRollover;

        @Override
        public void setButtonSelected(boolean selected) {
            buttonSelected = selected;
        }

        @Override
        public boolean isButtonSelected() {
            return buttonSelected;
        }

        @Override
        public void setButtonEnabled(boolean enabled) {
            buttonEnabled = enabled;
        }

        @Override
        public boolean isButtonEnabled() {
            return buttonEnabled;
        }

        @Override
        public void setButtonRollover(boolean rollover) {
            buttonRollover = rollover;
        }

        @Override
        public boolean isButtonRollover() {
            return buttonRollover;
        }
    }
}
