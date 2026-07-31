package com.github.derreisende77;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.CheckBoxList;
import com.jidesoft.swing.JideButton;
import com.jidesoft.swing.JideLabel;
import com.jidesoft.swing.JidePopupMenu;
import com.jidesoft.swing.JideSplitButton;
import com.jidesoft.swing.JideTabbedPane;
import com.jidesoft.swing.JideToggleButton;
import com.jidesoft.swing.JideToggleSplitButton;
import com.jidesoft.swing.RangeSlider;
import com.jidesoft.swing.SplitButtonGroup;
import com.jidesoft.swing.TristateCheckBox;

import javax.accessibility.AccessibleState;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComponentUI;
import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Interactive showcase for the JIDE controls maintained on this branch.
 */
public final class FlatLafControlShowcase {
    private static final Color ACCENT = new Color(78, 116, 255);
    private static final int HYPERLINK_STYLE = 3;
    private static final int GAP = 16;

    private JFrame frame;
    private JLabel statusLabel;
    private JideTabbedPane showcaseTabs;
    private boolean darkMode;
    private final List<JComponent> detachedThemeComponents = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        boolean smoke = Arrays.asList(args).contains("--smoke");
        boolean render = Arrays.asList(args).contains("--render");
        installLookAndFeel(false);

        if (render) {
            SwingUtilities.invokeAndWait(() -> {
                try {
                    new FlatLafControlShowcase().renderLivePreviews();
                }
                catch (IOException exception) {
                    throw new IllegalStateException("Could not render demo previews", exception);
                }
            });
            return;
        }

        if (smoke) {
            SwingUtilities.invokeAndWait(() -> {
                FlatLafControlShowcase showcase = new FlatLafControlShowcase();
                JComponent content = showcase.createContent();
                content.setSize(1180, 790);
                layoutTree(content);
                showcase.verifyDetachedThemeRefresh();
                System.out.println("FlatLaf control showcase smoke check passed");
            });
            return;
        }

