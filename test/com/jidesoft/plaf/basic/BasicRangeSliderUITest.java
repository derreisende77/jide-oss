package com.jidesoft.plaf.basic;

import com.jidesoft.testing.FlatLafTest;
import com.jidesoft.swing.RangeSlider;
import org.junit.jupiter.api.Test;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import java.awt.ComponentOrientation;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@FlatLafTest
class BasicRangeSliderUITest {
    @Test
    void shiftArrowKeysMoveUpperValueByOne() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            RangeSlider slider = createSlider(20, 80);

            perform(slider, KeyEvent.VK_RIGHT);
            assertEquals(81, slider.getHighValue());

            perform(slider, KeyEvent.VK_LEFT);
            assertEquals(80, slider.getHighValue());
        });
    }

    @Test
    void shiftPageKeysMoveUpperValueByOneBlock() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            RangeSlider slider = createSlider(20, 80);

            perform(slider, KeyEvent.VK_PAGE_UP);
            assertEquals(90, slider.getHighValue());

            perform(slider, KeyEvent.VK_PAGE_DOWN);
            assertEquals(80, slider.getHighValue());
        });
    }

    @Test
    void shiftHomeAndEndMoveUpperValueToBounds() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            RangeSlider slider = createSlider(0, 80);

            perform(slider, KeyEvent.VK_HOME);
            assertEquals(0, slider.getHighValue());

            perform(slider, KeyEvent.VK_END);
            assertEquals(100, slider.getHighValue());
        });
    }

    @Test
    void shiftHorizontalArrowKeysFollowRightToLeftOrientation() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            RangeSlider slider = createSlider(20, 80);
            slider.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

            perform(slider, KeyEvent.VK_RIGHT);
            assertEquals(79, slider.getHighValue());

            perform(slider, KeyEvent.VK_LEFT);
            assertEquals(80, slider.getHighValue());
        });
    }

    private static RangeSlider createSlider(int lowValue, int highValue) {
        RangeSlider slider = new RangeSlider(0, 100, lowValue, highValue);
        slider.setUI(new BasicRangeSliderUI(slider));
        return slider;
    }

    private static void perform(RangeSlider slider, int keyCode) {
        KeyStroke keyStroke = KeyStroke.getKeyStroke(keyCode, InputEvent.SHIFT_DOWN_MASK);
        InputMap inputMap = SwingUtilities.getUIInputMap(slider, JComponent.WHEN_FOCUSED);
        ActionMap actionMap = SwingUtilities.getUIActionMap(slider);
        Object actionKey = inputMap.get(keyStroke);
        assertNotNull(actionKey);

        Action action = actionMap.get(actionKey);
        assertNotNull(action);
        action.actionPerformed(new ActionEvent(slider, ActionEvent.ACTION_PERFORMED, actionKey.toString()));
    }
}
