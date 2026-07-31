package com.jidesoft.swing;

import com.jidesoft.testing.FlatLafTest;
import org.junit.jupiter.api.Test;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import javax.swing.DefaultButtonModel;
import javax.swing.LookAndFeel;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPopupMenuUI;
import javax.swing.plaf.metal.MetalLookAndFeel;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@FlatLafTest
class JideSplitButtonTest {
    @Test
    void doClickPerformsActionAndReleasesModel() {
        JideSplitButton splitButton = new JideSplitButton();
        boolean[] performed = {false};
        splitButton.addActionListener(event -> performed[0] = true);

        splitButton.doClick();

        assertTrue(performed[0]);
        assertFalse(splitButton.getModel().isArmed());
        assertFalse(splitButton.getModel().isPressed());
    }

    @Test
    void supportsCustomSplitButtonModel() {
        JideSplitButton splitButton = new JideSplitButton();
        CustomSplitButtonModel model = new CustomSplitButtonModel();
        splitButton.setModel(model);

        splitButton.setButtonSelected(true);
        splitButton.setButtonEnabled(false);

        assertTrue(splitButton.isButtonSelected());
        assertFalse(splitButton.isButtonEnabled());

        splitButton.setSize(100, 30);
        Graphics2D graphics = new BufferedImage(100, 30, BufferedImage.TYPE_INT_ARGB).createGraphics();
        try {
            splitButton.paint(graphics);
        }
        finally {
            graphics.dispose();
        }
    }

    @Test
    void updateUIUpdatesExistingPopupMenu() throws Exception {
        LookAndFeel previousLookAndFeel = UIManager.getLookAndFeel();
        try {
            UIManager.setLookAndFeel(new MetalLookAndFeel());
            JideSplitButton splitButton = new JideSplitButton();
            splitButton.getPopupMenu();

            UIManager.setLookAndFeel(new PopupMenuLookAndFeel());
            splitButton.updateUI();

            assertInstanceOf(TrackingPopupMenuUI.class, splitButton.getPopupMenu().getUI());
        }
        finally {
            UIManager.setLookAndFeel(previousLookAndFeel);
        }
    }

    private static final class PopupMenuLookAndFeel extends MetalLookAndFeel {
        @Override
        public UIDefaults getDefaults() {
            UIDefaults defaults = super.getDefaults();
            defaults.put("PopupMenuUI", TrackingPopupMenuUI.class.getName());
            return defaults;
        }
    }

    public static final class TrackingPopupMenuUI extends BasicPopupMenuUI {
        public static ComponentUI createUI(JComponent component) {
            return new TrackingPopupMenuUI();
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
