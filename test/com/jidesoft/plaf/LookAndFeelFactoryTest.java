package com.jidesoft.plaf;

import com.jidesoft.testing.FlatLafTest;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.jideoss.FlatJideOssDefaultsAddon;
import org.junit.jupiter.api.Test;

import javax.swing.LookAndFeel;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

@FlatLafTest
class LookAndFeelFactoryTest {
    private static List<String> hookOrder;

    @Test
    void acceptsFlatLafJideOssAddonRegistrations() {
        assertDoesNotThrow(() ->
                new FlatJideOssDefaultsAddon().getDefaults(FlatLightLaf.class));
    }

    @Test
    void installsFlatLafCompatibleDefaults() {
        UIDefaults defaults = new UIDefaults();
        LookAndFeel lookAndFeel = UIManager.getLookAndFeel();
        Object customValue = new Object();
        LookAndFeelFactory.UIDefaultsInitializer initializer =
                table -> table.put("test.initialized", customValue);
        LookAndFeelFactory.UIDefaultsCustomizer customizer =
                table -> table.put("test.customized", customValue);

        LookAndFeelFactory.addUIDefaultsInitializer(initializer);
        LookAndFeelFactory.addUIDefaultsCustomizer(customizer);
        try {
            LookAndFeelFactory.installJideExtension(defaults, lookAndFeel);
        }
        finally {
            LookAndFeelFactory.removeUIDefaultsInitializer(initializer);
            LookAndFeelFactory.removeUIDefaultsCustomizer(customizer);
        }

        assertEquals(Boolean.TRUE, defaults.get(LookAndFeelFactory.JIDE_EXTENSION_INSTALLED));
        assertEquals("com.jidesoft.plaf.basic.BasicJideButtonUI", defaults.get("JideButtonUI"));
        assertSame(customValue, defaults.get("test.initialized"));
        assertSame(customValue, defaults.get("test.customized"));
        assertNull(defaults.get("TristateCheckBox.icon"));
        assertArrayEquals(
                new Object[]{"JButton.selectedState", "indeterminate"},
                (Object[]) defaults.get("TristateCheckBox.setMixed.clientProperty"));
        assertArrayEquals(
                new Object[]{"JButton.selectedState", null},
                (Object[]) defaults.get("TristateCheckBox.clearMixed.clientProperty"));
    }

    @Test
    void executesRegisteredFlatLafSuperclassHooksInOrder() {
        UIDefaults defaults = new UIDefaults();
        LookAndFeel lookAndFeel = new ActiveFlatLaf();
        hookOrder = new ArrayList<>();
        LookAndFeelFactory.UIDefaultsInitializer initializer = table -> {
            hookOrder.add("explicit initializer");
            table.put("test.phase", "explicit initializer");
        };
        LookAndFeelFactory.UIDefaultsCustomizer customizer = table -> {
            assertEquals("registered customizer", table.get("test.phase"));
            hookOrder.add("explicit customizer");
        };

        LookAndFeelFactory.registerDefaultInitializer(
                RegisteredFlatLaf.class.getName(), RegisteredHooks.class.getName());
        LookAndFeelFactory.registerDefaultCustomizer(
                RegisteredFlatLaf.class.getName(), RegisteredHooks.class.getName());
        LookAndFeelFactory.addUIDefaultsInitializer(initializer);
        LookAndFeelFactory.addUIDefaultsCustomizer(customizer);
        try {
            LookAndFeelFactory.installJideExtension(defaults, lookAndFeel);
        }
        finally {
            LookAndFeelFactory.removeUIDefaultsInitializer(initializer);
            LookAndFeelFactory.removeUIDefaultsCustomizer(customizer);
        }

        assertEquals(List.of(
                "explicit initializer",
                "registered initializer",
                "registered customizer",
                "explicit customizer"), hookOrder);
    }

    public static class RegisteredHooks
            implements LookAndFeelFactory.UIDefaultsInitializer, LookAndFeelFactory.UIDefaultsCustomizer {
        @Override
        public void initialize(UIDefaults defaults) {
            assertEquals("explicit initializer", defaults.get("test.phase"));
            assertNull(defaults.get("JideButtonUI"));
            hookOrder.add("registered initializer");
            defaults.put("test.phase", "registered initializer");
        }

        @Override
        public void customize(UIDefaults defaults) {
            assertEquals("com.jidesoft.plaf.basic.BasicJideButtonUI", defaults.get("JideButtonUI"));
            assertEquals("registered initializer", defaults.get("test.phase"));
            hookOrder.add("registered customizer");
            defaults.put("test.phase", "registered customizer");
        }
    }

    public static class RegisteredFlatLaf extends FlatLightLaf {
    }

    private static class ActiveFlatLaf extends RegisteredFlatLaf {
    }
}
