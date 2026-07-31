package com.jidesoft.swing;

import com.jidesoft.testing.FlatLafTest;
import org.junit.jupiter.api.Test;

import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionListener;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@FlatLafTest
class CheckBoxListSelectionBehaviorTest {
    @Test
    void replacingSelectionModelMovesTheInternalListener() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            CheckBoxList list = new CheckBoxList(new Object[]{"alpha", "beta"});
            CheckBoxListSelectionModel oldModel = list.getCheckBoxListSelectionModel();
            CheckBoxListSelectionModel newModel = new CheckBoxListSelectionModel(list.getModel());

            list.setCheckBoxListSelectionModel(newModel);

            assertSame(list.getModel(), newModel.getModel());
            assertFalse(hasListener(oldModel, list._handler));
            assertTrue(hasListener(newModel, list._handler));
        });
    }

    @Test
    void missingSelectedValueClearsCheckboxSelection() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            CheckBoxList list = selectedList();

            list.setCheckBoxListSelectedValue("missing", false);

            assertArrayEquals(new int[0], list.getCheckBoxListSelectedIndices());
        });
    }

    @Test
    void nullSelectedValueClearsCheckboxSelection() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            CheckBoxList list = selectedList();

            list.setCheckBoxListSelectedValue(null, false);

            assertArrayEquals(new int[0], list.getCheckBoxListSelectedIndices());
        });
    }

    private static CheckBoxList selectedList() {
        CheckBoxList list = new CheckBoxList(new Object[]{"alpha", "beta"});
        list.setCheckBoxListSelectedIndex(0);
        return list;
    }

    private static boolean hasListener(CheckBoxListSelectionModel model, ListSelectionListener listener) {
        return Arrays.asList(model.getListSelectionListeners()).contains(listener);
    }
}
