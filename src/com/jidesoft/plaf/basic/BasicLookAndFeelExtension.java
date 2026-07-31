/*
 * @(#)BasicLookAndFeelExtension.java 4/15/2007
 *
 * Copyright 2002 - 2007 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.plaf.basic;

import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.swing.JideTabbedPane;
import com.jidesoft.utils.ProductNames;

import javax.swing.*;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.basic.BasicBorders;
import java.awt.*;

/**
 * Initialize the uiClassID to BasicComponentUI mapping for JIDE components. The JComponent classes define their own
 * uiClassID constants (see AbstractComponent.getUIClassID).  This table must map those constants to a BasicComponentUI
 * class of the appropriate type.
 */
public class BasicLookAndFeelExtension implements ProductNames {

    /**
     * Initializes class defaults.
     *
     * @param table UIDefaults table
     */
    public static void initClassDefaults(UIDefaults table) {
        int products = LookAndFeelFactory.getProductsUsed();

        final String basicPackageName = "com.jidesoft.plaf.basic.";

        // common
        table.put("JidePopupMenuUI", basicPackageName + "BasicJidePopupMenuUI");
        table.put("HeaderBoxUI", basicPackageName + "BasicHeaderBoxUI");
        table.put("RangeSliderUI", basicPackageName + "BasicRangeSliderUI");
        table.put("FolderChooserUI", basicPackageName + "BasicFolderChooserUI");
        table.put("StyledLabelUI", basicPackageName + "BasicStyledLabelUI");
        table.put("GripperUI", basicPackageName + "BasicGripperUI");
        table.put("JidePopupUI", basicPackageName + "BasicJidePopupUI");
        table.put("JideTabbedPaneUI", basicPackageName + "BasicJideTabbedPaneUI");
        table.put("JideLabelUI", basicPackageName + "BasicJideLabelUI");
        table.put("JideButtonUI", basicPackageName + "BasicJideButtonUI");
        table.put("JideSplitButtonUI", basicPackageName + "BasicJideSplitButtonUI");
        table.put("JideComboBoxUI", table.get("ComboBoxUI"));
        table.put("MeterProgressBarUI", basicPackageName + "MeterProgressBarUI");

        if ((products & PRODUCT_GRIDS) != 0) {
            // grids
            table.put("JideTableUI", basicPackageName + "BasicJideTableUI");
            table.put("NavigableTableUI", basicPackageName + "BasicNavigableTableUI");
            table.put("CellSpanTableUI", basicPackageName + "BasicCellSpanTableUI");
            table.put("TreeTableUI", basicPackageName + "BasicTreeTableUI");
            table.put("HierarchicalTableUI", basicPackageName + "BasicHierarchicalTableUI");
            table.put("CellStyleTableHeaderUI", basicPackageName + "BasicCellStyleTableHeaderUI");
            table.put("SortableTableHeaderUI", basicPackageName + "BasicSortableTableHeaderUI");
            table.put("NestedTableHeaderUI", basicPackageName + "BasicNestedTableHeaderUI");
            table.put("EditableTableHeaderUI", basicPackageName + "BasicEditableTableHeaderUI");
            table.put("AutoFilterTableHeaderUI", basicPackageName + "BasicAutoFilterTableHeaderUI");
            table.put("GroupTableHeaderUI", basicPackageName + "BasicGroupTableHeaderUI");
            table.put("GroupListUI", basicPackageName + "BasicGroupListUI");
            table.put("ExComboBoxUI", basicPackageName + "BasicExComboBoxUI");
        }

        if ((products & PRODUCT_DOCK) != 0) {
            // dock
            table.put("SidePaneUI", basicPackageName + "BasicSidePaneUI");
            table.put("DockableFrameUI", basicPackageName + "BasicDockableFrameUI");
        }

        if ((products & PRODUCT_COMPONENTS) != 0) {
            // components
            table.put("CollapsiblePaneUI", basicPackageName + "BasicCollapsiblePaneUI");
            table.put("StatusBarSeparatorUI", basicPackageName + "BasicStatusBarSeparatorUI");
        }

        if ((products & PRODUCT_ACTION) != 0) {
            // action
            table.put("CommandBarUI", basicPackageName + "BasicCommandBarUI");
            table.put("CommandBarSeparatorUI", basicPackageName + "BasicCommandBarSeparatorUI");
            table.put("ChevronUI", basicPackageName + "BasicChevronUI");
            table.put("CommandBarTitleBarUI", basicPackageName + "BasicCommandBarTitleBarUI");
        }
        if ((products & PRODUCT_TREEMAP) != 0) {
            // action
            table.put("TreeMapUI", basicPackageName + "BasicTreeMapUI");
        }
    }

