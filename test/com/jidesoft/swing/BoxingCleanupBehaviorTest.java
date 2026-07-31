package com.jidesoft.swing;

import com.jidesoft.testing.FlatLafTest;
import org.junit.jupiter.api.Test;

import javax.swing.SwingUtilities;
import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@FlatLafTest
class BoxingCleanupBehaviorTest {
    @Test
    void selectedObjectsSelectEveryMatchingModelIndex() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            CheckBoxList list = new CheckBoxList(new Object[]{"alpha", "beta", "alpha", "gamma"});

            list.setSelectedObjects(new Object[]{"alpha", "gamma"});

            assertArrayEquals(new int[]{0, 2, 3}, list.getCheckBoxListSelectedIndices());
            assertArrayEquals(new Object[]{"alpha", "alpha", "gamma"}, list.getCheckBoxListSelectedValues());
        });
    }

    @Test
    void calculatorKeepsArithmeticAndOperatorEventBehavior() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            Calculator calculator = new Calculator();
            AtomicReference<PropertyChangeEvent> operatorEvent = new AtomicReference<>();
            calculator.addPropertyChangeListener(Calculator.PROPERTY_OPERATOR, operatorEvent::set);

            calculator.setOperator(Calculator.OPERATOR_MULTIPLY);

            assertEquals(Calculator.OPERATOR_NONE, operatorEvent.get().getOldValue());
            assertEquals(Calculator.OPERATOR_MULTIPLY, operatorEvent.get().getNewValue());
            assertInstanceOf(Integer.class, operatorEvent.get().getOldValue());
            assertInstanceOf(Integer.class, operatorEvent.get().getNewValue());

            calculator.clear();
            calculator.input('1');
            calculator.input('0');
            calculator.input('*');
            calculator.input('2');
            calculator.input('4');
            calculator.input('=');

            assertEquals(240.0, calculator.getResult(), 0.0);
        });
    }

    @Test
    void shadowSizeEventRetainsIntegerValues() {
        ShadowFactory factory = new ShadowFactory(4, 0.5f, Color.BLACK);
        AtomicReference<PropertyChangeEvent> sizeEvent = new AtomicReference<>();
        factory.addPropertyChangeListener(event -> {
            if (ShadowFactory.SIZE_CHANGED_PROPERTY.equals(event.getPropertyName())) {
                sizeEvent.set(event);
            }
        });

        factory.setSize(7);

        assertEquals(7, factory.getSize());
        assertEquals(4, sizeEvent.get().getOldValue());
        assertEquals(7, sizeEvent.get().getNewValue());
        assertInstanceOf(Integer.class, sizeEvent.get().getOldValue());
        assertInstanceOf(Integer.class, sizeEvent.get().getNewValue());
    }

    @Test
    void styledLabelGlobalNumericConfigurationIsPreserved() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            StyledLabel label = StyledLabelBuilder.createStyledLabel(
                    "content@rows:3:2:5,width:240");

            assertEquals("content", label.getText());
            assertEquals(3, label.getRows());
            assertEquals(2, label.getMinRows());
            assertEquals(5, label.getMaxRows());
            assertEquals(240, label.getPreferredWidth());
        });
    }
}
