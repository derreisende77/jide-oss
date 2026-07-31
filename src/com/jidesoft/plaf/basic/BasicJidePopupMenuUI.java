/*
 * @(#)BasicJidePopupMenuUI.java 12/13/2006
 *
 * Copyright 2002 - 2006 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.plaf.basic;

import com.jidesoft.swing.JidePopupMenu;
import com.jidesoft.swing.SimpleScrollPane;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPopupMenuUI;
import javax.swing.plaf.basic.DefaultMenuLayout;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BasicJidePopupMenuUI extends BasicPopupMenuUI {
    public BasicJidePopupMenuUI() {
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public static ComponentUI createUI(JComponent c) {
        return new BasicJidePopupMenuUI();
    }

    @Override
    public Popup getPopup(JPopupMenu popupMenu, int x, int y) {
        Popup popup = BasicJidePopupMenuUI.addScrollPaneIfNecessary(popupMenu, x, y);
        return popup == null ? super.getPopup(popupMenu, x, y) : popup;
    }

    /**
     * Adds a scroll pane to the popup menu if the popup menu is taller than the screen boundary.
     *
     * @param popupMenu the popup menu.
     * @param x         the x origin
     * @param y         the y origin
     * @return Popup
     */
    public static Popup addScrollPaneIfNecessary(final JPopupMenu popupMenu, int x, int y) {
        final SimpleScrollPane contents = new SimpleScrollPane(popupMenu, SimpleScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, SimpleScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        if (popupMenu instanceof JidePopupMenu && popupMenu.getPreferredSize().height != ((JidePopupMenu) popupMenu).getPreferredScrollableViewportSize().height) {
            if (popupMenu.getLayout() instanceof DefaultMenuLayout) {
                popupMenu.setLayout(new BoxLayout(popupMenu, ((DefaultMenuLayout) popupMenu.getLayout()).getAxis()));
            }
            PopupFactory popupFactory = PopupFactory.getSharedInstance();
            contents.getScrollUpButton().setOpaque(true);
            contents.getScrollDownButton().setOpaque(true);
            contents.setBorder(BorderFactory.createEmptyBorder());
            final Popup popup = popupFactory.getPopup(popupMenu.getInvoker(), contents, x, y);
            final List<JMenuItem> menuItems = new ArrayList<>();
            final ChangeListener scrollListener = new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    if (e.getSource() instanceof JMenuItem) {
                        if (((JMenuItem) e.getSource()).getModel().isArmed()) {
                            popupMenu.scrollRectToVisible(((JMenuItem) e.getSource()).getBounds());
                            Point position = contents.getViewport().getViewPosition();
                            contents.getScrollUpButton().setEnabled(position.y > 2);
                            contents.getScrollDownButton().setEnabled(position.y < contents.getViewport().getViewSize().height - contents.getViewport().getViewRect().height - 2);
                        }
                    }
                }
            };
            Component[] components = popupMenu.getComponents();
            for (Component component : components) {
                if (component instanceof JMenuItem) {
                    JMenuItem menuItem = (JMenuItem) component;
                    menuItem.addChangeListener(scrollListener);
                    menuItems.add(menuItem);
                }
            }
            return new Popup() {
                private boolean _listenersRemoved;

                @Override
                public void show() {
                    popup.show();
                }

                @Override
                public void hide() {
                    try {
                        popup.hide();
                    }
                    finally {
                        if (!_listenersRemoved) {
                            for (JMenuItem menuItem : menuItems) {
                                menuItem.removeChangeListener(scrollListener);
                            }
                            _listenersRemoved = true;
                        }
                    }
                }
            };
        }
        else {
            return null;
        }
    }
}