    /**
     * Initializes the generic component defaults used by the basic UI delegates.
     *
     * @param table UIDefaults table
     */
    public static void initComponentDefaults(UIDefaults table) {
        Font controlFont = firstNonNull(
                table.getFont("Button.font"),
                table.getFont("Label.font"),
                new FontUIResource(Font.DIALOG, Font.PLAIN, 12));
        Color control = firstNonNull(
                table.getColor("control"),
                table.getColor("Panel.background"),
                new ColorUIResource(238, 238, 238));
        Color controlText = firstNonNull(
                table.getColor("controlText"),
                table.getColor("Label.foreground"),
                new ColorUIResource(Color.BLACK));
        Color highlight = firstNonNull(
                table.getColor("controlHighlight"),
                table.getColor("Button.highlight"),
                new ColorUIResource(Color.WHITE));
        Color lightHighlight = firstNonNull(
                table.getColor("controlLtHighlight"),
                highlight);
        Color shadow = firstNonNull(
                table.getColor("controlShadow"),
                table.getColor("Button.shadow"),
                new ColorUIResource(Color.GRAY));
        Color darkShadow = firstNonNull(
                table.getColor("controlDkShadow"),
                table.getColor("Button.darkShadow"),
                new ColorUIResource(Color.DARK_GRAY));
        Color selection = firstNonNull(
                table.getColor("textHighlight"),
                table.getColor("Button.select"),
                control);

        Object buttonBorder = firstNonNull(
                table.getBorder("Button.border"),
                new BasicBorders.MarginBorder());
        Object gripperPainter = (Painter) (component, graphics, bounds, orientation, state) ->
                BasicPainter.getInstance().paintGripper(component, graphics, bounds, orientation, state);

        table.putDefaults(new Object[]{
                "JideLabel.font", controlFont,
                "JideLabel.background", control,
                "JideLabel.foreground", controlText,

                "JideButton.selectedAndFocusedBackground", selection,
                "JideButton.focusedBackground", selection,
                "JideButton.selectedBackground", selection,
                "JideButton.borderColor", darkShadow,
                "JideButton.font", controlFont,
                "JideButton.background", control,
                "JideButton.foreground", controlText,
                "JideButton.shadow", shadow,
                "JideButton.darkShadow", darkShadow,
                "JideButton.light", highlight,
                "JideButton.highlight", lightHighlight,
                "JideButton.border", buttonBorder,
                "JideButton.margin", new InsetsUIResource(3, 3, 3, 3),
                "JideButton.textIconGap", 4,
                "JideButton.textShiftOffset", 0,
                "JideButton.focusInputMap", new UIDefaults.LazyInputMap(new Object[]{
                        "SPACE", "pressed",
                        "released SPACE", "released"
                }),

                "TristateCheckBox.icon", null,
                "TristateCheckBox.setMixed.clientProperty",
                new Object[]{"JButton.selectedState", "indeterminate"},
                "TristateCheckBox.clearMixed.clientProperty",
                new Object[]{"JButton.selectedState", null},
                "JideScrollPane.border", table.getBorder("ScrollPane.border"),

                "JideSplitPane.dividerSize", 3,
                "JideSplitPaneDivider.border",
                new BorderUIResource(BorderFactory.createEmptyBorder()),
                "JideSplitPaneDivider.background", control,
                "JideSplitPaneDivider.gripperPainter", gripperPainter,

                "JideTabbedPane.defaultTabShape", JideTabbedPane.SHAPE_BOX,
                "JideTabbedPane.defaultResizeMode", JideTabbedPane.RESIZE_MODE_NONE,
                "JideTabbedPane.defaultTabColorTheme", JideTabbedPane.COLOR_THEME_DEFAULT,
                "JideTabbedPane.tabRectPadding", 2,
                "JideTabbedPane.closeButtonMarginHorizonal", 3,
                "JideTabbedPane.closeButtonMarginVertical", 3,
                "JideTabbedPane.textMarginVertical", 4,
                "JideTabbedPane.noIconMargin", 2,
                "JideTabbedPane.iconMargin", 5,
                "JideTabbedPane.textPadding", 6,
                "JideTabbedPane.buttonSize", 18,
                "JideTabbedPane.buttonMargin", 5,
                "JideTabbedPane.fitStyleBoundSize", 8,
                "JideTabbedPane.fitStyleFirstTabMargin", 4,
                "JideTabbedPane.fitStyleIconMinWidth", 24,
                "JideTabbedPane.fitStyleTextMinWidth", 16,
                "JideTabbedPane.compressedStyleNoIconRectSize", 24,
                "JideTabbedPane.compressedStyleIconMargin", 12,
                "JideTabbedPane.compressedStyleCloseButtonMarginHorizontal", 0,
                "JideTabbedPane.compressedStyleCloseButtonMarginVertical", 0,
                "JideTabbedPane.fixedStyleRectSize", 60,
                "JideTabbedPane.closeButtonMargin", 2,
                "JideTabbedPane.gripLeftMargin", 4,
                "JideTabbedPane.closeButtonMarginSize", 6,
                "JideTabbedPane.closeButtonLeftMargin", 2,
                "JideTabbedPane.closeButtonRightMargin", 2,
                "JideTabbedPane.defaultTabBorderShadowColor", darkShadow,
                "JideTabbedPane.gripperPainter", gripperPainter,
                "JideTabbedPane.border",
                new BorderUIResource(BorderFactory.createEmptyBorder()),
                "JideTabbedPane.background", control,
                "JideTabbedPane.foreground", controlText,
                "JideTabbedPane.light", highlight,
                "JideTabbedPane.highlight", lightHighlight,
                "JideTabbedPane.shadow", shadow,
                "JideTabbedPane.darkShadow", darkShadow,
                "JideTabbedPane.tabInsets", new InsetsUIResource(1, 4, 1, 4),
                "JideTabbedPane.contentBorderInsets", new InsetsUIResource(2, 2, 2, 2),
                "JideTabbedPane.ignoreContentBorderInsetsIfNoTabs", Boolean.FALSE,
                "JideTabbedPane.tabAreaInsets", new InsetsUIResource(2, 4, 0, 4),
                "JideTabbedPane.tabAreaBackground", control,
                "JideTabbedPane.tabAreaBackgroundLt", lightHighlight,
                "JideTabbedPane.tabAreaBackgroundDk", control,
                "JideTabbedPane.tabRunOverlay", 2,
                "JideTabbedPane.font", controlFont,
                "JideTabbedPane.selectedTabFont", controlFont,
                "JideTabbedPane.selectedTabTextForeground", controlText,
                "JideTabbedPane.unselectedTabTextForeground", controlText,
                "JideTabbedPane.selectedTabBackground", control,
                "JideTabbedPane.selectedTabBackgroundLt", highlight,
                "JideTabbedPane.selectedTabBackgroundDk", shadow,
                "JideTabbedPane.tabListBackground", table.getColor("List.background"),
                "JideTabbedPane.textIconGap", 4,
                "JideTabbedPane.showIconOnTab", Boolean.TRUE,
                "JideTabbedPane.showCloseButtonOnTab", Boolean.FALSE,
                "JideTabbedPane.closeButtonAlignment", SwingConstants.TRAILING,
                "JideTabbedPane.focusInputMap",
                new UIDefaults.LazyInputMap(new Object[]{
                        "RIGHT", "navigateRight",
                        "KP_RIGHT", "navigateRight",
                        "LEFT", "navigateLeft",
                        "KP_LEFT", "navigateLeft",
                        "UP", "navigateUp",
                        "KP_UP", "navigateUp",
                        "DOWN", "navigateDown",
                        "KP_DOWN", "navigateDown",
                        "ctrl DOWN", "requestFocusForVisibleComponent",
                        "ctrl KP_DOWN", "requestFocusForVisibleComponent"
                }),
                "JideTabbedPane.ancestorInputMap",
                new UIDefaults.LazyInputMap(new Object[]{
                        "ctrl PAGE_DOWN", "navigatePageDown",
                        "ctrl PAGE_UP", "navigatePageUp",
                        "ctrl UP", "requestFocus",
                        "ctrl KP_UP", "requestFocus"
                }),

                "Resizable.resizeBorder",
                new BorderUIResource(BorderFactory.createLineBorder(shadow)),
                "Gripper.size", 8,
                "Gripper.foreground", control,
                "Gripper.painter", gripperPainter,
                "Icon.floating", Boolean.FALSE,

                "JideSplitButton.font", controlFont,
                "JideSplitButton.margin", new InsetsUIResource(3, 3, 3, 7),
                "JideSplitButton.border", buttonBorder,
                "JideSplitButton.borderPainted", Boolean.FALSE,
                "JideSplitButton.textIconGap", 4,
                "JideSplitButton.selectionBackground",
                firstNonNull(table.getColor("MenuItem.selectionBackground"), selection),
                "JideSplitButton.focusInputMap", new UIDefaults.LazyInputMap(new Object[]{
                        "SPACE", "pressed",
                        "released SPACE", "released",
                        "DOWN", "downPressed",
                        "released DOWN", "downReleased"
                }),

                "ButtonPanel.order", "ACO",
                "ButtonPanel.oppositeOrder", "H",
                "ButtonPanel.buttonGap", 5,
                "ButtonPanel.groupGap", 5,
                "ButtonPanel.minButtonWidth", 57,

                "MeterProgressBar.border",
                new BorderUIResource(BorderFactory.createLineBorder(Color.BLACK)),
                "MeterProgressBar.background", new ColorUIResource(Color.BLACK),
                "MeterProgressBar.foreground", new ColorUIResource(Color.GREEN),
                "MeterProgressBar.cellForeground", new ColorUIResource(Color.GREEN),
                "MeterProgressBar.cellBackground", new ColorUIResource(0, 128, 0),
                "MeterProgressBar.cellLength", 2,
                "MeterProgressBar.cellSpacing", 2,

                "HeaderBox.background", control
        });
    }

    @SafeVarargs
    private static <T> T firstNonNull(T... values) {
        for (T value : values) {
            if (value != null) {
                return value;
            }
        }
        throw new IllegalArgumentException("At least one value must be non-null");
    }
}