        EventQueue.invokeLater(() -> new FlatLafControlShowcase().show());
    }

    private static void installLookAndFeel(boolean dark) {
        if (dark) {
            FlatDarkLaf.setup();
        }
        else {
            FlatLightLaf.setup();
        }
        LookAndFeelFactory.installJideExtension();
    }

    private void show() {
        frame = new JFrame("JIDE OSS - FlatLaf Control Showcase");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(createContent());
        frame.setMinimumSize(new Dimension(940, 680));
        frame.setSize(1180, 790);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JComponent createContent() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(new EmptyBorder(22, 26, 18, 26));
        root.add(createHeader(), BorderLayout.NORTH);

        showcaseTabs = new JideTabbedPane();
        showcaseTabs.setBoldActiveTab(true);
        showcaseTabs.setTabResizeMode(JideTabbedPane.RESIZE_MODE_FIT);
        showcaseTabs.setShowTabButtons(true);
        showcaseTabs.addTab("Essentials", createEssentialsPage());
        showcaseTabs.addTab("Selection", createSelectionPage());
        showcaseTabs.addTab("Menus & Popups", createPopupPage());
        showcaseTabs.addTab("Tabs", createTabsPage());
        root.add(showcaseTabs, BorderLayout.CENTER);

        statusLabel = new JLabel("Ready - try every control and switch themes at any time.");
        statusLabel.setBorder(new EmptyBorder(12, 2, 0, 2));
        root.add(statusLabel, BorderLayout.SOUTH);
        return root;
    }

    private JComponent createHeader() {
        JPanel header = new JPanel(new BorderLayout(20, 0));
        header.setBorder(new EmptyBorder(0, 0, 20, 0));

        JPanel titleBlock = verticalPanel();
        JLabel title = new JLabel("JIDE OSS Control Lab");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 28f));
        JLabel subtitle = new JLabel(
                "Eleven branch-maintained Swing controls, running on FlatLaf");
        subtitle.setFont(subtitle.getFont().deriveFont(14f));
        subtitle.setForeground(UIManager.getColor("Label.disabledForeground"));
        titleBlock.add(title);
        titleBlock.add(Box.createVerticalStrut(4));
        titleBlock.add(subtitle);
        header.add(titleBlock, BorderLayout.CENTER);

        JideToggleButton light = new JideToggleButton("Light");
        JideToggleButton dark = new JideToggleButton("Dark");
        styleRoundButton(light);
        styleRoundButton(dark);
        light.setSelected(true);

        ButtonGroup themes = new ButtonGroup();
        themes.add(light);
        themes.add(dark);
        light.addActionListener(event -> switchTheme(false));
        dark.addActionListener(event -> switchTheme(true));

        JPanel themePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        themePanel.add(new JLabel("Theme"));
        themePanel.add(light);
        themePanel.add(dark);
        header.add(themePanel, BorderLayout.EAST);
        return header;
    }

    private JComponent createEssentialsPage() {
        JPanel page = pageGrid();
        page.add(createButtonCard(), cell(0, 0, 0.54));
        page.add(createLabelAndRangeCard(), cell(1, 0, 0.46));
        return page;
    }

    private JComponent createButtonCard() {
        JPanel body = verticalPanel();

        JideButton primary = new JideButton("Run action",
                new AccentIcon(ACCENT, AccentIcon.PLAY));
        styleRoundButton(primary);
        primary.addActionListener(event -> setStatus("JideButton dispatched its action."));

        JideButton hyperlink = new JideButton("Open project documentation");
        hyperlink.setButtonStyle(HYPERLINK_STYLE);
        hyperlink.setAlwaysShowHyperlink(true);
        hyperlink.addActionListener(event -> setStatus("Hyperlink-style JideButton clicked."));

        JideToggleButton pinned = new JideToggleButton("Pin dashboard",
                new AccentIcon(ACCENT, AccentIcon.PIN));
        styleRoundButton(pinned);
        pinned.addActionListener(event ->
                setStatus("JideToggleButton: dashboard " +
                        (pinned.isSelected() ? "pinned." : "unpinned.")));

        JideToggleButton listMode = new JideToggleButton("List", true);
        JideToggleButton gridMode = new JideToggleButton("Grid");
        listMode.putClientProperty("JButton.buttonType", "roundRect");
        gridMode.putClientProperty("JButton.buttonType", "roundRect");
        ButtonGroup viewMode = new ButtonGroup();
        viewMode.add(listMode);
        viewMode.add(gridMode);
        listMode.addActionListener(event -> setStatus("JideToggleButton view mode: List."));
        gridMode.addActionListener(event -> setStatus("JideToggleButton view mode: Grid."));

        JideButton saveToggleState = new JideButton("Save state");
        styleRoundButton(saveToggleState);
        saveToggleState.addActionListener(event -> {
            try {
                JideToggleButton restored = roundTripToggle(
                        new JideToggleButton("Saved state", pinned.isSelected()));
                setStatus("JideToggleButton state restored: " +
                        (restored.isSelected() ? "selected." : "not selected."));
            }
            catch (IOException | ClassNotFoundException exception) {
                setStatus("Could not restore JideToggleButton state.");
            }
        });

        JideSplitButton export = new JideSplitButton("Export");
        styleRoundButton(export);
        addMenuAction(export, "PDF report");
        addMenuAction(export, "CSV data");
        addMenuAction(export, "PNG snapshot");
        export.addActionListener(event -> setStatus("JideSplitButton primary export action."));

        Action syncAction = new AbstractAction("Auto sync") {
            @Override
            public void actionPerformed(ActionEvent event) {
                JideToggleSplitButton source = (JideToggleSplitButton) event.getSource();
                setStatus("JideToggleSplitButton action: auto sync " +
                        (source.isButtonSelected() ? "enabled." : "disabled."));
            }
        };
        syncAction.putValue(Action.SELECTED_KEY, true);
        JideToggleSplitButton sync = new JideToggleSplitButton(syncAction);
        styleRoundButton(sync);
        addMenuAction(sync, "Every 5 minutes");
        addMenuAction(sync, "Every hour");
        addMenuAction(sync, "Manual only");

        JideToggleSplitButton scheduled = new JideToggleSplitButton("Scheduled");
        styleRoundButton(scheduled);
        addMenuAction(scheduled, "At startup");
        addMenuAction(scheduled, "Every night");
        SplitButtonGroup syncModes = new SplitButtonGroup();
        syncModes.add(sync);
        syncModes.add(scheduled);

        JLabel splitProof = hint("ItemEvent: ready | Action: true | Accessible: checked");
        sync.addItemListener(event -> SwingUtilities.invokeLater(() ->
                updateToggleSplitProof(splitProof, sync, event)));
        scheduled.addItemListener(event -> SwingUtilities.invokeLater(() ->
                updateToggleSplitProof(splitProof, scheduled, event)));

        JideButton selectFromAction = new JideButton("Toggle via Action");
        styleRoundButton(selectFromAction);
        selectFromAction.addActionListener(event -> syncAction.putValue(
                Action.SELECTED_KEY,
                !Boolean.TRUE.equals(syncAction.getValue(Action.SELECTED_KEY))));

        JideButton saveSplitState = new JideButton("Save split state");
        styleRoundButton(saveSplitState);
        saveSplitState.addActionListener(event -> {
            try {
                SerializableToggleAction savedAction = new SerializableToggleAction();
                savedAction.putValue(Action.SELECTED_KEY, sync.isButtonSelected());
                JideToggleSplitButton restored = roundTripToggleSplit(
                        new JideToggleSplitButton(savedAction));
                setStatus("JideToggleSplitButton restored with Action state: " +
                        (restored.isButtonSelected() ? "selected." : "not selected."));
            }
            catch (IOException | ClassNotFoundException exception) {
                setStatus("Could not restore JideToggleSplitButton state.");
            }
        });

        body.add(sectionLabel("Push and toggle"));
        body.add(Box.createVerticalStrut(8));
        body.add(flow(primary, pinned));
        body.add(Box.createVerticalStrut(18));
        body.add(sectionLabel("Grouped focus & saved state"));
        body.add(Box.createVerticalStrut(8));
        body.add(flow(listMode, gridMode, saveToggleState));
        body.add(Box.createVerticalStrut(8));
        body.add(hint("Select a mode, tab away, then return with keyboard traversal."));
        body.add(Box.createVerticalStrut(18));
        body.add(sectionLabel("Split actions"));
        body.add(Box.createVerticalStrut(8));
        body.add(flow(export));
        body.add(Box.createVerticalStrut(18));
        body.add(sectionLabel("Verified toggle split state"));
        body.add(Box.createVerticalStrut(8));
        body.add(flow(sync, scheduled, selectFromAction, saveSplitState));
        body.add(Box.createVerticalStrut(8));
        body.add(splitProof);
        body.add(Box.createVerticalStrut(14));
        body.add(hyperlink);
        body.add(Box.createVerticalGlue());

        return card("Buttons & commands",
                "Primary actions, grouped toggle focus, saved state, and split menus.", body);
    }

    private static JideToggleButton roundTripToggle(JideToggleButton button)
            throws IOException, ClassNotFoundException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream output = new ObjectOutputStream(bytes)) {
            output.writeObject(button);
        }
        try (ObjectInputStream input = new ObjectInputStream(
                new ByteArrayInputStream(bytes.toByteArray()))) {
            return (JideToggleButton) input.readObject();
        }
    }

    private static JideToggleSplitButton roundTripToggleSplit(JideToggleSplitButton button)
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

    private static void updateToggleSplitProof(
            JLabel proof, JideToggleSplitButton button, ItemEvent event) {
        boolean checked = button.getAccessibleContext().getAccessibleStateSet()
                .contains(AccessibleState.CHECKED);
        Object actionSelected = button.getAction() == null
                ? "n/a"
                : button.getAction().getValue(Action.SELECTED_KEY);
        proof.setText(button.getText() + " | ItemEvent: " +
                (event.getStateChange() == ItemEvent.SELECTED ? "SELECTED" : "DESELECTED") +
                " | Action: " + actionSelected +
                " | Accessible: " + (checked ? "checked" : "unchecked"));
    }

    private JComponent createLabelAndRangeCard() {
        JPanel body = verticalPanel();

        JideLabel standard = new JideLabel("Horizontal JideLabel");
        standard.setFont(standard.getFont().deriveFont(Font.BOLD, 15f));

        JideLabel clockwise = verticalLabel("Clockwise", true);
        JideLabel counterClockwise = verticalLabel("Counter", false);
        JPanel verticalLabels = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 4));
        verticalLabels.setOpaque(false);
        verticalLabels.add(clockwise);
        verticalLabels.add(counterClockwise);
        lockHeight(verticalLabels);

        RangeSlider range = new RangeSlider(0, 100, 24, 76);
        range.setMajorTickSpacing(25);
        range.setMinorTickSpacing(5);
        range.setPaintTicks(true);
        range.setPaintLabels(true);
        range.setPreferredSize(new Dimension(390, 74));
        range.setMaximumSize(range.getPreferredSize());
        JLabel rangeValue = new JLabel(rangeText(range));
        rangeValue.setFont(rangeValue.getFont().deriveFont(Font.BOLD));
        range.addChangeListener(event -> {
            rangeValue.setText(rangeText(range));
            setStatus("RangeSlider selection: " + rangeText(range));
        });

        body.add(standard);
        body.add(Box.createVerticalStrut(10));
        body.add(verticalLabels);
        body.add(Box.createVerticalStrut(16));
        body.add(sectionLabel("Range selection"));
        body.add(range);
        body.add(rangeValue);
        body.add(Box.createVerticalStrut(8));
        body.add(hint("Drag either thumb. Shift + arrow moves the upper value."));
        body.add(Box.createVerticalGlue());

        return card("Labels & range",
                "Rotated labels in both directions and an independent two-thumb range.", body);
    }

    private JComponent createSelectionPage() {
        JPanel page = pageGrid();
        page.add(createCheckBoxListCard(), cell(0, 0, 0.62));
        page.add(createTristateCard(), cell(1, 0, 0.38));
        return page;
    }

    private JComponent createCheckBoxListCard() {
        DefaultListModel<String> model = new DefaultListModel<>();
        model.addAll(Arrays.asList(
                "Buttons and command bars",
                "Range and filter controls",
                "Popup lifecycle",
                "Tabbed workspace",
                "Light and dark themes",
                "Keyboard navigation"
        ));

        CheckBoxList list = new CheckBoxList(model);
        list.setClickInCheckBoxOnly(false);
        list.setFixedCellHeight(32);
        list.setVisibleRowCount(7);
        list.setCheckBoxListSelectedIndices(new int[]{0, 1, 4});
        list.getCheckBoxListSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                setStatus("CheckBoxList: " +
                        list.getCheckBoxListSelectedValues().length + " items checked.");
            }
        });

        JideButton all = new JideButton("Select all");
        JideButton none = new JideButton("Clear");
        JideToggleButton wholeRowMode = new JideToggleButton("Whole row", true);
        JideToggleButton checkBoxOnlyMode = new JideToggleButton("Checkbox only");
        styleRoundButton(all);
        styleRoundButton(none);
        wholeRowMode.putClientProperty("JButton.buttonType", "roundRect");
        checkBoxOnlyMode.putClientProperty("JButton.buttonType", "roundRect");
        ButtonGroup clickMode = new ButtonGroup();
        clickMode.add(wholeRowMode);
        clickMode.add(checkBoxOnlyMode);
        JLabel clickModeHint = hint("Whole-row mode toggles a check from anywhere inside its cell.");
        all.addActionListener(event -> list.selectAll());
        none.addActionListener(event -> list.selectNone());
        wholeRowMode.addActionListener(event -> {
            list.setClickInCheckBoxOnly(false);
            clickModeHint.setText("Whole-row mode toggles a check from anywhere inside its cell.");
            setStatus("CheckBoxList click mode: whole row.");
        });
        checkBoxOnlyMode.addActionListener(event -> {
            list.setClickInCheckBoxOnly(true);
            clickModeHint.setText("Checkbox-only mode leaves clicks on the cell text unchecked.");
            setStatus("CheckBoxList click mode: checkbox only.");
        });

        JPanel controls = verticalPanel();
        controls.add(flow(wholeRowMode, checkBoxOnlyMode, all, none));
        controls.add(Box.createVerticalStrut(6));
        controls.add(clickModeHint);

        JPanel body = new JPanel(new BorderLayout(0, 12));
        body.setOpaque(false);
        body.add(new JScrollPane(list), BorderLayout.CENTER);
        body.add(controls, BorderLayout.SOUTH);
        return card("CheckBoxList",
                "Switch between whole-row and checkbox-only toggling; list selection remains independent.",
                body);
    }

    private JComponent createTristateCard() {
        JPanel body = verticalPanel();

        TristateCheckBox inherited = new TristateCheckBox("Inherit workspace setting");
        inherited.setMixed(true);
        TristateCheckBox enabled = new TristateCheckBox("Enable notifications");
        enabled.setSelected(true);
        TristateCheckBox disabled = new TristateCheckBox("Archive automatically");

        inherited.addActionListener(event -> updateTristateStatus(inherited));
        enabled.addActionListener(event -> updateTristateStatus(enabled));
        disabled.addActionListener(event -> updateTristateStatus(disabled));

        Icon customIcon = new AccentIcon(ACCENT, AccentIcon.WINDOW);
        TristateCheckBox eventCycle = new TristateCheckBox("Custom icon survives the cycle", customIcon);
        JLabel eventProof = hint("ItemEvents: 0 selected / 0 deselected | icon preserved: yes");
        int[] itemEvents = {0, 0};
        eventCycle.addItemListener(event -> {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                itemEvents[0]++;
            }
            else {
                itemEvents[1]++;
            }
            SwingUtilities.invokeLater(() -> eventProof.setText(
                    "ItemEvents: " + itemEvents[0] + " selected / " + itemEvents[1] +
                            " deselected | icon preserved: " +
                            (eventCycle.isMixed() || eventCycle.getIcon() == customIcon ? "yes" : "no")));
        });
        eventCycle.addActionListener(event -> {
            updateTristateStatus(eventCycle);
            SwingUtilities.invokeLater(() -> eventProof.setText(
                    "ItemEvents: " + itemEvents[0] + " selected / " + itemEvents[1] +
                            " deselected | icon preserved: " +
                            (eventCycle.isMixed() || eventCycle.getIcon() == customIcon ? "yes" : "no")));
        });

        TristateCheckBox workspacePolicy = new TristateCheckBox("Workspace policy");
        TristateCheckBox projectPolicy = new TristateCheckBox("Project override");
        ButtonGroup policyGroup = new ButtonGroup();
        policyGroup.add(workspacePolicy);
        policyGroup.add(projectPolicy);
        workspacePolicy.setSelected(true);
        JLabel groupProof = hint("Selected group member: Workspace policy");
        workspacePolicy.addActionListener(event ->
                updateTristateGroupProof(groupProof, workspacePolicy, projectPolicy));
        projectPolicy.addActionListener(event ->
                updateTristateGroupProof(groupProof, workspacePolicy, projectPolicy));

        body.add(sectionLabel("Three distinct states"));
        body.add(Box.createVerticalStrut(8));
        body.add(inherited);
        body.add(Box.createVerticalStrut(12));
        body.add(enabled);
        body.add(Box.createVerticalStrut(12));
        body.add(disabled);
        body.add(Box.createVerticalStrut(20));
        body.add(sectionLabel("Balanced events & custom icon"));
        body.add(Box.createVerticalStrut(8));
        body.add(eventCycle);
        body.add(Box.createVerticalStrut(6));
        body.add(eventProof);
        body.add(Box.createVerticalStrut(20));
        body.add(sectionLabel("Exclusive grouped states"));
        body.add(Box.createVerticalStrut(8));
        body.add(workspacePolicy);
        body.add(Box.createVerticalStrut(8));
        body.add(projectPolicy);
        body.add(Box.createVerticalStrut(6));
        body.add(groupProof);
        body.add(Box.createVerticalStrut(14));
        body.add(hint("Click a selected group member again to reach its mixed state."));
        body.add(Box.createVerticalGlue());

        return card("TristateCheckBox",
                "Unchecked, selected, and mixed states with FlatLaf indicators.", body);
    }

    private JComponent createPopupPage() {
        JPanel page = pageGrid();
        page.add(createPopupMenuCard(), cell(0, 0, 0.5));
        page.add(createPopupWindowCard(), cell(1, 0, 0.5));
        return page;
    }

    private JComponent createPopupMenuCard() {
        JPanel body = verticalPanel();
        JideButton openMenu = new JideButton("Open scrolling menu",
                new AccentIcon(ACCENT, AccentIcon.MENU));
        styleRoundButton(openMenu);

        JidePopupMenu menu = new JidePopupMenu();
        detachedThemeComponents.add(menu);
        menu.setVisibleMenuItemCount(6);
        for (int i = 1; i <= 12; i++) {
            JMenuItem item = new JMenuItem("Recent workspace " + i);
            int index = i;
            item.addActionListener(event ->
                    setStatus("JidePopupMenu selected workspace " + index + "."));
            menu.add(item);
        }
        openMenu.addActionListener(event ->
                menu.show(openMenu, 0, openMenu.getHeight() + 4));

        body.add(openMenu);
        body.add(Box.createVerticalStrut(18));
        body.add(hint("The menu limits visible rows and installs scrolling controls."));
        body.add(Box.createVerticalStrut(8));
        body.add(hint("Open it, switch themes, then open it again to inspect lifecycle refresh."));
        body.add(Box.createVerticalGlue());
        return card("JidePopupMenu",
                "A long, scrollable popup menu with lifecycle-safe listeners.", body);
    }

    private JComponent createPopupWindowCard() {
        JPanel body = verticalPanel();
        JideButton openPopup = new JideButton("Open floating inspector",
                new AccentIcon(ACCENT, AccentIcon.WINDOW));
        styleRoundButton(openPopup);
        openPopup.addActionListener(event -> showInspectorPopup(openPopup));

        body.add(openPopup);
        body.add(Box.createVerticalStrut(18));
        body.add(hint("The JidePopup is movable and resizable."));
        body.add(Box.createVerticalStrut(8));
        body.add(hint("Close and reopen it to exercise owner and container lifecycle handling."));
        body.add(Box.createVerticalGlue());
        return card("JidePopup",
                "A detachable-style floating inspector anchored to its owner.", body);
    }

    private JComponent createTabsPage() {
        JPanel body = new JPanel(new BorderLayout(0, 14));
        body.setOpaque(false);

        JideTabbedPane workspace = new JideTabbedPane();
        workspace.setBoldActiveTab(true);
        workspace.setShowCloseButtonOnTab(true);
        workspace.setShowCloseButtonOnMouseOver(true);
        workspace.setCloseTabOnMouseMiddleButton(true);
        workspace.setTabResizeMode(JideTabbedPane.RESIZE_MODE_FIT);
        workspace.addTab("Dashboard", tabContent("Dashboard", "Live control overview"));
        workspace.addTab("Activity", tabContent("Activity", "Theme and action events"));
        workspace.addTab("Settings", tabContent("Settings", "Component preferences"));
        workspace.setTabClosableAt(0, false);

        JideButton addTab = new JideButton("Add tab");
        styleRoundButton(addTab);
        addTab.addActionListener(event -> {
            int number = workspace.getTabCount() + 1;
            workspace.addTab("View " + number,
                    tabContent("View " + number, "A dynamically added closable tab"));
            workspace.setSelectedIndex(workspace.getTabCount() - 1);
            setStatus("JideTabbedPane added View " + number + ".");
        });

        JideButton moveLeft = new JideButton("Move left");
        styleRoundButton(moveLeft);
        moveLeft.addActionListener(event -> {
            int selectedIndex = workspace.getSelectedIndex();
            if (selectedIndex > 0) {
                workspace.moveSelectedTabTo(selectedIndex - 1);
                setStatus("JideTabbedPane moved the selected tab left with its properties intact.");
            }
        });

        JideButton moveRight = new JideButton("Move right");
        styleRoundButton(moveRight);
        moveRight.addActionListener(event -> {
            int selectedIndex = workspace.getSelectedIndex();
            if (selectedIndex >= 0 && selectedIndex < workspace.getTabCount() - 1) {
                workspace.moveSelectedTabTo(selectedIndex + 1);
                setStatus("JideTabbedPane moved the selected tab right with its properties intact.");
            }
        });

        JideButton replaceContent = new JideButton("Replace content");
        styleRoundButton(replaceContent);
        replaceContent.addActionListener(event -> {
            int selectedIndex = workspace.getSelectedIndex();
            if (selectedIndex >= 0) {
                String title = workspace.getTitleAt(selectedIndex);
                workspace.setComponentAt(selectedIndex,
                        tabContent(title, "Replacement content with fresh focus tracking"));
                setStatus("JideTabbedPane replaced the selected tab content.");
            }
        });

        body.add(workspace, BorderLayout.CENTER);
        body.add(flow(addTab, moveLeft, moveRight, replaceContent), BorderLayout.SOUTH);
        return card("JideTabbedPane workspace",
                "Fit-mode tabs with safe moving, content replacement, and dynamic tabs.", body);
    }

    private void showInspectorPopup(Component owner) {
        JidePopup popup = new JidePopup();
        popup.setOwner(owner);
        popup.setPopupType(JidePopup.HEAVY_WEIGHT_POPUP);
        popup.setMovable(true);
        popup.setResizable(true);
        popup.setPreferredPopupSize(new Dimension(330, 190));

        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setBorder(new EmptyBorder(16, 18, 16, 18));
        JLabel title = new JLabel("Floating inspector");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 17f));
        content.add(title, BorderLayout.NORTH);
        content.add(new JLabel("<html>Owner retained across hide/show cycles.<br>" +
                "Drag the gripper or resize the popup.</html>"), BorderLayout.CENTER);
        JideButton close = new JideButton("Close");
        styleRoundButton(close);
        close.addActionListener(event -> popup.hidePopupImmediately());
        content.add(flow(close), BorderLayout.SOUTH);
        popup.setContentPane(content);

        Point location = owner.getLocationOnScreen();
        popup.showPopup(location.x + owner.getWidth() + 10, location.y, owner);
        setStatus("JidePopup floating inspector opened.");
    }

    private void switchTheme(boolean dark) {
        if (darkMode == dark) {
            return;
        }
        darkMode = dark;
        installLookAndFeel(dark);
        FlatLaf.updateUI();
        for (JComponent component : detachedThemeComponents) {
            SwingUtilities.updateComponentTreeUI(component);
        }
        for (Window window : Window.getWindows()) {
            SwingUtilities.updateComponentTreeUI(window);
        }
        setStatus((dark ? "Dark" : "Light") + " FlatLaf theme applied.");
    }

    private void verifyDetachedThemeRefresh() {
        JComponent popup = detachedThemeComponents.get(0);
        JComponent popupItem = (JComponent) popup.getComponent(0);
        ComponentUI popupUI = popup.getUI();
        ComponentUI popupItemUI = popupItem.getUI();

        switchTheme(true);

        if (popup.getUI() == popupUI || popupItem.getUI() == popupItemUI) {
            throw new IllegalStateException("Detached popup did not refresh with the FlatLaf theme");
        }
        switchTheme(false);
    }

    private void addMenuAction(JideSplitButton button, String label) {
        JMenuItem item = new JMenuItem(label);
        item.addActionListener(event ->
                setStatus(button.getText() + ": " + label + " selected."));
        button.add(item);
    }

    private void updateTristateStatus(TristateCheckBox checkBox) {
        String state;
        if (checkBox.isMixed()) {
            state = "mixed";
        }
        else {
            state = checkBox.isSelected() ? "selected" : "unselected";
        }
        setStatus("TristateCheckBox \"" + checkBox.getText() + "\" is " + state + ".");
    }

    private static void updateTristateGroupProof(
            JLabel proof, TristateCheckBox first, TristateCheckBox second) {
        TristateCheckBox selected = first.isSelected() ? first : second;
        proof.setText("Selected group member: " + selected.getText() +
                (selected.isMixed() ? " (mixed)" : ""));
    }

    private void setStatus(String text) {
        if (statusLabel != null) {
            statusLabel.setText(text);
        }
    }

    private static JideLabel verticalLabel(String text, boolean clockwise) {
        JideLabel label = new JideLabel(text);
        label.setOrientation(SwingConstants.VERTICAL);
        label.setClockwise(clockwise);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(BorderFactory.createCompoundBorder(
                new CardBorder(12),
                new EmptyBorder(7, 13, 10, 5)));
        label.setPreferredSize(new Dimension(48, 118));
        return label;
    }

    private static String rangeText(RangeSlider range) {
        return range.getLowValue() + " - " + range.getHighValue();
    }

    private static JPanel pageGrid() {
        JPanel page = new JPanel(new GridBagLayout());
        page.setBorder(new EmptyBorder(18, 0, 2, 0));
        return page;
    }

    private static GridBagConstraints cell(int x, int y, double weight) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = x;
        constraints.gridy = y;
        constraints.weightx = weight;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.insets = x == 0
                ? new Insets(0, 0, 0, GAP / 2)
                : new Insets(0, GAP / 2, 0, 0);
        return constraints;
    }

    private static JPanel verticalPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void addImpl(Component component, Object constraints, int index) {
                if (component instanceof JComponent) {
                    ((JComponent) component).setAlignmentX(Component.LEFT_ALIGNMENT);
                }
                super.addImpl(component, constraints, index);
            }
        };
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        return panel;
    }

    private static JPanel flow(Component... components) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.setOpaque(false);
        for (Component component : components) {
            panel.add(component);
        }
        lockHeight(panel);
        return panel;
    }

    private static JComponent card(String title, String description, JComponent content) {
        CardPanel panel = new CardPanel();
        panel.setLayout(new BorderLayout(0, 16));

        JPanel heading = verticalPanel();
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 19f));
        JLabel descriptionLabel = new JLabel("<html><div style='width:300px'>" +
                description + "</div></html>");
        descriptionLabel.setForeground(UIManager.getColor("Label.disabledForeground"));
        heading.add(titleLabel);
        heading.add(Box.createVerticalStrut(5));
        heading.add(descriptionLabel);
        heading.add(Box.createVerticalStrut(8));
        heading.add(new JSeparator());

        panel.add(heading, BorderLayout.NORTH);
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private static JLabel sectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 13f));
        return label;
    }

    private static JLabel hint(String text) {
        JLabel label = new JLabel("<html><div style='width:300px'>" + text + "</div></html>");
        label.setForeground(UIManager.getColor("Label.disabledForeground"));
        return label;
    }

    private static JComponent tabContent(String title, String subtitle) {
        JPanel panel = new JPanel(new GridBagLayout());
        JPanel text = verticalPanel();
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 24f));
        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setForeground(UIManager.getColor("Label.disabledForeground"));
        text.add(titleLabel);
        text.add(Box.createVerticalStrut(5));
        text.add(subtitleLabel);
        panel.add(text);
        return panel;
    }

    private static void styleRoundButton(AbstractButton button) {
        button.putClientProperty("JButton.buttonType", "roundRect");
        button.setFocusable(false);
    }

    private void renderPreviews(JComponent content, String theme) throws IOException {
        for (int index = 0; index < showcaseTabs.getTabCount(); index++) {
            showcaseTabs.setSelectedIndex(index);
            frame.validate();
            renderPreview(content, theme + "-" + (index + 1));
        }
    }

    private void renderLivePreviews() throws IOException {
        frame = new JFrame("JIDE OSS - FlatLaf Control Showcase");
        frame.setContentPane(createContent());
        frame.setSize(1180, 790);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        try {
            renderPreviews((JComponent) frame.getContentPane(), "light");
            installLookAndFeel(true);
            SwingUtilities.updateComponentTreeUI(frame);
            renderPreviews((JComponent) frame.getContentPane(), "dark");
            System.out.println("Demo previews written to build/reports/demo");
        }
        finally {
            frame.dispose();
        }
    }

    private static void renderPreview(JComponent content, String name) throws IOException {
        content.setSize(1180, 790);
        layoutTree(content);
        BufferedImage image = new BufferedImage(
                content.getWidth(), content.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        try {
            content.printAll(graphics);
        }
        finally {
            graphics.dispose();
        }

        Path output = Path.of("build", "reports", "demo", "showcase-" + name + ".png");
        Files.createDirectories(output.getParent());
        ImageIO.write(image, "png", output.toFile());
    }

    private static void layoutTree(Container container) {
        container.doLayout();
        for (Component child : container.getComponents()) {
            if (child instanceof Container) {
                layoutTree((Container) child);
            }
        }
    }

    private static void lockHeight(JComponent component) {
        Dimension preferred = component.getPreferredSize();
        component.setMaximumSize(new Dimension(Integer.MAX_VALUE, preferred.height));
        component.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private static final class CardPanel extends JPanel {
        private CardPanel() {
            setOpaque(false);
            setBorder(new EmptyBorder(22, 22, 22, 22));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                Color background = UIManager.getColor("TextField.background");
                g2.setColor(background != null ? background : getBackground());
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 22, 22);
                Color border = UIManager.getColor("Component.borderColor");
                g2.setColor(border != null ? border : Color.GRAY);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 22, 22);
            }
            finally {
                g2.dispose();
            }
            super.paintComponent(graphics);
        }
    }

    private static final class CardBorder extends AbstractBorder {
        private final int arc;

        private CardBorder(int arc) {
            this.arc = arc;
        }

        @Override
        public void paintBorder(Component component, Graphics graphics, int x, int y,
                                int width, int height) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                Color border = UIManager.getColor("Component.borderColor");
                g2.setColor(border != null ? border : Color.GRAY);
                g2.drawRoundRect(x, y, width - 1, height - 1, arc, arc);
            }
            finally {
                g2.dispose();
            }
        }
    }

    private static final class SerializableToggleAction extends AbstractAction implements Serializable {
        private static final long serialVersionUID = 1L;

        private SerializableToggleAction() {
            super("Saved toggle split");
        }

        @Override
        public void actionPerformed(ActionEvent event) {
        }
    }

    private static final class AccentIcon implements Icon {
        private static final int PLAY = 0;
        private static final int PIN = 1;
        private static final int MENU = 2;
        private static final int WINDOW = 3;

        private final Color color;
        private final int shape;

        private AccentIcon(Color color, int shape) {
            this.color = color;
            this.shape = shape;
        }

        @Override
        public void paintIcon(Component component, Graphics graphics, int x, int y) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            try {
                g2.translate(x, y);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND,
                        BasicStroke.JOIN_ROUND));
                if (shape == PLAY) {
                    g2.fillPolygon(new int[]{4, 13, 4}, new int[]{3, 8, 13}, 3);
                }
                else if (shape == PIN) {
                    g2.drawLine(5, 3, 11, 9);
                    g2.drawLine(10, 2, 13, 5);
                    g2.drawLine(3, 10, 8, 5);
                    g2.drawLine(8, 9, 4, 13);
                }
                else if (shape == MENU) {
                    g2.drawLine(3, 4, 13, 4);
                    g2.drawLine(3, 8, 13, 8);
                    g2.drawLine(3, 12, 13, 12);
                }
                else {
                    g2.drawRoundRect(2, 3, 12, 10, 3, 3);
                    g2.drawLine(2, 6, 14, 6);
                }
            }
            finally {
                g2.dispose();
            }
        }

        @Override
        public int getIconWidth() {
            return 16;
        }

        @Override
        public int getIconHeight() {
            return 16;
        }
    }
}
