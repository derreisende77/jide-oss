/*
 * @(#)TristateCheckBoxEx.java 5/20/2011
 *
 * Copyright 2002 - 2011 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.swing;

import com.jidesoft.plaf.UIDefaultsLookup;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * TristateCheckBox is a check box with three states - selected, unselected and mixed (a.k.a partial selected state).
 * Internally it uses a new class called {@link TristateButtonModel} to store the 3rd mixed state information.
 * <p/>
 * The mixed state uses a different check icon. Instead of a checked sign in the selected state as in a regular check
 * box, we use a square sign to indicate the mixed state. The {@code TristateCheckBox.icon} UI default can provide an
 * icon that paints all three states. FlatLaf's built-in indeterminate state can instead be selected through client
 * properties:
 * <pre>
 * "TristateCheckBox.icon", null,
 * "TristateCheckBox.setMixed.clientProperty", new Object[]{"JButton.selectedState", "indeterminate"},
 * "TristateCheckBox.clearMixed.clientProperty", new Object[]{"JButton.selectedState", null},
 * </pre>
 * The correct listener for state change is ActionListener. It will be fired when the state is changed. The ItemListener
 * is only fired when changing from selected state to unselected state or vice versa. Only ActionListener will be fired
 * for all three states.
 */
public class TristateCheckBox extends JCheckBox implements ActionListener {
    private static final String ORIGINAL_ICON_PROPERTY = "TristateCheckBox.originalIcon";
    public static final int STATE_UNSELECTED = 0;
    public static final int STATE_SELECTED = 1;
    public static final int STATE_MIXED = 2;

    public TristateCheckBox(String text, Icon icon) {
        super(text, icon);
    }

    public TristateCheckBox(String text) {
        this(text, null);
    }

    public TristateCheckBox() {
        this(null);
    }

    @Override
    protected void init(String text, Icon icon) {
        model = createButtonModel();
        setModel(model);
        addActionListener(this);
        super.init(text, icon);
    }


    /**
     * Creates the button model. In this case, it is always a TristateButtonModel.
     *
     * @return TristateButtonModel
     */
    protected ButtonModel createButtonModel() {
        return new TristateButtonModel();
    }

    @Override
    public void updateUI() {
        super.updateUI();
        if (isMixed()) {
            adjustMixedIcon();
        }
        else {
            restoreMixedIcon();
        }
    }

    protected void adjustMixedIcon() {
        if (getClientProperty(ORIGINAL_ICON_PROPERTY) == null) {
            Icon icon = getIcon();
            putClientProperty(ORIGINAL_ICON_PROPERTY, icon != null ? icon : Boolean.TRUE);
        }
        setIcon(UIManager.getIcon("TristateCheckBox.icon"));
    }

    protected void restoreMixedIcon() {
        Object icon = getClientProperty(ORIGINAL_ICON_PROPERTY);
        if (icon != null) {
            putClientProperty(ORIGINAL_ICON_PROPERTY, null);
            setIcon(Boolean.TRUE.equals(icon) ? null : (Icon) icon);
        }
    }

    /**
     * Checks if the check box is in mixed selection state.
     *
     * @return true or false.
     */
    public boolean isMixed() {
        return getState() == STATE_MIXED;
    }

    /**
     * Sets the check box to mixed selection state.
     *
     * @param b true or false. True means mixed state. False means unselected state.
     */
    public void setMixed(boolean b) {
        if (b) {
            setState(STATE_MIXED);
        }
        else {
            setState(STATE_UNSELECTED);
        }
    }

    /**
     * Gets the selection state. It could be one of the three states as defined - {@link #STATE_SELECTED}, {@link
     * #STATE_UNSELECTED} and {@link #STATE_MIXED}.
     *
     * @return one of the three selection states.
     */
    public int getState() {
        if (model instanceof TristateButtonModel tristateModel)
            return tristateModel.getState();
        else {
            throw new IllegalStateException("TristateButtonModel is required for TristateCheckBox");
        }
    }

    @Override
    public void setSelected(boolean b) {
        if (b) {
            setState(STATE_SELECTED);
        }
        else {
            setState(STATE_UNSELECTED);
        }
    }

    /**
     * Sets the selection state. It could be one of the three states as defined - {@link #STATE_SELECTED}, {@link
     * #STATE_UNSELECTED} and {@link #STATE_MIXED}.
     *
     * @param state one of the three selection states.
     */
    public void setState(int state) {
        if (model instanceof TristateButtonModel tristateModel) {
            int old = tristateModel.getState();
            if (old != state) tristateModel.setState(state);
            stateUpdated(state);
        }
        else {
            throw new IllegalStateException("TristateButtonModel is required for TristateCheckBox");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        stateUpdated(getState());
    }

    /**
     * This method is called when the selection state changes.
     *
     * @param state the new selection state.
     */
    protected void stateUpdated(int state) {
        if (state == STATE_MIXED) {
            adjustMixedIcon();
            Object cp = UIDefaultsLookup.get("TristateCheckBox.setMixed.clientProperty");
            if (cp != null) {
                putClientProperty(((Object[]) cp)[0], ((Object[]) cp)[1]); // for L&F that uses client property
            }
        }
        else {
            restoreMixedIcon();
            Object cp = UIDefaultsLookup.get("TristateCheckBox.clearMixed.clientProperty");
            if (cp != null) {
                putClientProperty(((Object[]) cp)[0], ((Object[]) cp)[1]); // for L&F that uses client property
            }
        }
    }
}

