package com.jidesoft.swing;

import com.jidesoft.testing.FlatLafTest;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.jidesoft.plaf.LookAndFeelFactory;
import org.junit.jupiter.api.Test;

import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleState;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ItemEvent;
import java.awt.image.BufferedImage;
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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@FlatLafTest
class TristateCheckBoxBehaviorTest {
    @Test
    void customIconSurvivesConstructionUiUpdatesAndMixedState() {
        SerializableIcon icon = new SerializableIcon();
        TristateCheckBox checkBox = new TristateCheckBox("Custom", icon);

        assertSame(icon, checkBox.getIcon());

        checkBox.setMixed(true);
        checkBox.setSelected(true);
        checkBox.updateUI();

        assertSame(icon, checkBox.getIcon());
    }

    @Test
    void fullClickCycleProducesBalancedItemEvents() {
        TristateCheckBox checkBox = new TristateCheckBox("Events");
        List<Integer> states = new ArrayList<>();
        checkBox.addItemListener(event -> states.add(event.getStateChange()));

        checkBox.doClick(0);
        checkBox.doClick(0);
        checkBox.doClick(0);

        assertEquals(TristateCheckBox.STATE_UNSELECTED, checkBox.getState());
        assertEquals(List.of(ItemEvent.SELECTED, ItemEvent.DESELECTED), states);
    }

    @Test
    void fullClickCycleProducesBalancedAccessibleCheckedEvents() {
        TristateCheckBox checkBox = new TristateCheckBox("Accessible");
        int[] checkedEvents = {0, 0};
        checkBox.getAccessibleContext().addPropertyChangeListener(event -> {
            if (AccessibleContext.ACCESSIBLE_STATE_PROPERTY.equals(event.getPropertyName())) {
                if (AccessibleState.CHECKED.equals(event.getNewValue())) {
                    checkedEvents[0]++;
                }
                if (AccessibleState.CHECKED.equals(event.getOldValue())) {
                    checkedEvents[1]++;
                }
            }
        });

        checkBox.doClick(0);
        checkBox.doClick(0);
        checkBox.doClick(0);

        assertEquals(1, checkedEvents[0]);
        assertEquals(1, checkedEvents[1]);
    }

    @Test
    void mixedStateParticipatesInButtonGroupSelection() {
        TristateCheckBox mixed = new TristateCheckBox("Mixed");
        TristateCheckBox selected = new TristateCheckBox("Selected");
        ButtonGroup group = new ButtonGroup();
        group.add(mixed);
        group.add(selected);
        selected.setSelected(true);

        mixed.setMixed(true);

        assertTrue(mixed.isMixed());
        assertFalse(selected.isSelected());
        assertSame(mixed.getModel(), group.getSelection());
    }

    @Test
    void invalidStateIsRejectedWithoutChangingMixedState() {
        TristateCheckBox checkBox = new TristateCheckBox("Invalid");
        checkBox.setMixed(true);

        assertThrows(IllegalArgumentException.class, () -> checkBox.setState(99));

        assertTrue(checkBox.isMixed());
    }

    @Test
    void serializationRestoresCustomIconAfterMixedState() throws Exception {
        TristateCheckBox checkBox = new TristateCheckBox("Serialized", new SerializableIcon());
        checkBox.setMixed(true);

        TristateCheckBox restored = roundTrip(checkBox);
        restored.setSelected(true);

        assertInstanceOf(SerializableIcon.class, restored.getIcon());
    }

    @Test
    void flatLafIndeterminateStateSurvivesThemeChanges() throws Exception {
        LookAndFeel previousLookAndFeel = UIManager.getLookAndFeel();
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            LookAndFeelFactory.installJideExtension();
            TristateCheckBox checkBox = new TristateCheckBox("FlatLaf", new SerializableIcon());
            checkBox.setMixed(true);

            assertEquals("indeterminate", checkBox.getClientProperty("JButton.selectedState"));
            paint(checkBox);

            UIManager.setLookAndFeel(new FlatDarkLaf());
            LookAndFeelFactory.installJideExtension();
            checkBox.updateUI();

            assertTrue(checkBox.isMixed());
            assertEquals("indeterminate", checkBox.getClientProperty("JButton.selectedState"));
            paint(checkBox);

            checkBox.setSelected(false);

            assertNull(checkBox.getClientProperty("JButton.selectedState"));
            assertInstanceOf(SerializableIcon.class, checkBox.getIcon());
        }
        finally {
            UIManager.setLookAndFeel(previousLookAndFeel);
            LookAndFeelFactory.installJideExtension();
        }
    }

    private static void paint(TristateCheckBox checkBox) {
        checkBox.setSize(160, 30);
        Graphics2D graphics = new BufferedImage(160, 30, BufferedImage.TYPE_INT_ARGB).createGraphics();
        try {
            checkBox.paint(graphics);
        }
        finally {
            graphics.dispose();
        }
    }

    private static TristateCheckBox roundTrip(TristateCheckBox checkBox)
            throws IOException, ClassNotFoundException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream output = new ObjectOutputStream(bytes)) {
            output.writeObject(checkBox);
        }
        try (ObjectInputStream input = new ObjectInputStream(
                new ByteArrayInputStream(bytes.toByteArray()))) {
            return (TristateCheckBox) input.readObject();
        }
    }

    private static final class SerializableIcon implements Icon, Serializable {
        private static final long serialVersionUID = 1L;

        @Override
        public void paintIcon(Component component, Graphics graphics, int x, int y) {
        }

        @Override
        public int getIconWidth() {
            return 12;
        }

        @Override
        public int getIconHeight() {
            return 12;
        }
    }
}
