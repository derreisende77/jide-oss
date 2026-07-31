/*
 * @(#)LookAndFeelFactory.java 5/28/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.plaf;

import com.jidesoft.plaf.basic.BasicLookAndFeelExtension;
import com.jidesoft.plaf.basic.BasicPainter;
import com.jidesoft.utils.ProductNames;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Installs the additional component UIs and defaults required by JIDE components.
 * <p/>
 * Set up FlatLaf first, then call {@link #installJideExtension()}:
 * <code><pre>
 * FlatLightLaf.setup();
 * LookAndFeelFactory.installJideExtension();
 * </pre></code>
 * The initializer and customizer hooks allow applications to adjust FlatLaf defaults before and after the JIDE
 * defaults are installed.
 * <p/>
 * {@link #installJideExtension()} only adds the additional UIDefaults to the current class loader. If you have
 * several class loaders in your system, you probably should tell the UIManager to use the class loader that called
 * {@code installJideExtension}.
 * <code><pre>
 * UIManager.put("ClassLoader", currentClass.getClassLoader());
 * LookAndFeelFactory.installJideExtension();
 * </pre></code>
 */
public class LookAndFeelFactory implements ProductNames {

    private static LookAndFeel _lookAndFeel;

    /**
     * If installJideExtension is called, it will put an entry on UIDefaults table.
     * UIManagerLookup.getBoolean(JIDE_EXTENSION_INSTALLLED) will return true. You can also use {@link
     * #isJideExtensionInstalled()} to check the value instead of using UIManagerLookup.getBoolean(JIDE_EXTENSION_INSTALLLED).
     */
    public static final String JIDE_EXTENSION_INSTALLED = "jidesoft.extensionInstalled";

    /**
     * An interface to make the customization of UIDefaults easier. This customizer will be called after
     * installJideExtension() is called. So if you want to further customize UIDefault, you can use this customizer to
     * do it.
     */
    public interface UIDefaultsCustomizer {
        void customize(UIDefaults defaults);
    }

    /**
     * An interface to make the initialization of UIDefaults easier. This initializer will be called before
     * installJideExtension() is called. So if you want to initialize UIDefault before installJideExtension is called,
     * you can use this initializer to do it.
     */
    public interface UIDefaultsInitializer {
        void initialize(UIDefaults defaults);
    }

    private static final List<UIDefaultsCustomizer> _uiDefaultsCustomizers = new Vector<>();
    private static final List<UIDefaultsInitializer> _uiDefaultsInitializers = new Vector<>();
    private static final Map<String, String> _defaultInitializers = new ConcurrentHashMap<>();
    private static final Map<String, String> _defaultCustomizers = new ConcurrentHashMap<>();

    protected LookAndFeelFactory() {
    }

    /**
     * Adds the JIDE component defaults to the active FlatLaf defaults.
     * Call this method again after changing the active FlatLaf theme.
     */
    public static void installJideExtension() {
        installJideExtension(UIManager.getLookAndFeelDefaults(), UIManager.getLookAndFeel());
    }

    /**
     * Checks if JIDE extension is installed. Please note, UIManager.setLookAndFeel() method will overwrite the whole
     * UIDefaults table. So even you called {@link #installJideExtension()} method before, UIManager.setLookAndFeel()
     * method make isJideExtensionInstalled returning false.
     *
     * @return true if installed.
     */
    public static boolean isJideExtensionInstalled() {
        return UIDefaultsLookup.getBoolean(JIDE_EXTENSION_INSTALLED);
    }

    /**
     * Installs the UIDefault needed by JIDE component to the uiDefaults table passed in.
     *
     * @param uiDefaults the UIDefault tables where JIDE UIDefaults will be installed.
     * @param lnf        the active FlatLaf instance
     */
    public static void installJideExtension(UIDefaults uiDefaults, LookAndFeel lnf) {
        if (Boolean.TRUE.equals(uiDefaults.get(JIDE_EXTENSION_INSTALLED)) && _lookAndFeel == lnf) {
            return;
        }

        _lookAndFeel = lnf;
        UIDefaultsInitializer[] initializers = getUIDefaultsInitializers();
        for (UIDefaultsInitializer initializer : initializers) {
            if (initializer != null) {
                initializer.initialize(uiDefaults);
            }
        }
        initializeRegisteredDefaults(lnf, uiDefaults);

        BasicLookAndFeelExtension.initClassDefaults(uiDefaults);
        BasicLookAndFeelExtension.initComponentDefaults(uiDefaults);

        UIDefaultsLookup.put(uiDefaults, "Theme.painter", BasicPainter.getInstance());

        uiDefaults.put(JIDE_EXTENSION_INSTALLED, Boolean.TRUE);

        customizeRegisteredDefaults(lnf, uiDefaults);
        UIDefaultsCustomizer[] customizers = getUIDefaultsCustomizers();
        for (UIDefaultsCustomizer customizer : customizers) {
            if (customizer != null) {
                customizer.customize(uiDefaults);
            }
        }
    }

    /**
     * Registers a UIDefaults initializer for a FlatLaf class and its subclasses.
     *
     * @param lnfClassName         full class name of the FlatLaf class
     * @param initializerClassName full class name of the UIDefaults initializer
     */
    public static void registerDefaultInitializer(String lnfClassName, String initializerClassName) {
        _defaultInitializers.put(lnfClassName, initializerClassName);
    }

    /**
     * Registers a UIDefaults customizer for a FlatLaf class and its subclasses.
     *
     * @param lnfClassName        full class name of the FlatLaf class
     * @param customizerClassName full class name of the UIDefaults customizer
     */
    public static void registerDefaultCustomizer(String lnfClassName, String customizerClassName) {
        _defaultCustomizers.put(lnfClassName, customizerClassName);
    }

    private static void initializeRegisteredDefaults(LookAndFeel lnf, UIDefaults uiDefaults) {
        for (Class<?> lnfClass : getLookAndFeelHierarchy(lnf)) {
            String initializerClassName = _defaultInitializers.get(lnfClass.getName());
            if (initializerClassName != null) {
                instantiate(initializerClassName, UIDefaultsInitializer.class).initialize(uiDefaults);
            }
        }
    }

    private static void customizeRegisteredDefaults(LookAndFeel lnf, UIDefaults uiDefaults) {
        for (Class<?> lnfClass : getLookAndFeelHierarchy(lnf)) {
            String customizerClassName = _defaultCustomizers.get(lnfClass.getName());
            if (customizerClassName != null) {
                instantiate(customizerClassName, UIDefaultsCustomizer.class).customize(uiDefaults);
            }
        }
    }

    private static List<Class<?>> getLookAndFeelHierarchy(LookAndFeel lnf) {
        List<Class<?>> hierarchy = new ArrayList<>();
        for (Class<?> lnfClass = lnf.getClass();
             lnfClass != null && LookAndFeel.class.isAssignableFrom(lnfClass);
             lnfClass = lnfClass.getSuperclass()) {
            hierarchy.add(lnfClass);
        }
        Collections.reverse(hierarchy);
        return hierarchy;
    }

    private static <T> T instantiate(String className, Class<T> type) {
        try {
            Class<?> implementation = Class.forName(className, true, getUIManagerClassLoader());
            return type.cast(implementation.getDeclaredConstructor().newInstance());
        }
        catch (ReflectiveOperationException | ClassCastException e) {
            throw new IllegalStateException("Unable to instantiate registered UIDefaults hook " + className, e);
        }
    }

    public static ClassLoader getUIManagerClassLoader() {
        Object cl = UIManager.get("ClassLoader");
        if (cl instanceof ClassLoader classLoader) {
            return classLoader;
        }
        ClassLoader classLoader = LookAndFeelFactory.class.getClassLoader();
        if (classLoader == null) {
            classLoader = ClassLoader.getSystemClassLoader();
        }
        return classLoader;
    }

    /**
     * Gets the current look and feel.
     *
     * @return the current look and feel
     */
    public static LookAndFeel getLookAndFeel() {
        return _lookAndFeel;
    }

    /**
     * Gets all UIDefaults customizers.
     *
     * @return an array of UIDefaults customizers.
     */
    public static UIDefaultsCustomizer[] getUIDefaultsCustomizers() {
        return _uiDefaultsCustomizers.toArray(new UIDefaultsCustomizer[_uiDefaultsCustomizers.size()]);
    }

    /**
     * Adds your own UIDefaults customizer. You need to add it before installJideExtension() is called but the actual
     * customize() code will be called after installJideExtension() is called.
     * <code><pre>
     * For example, we use "JideButton.font" as the UIDefault for the JideButton font. If you want
     * to use another font, you can do
     * LookAndFeelFactory.addUIDefaultsCustomizer(new LookAndFeelFactory.UIDefaultsCustomizer() {
     *     public void customize(UIDefaults defaults) {
     *         defaults.put("JideButton.font", whateverFont);
     *     }
     * });
     * LookAndFeelFactory.installJideExtension();
     * </pre></code>
     *
     * @param uiDefaultsCustomizer the UIDefaultsCustomizer
     */
    public static void addUIDefaultsCustomizer(UIDefaultsCustomizer uiDefaultsCustomizer) {
        if (!_uiDefaultsCustomizers.contains(uiDefaultsCustomizer)) {
            _uiDefaultsCustomizers.add(uiDefaultsCustomizer);
        }
    }

    /**
     * Removes an existing UIDefaults customizer you added before.
     *
     * @param uiDefaultsCustomizer the UIDefaultsCustomizer
     */
    public static void removeUIDefaultsCustomizer(UIDefaultsCustomizer uiDefaultsCustomizer) {
        _uiDefaultsCustomizers.remove(uiDefaultsCustomizer);
    }

    /**
     * Gets all UIDefaults initializers.
     *
     * @return an array of UIDefaults initializers.
     */
    public static UIDefaultsInitializer[] getUIDefaultsInitializers() {
        return _uiDefaultsInitializers.toArray(new UIDefaultsInitializer[_uiDefaultsInitializers.size()]);
    }

    /**
     * Adds your own UIDefaults initializer. This initializer will be called before installJideExtension() is called.
     * <p/>
     * Here is how you use it. For example, we use the color of UIDefault "activeCaption" to get the active title color
     * which we will use for active title bar color in JIDE components. If FlatLaf doesn't set this
     * UIDefault, we might throw NPE later in the code. To avoid this, you call
     * <code><pre>
     * LookAndFeelFactory.addUIDefaultsInitializer(new LookAndFeelFactory.UIDefaultsInitializer() {
     *     public void initialize(UIDefaults defaults) {
     *         defaults.put("activeCaption", whateverColor);
     *     }
     * });
     * UIManager.setLookAndFeel(...); // set whatever look and feel
     * LookAndFeelFactory.installJideExtension(); // install the UIDefaults needed by the JIDE
     * components
     * </pre></code>
     *
     * @param uiDefaultsInitializer the UIDefaultsInitializer.
     */
    public static void addUIDefaultsInitializer(UIDefaultsInitializer uiDefaultsInitializer) {
        if (!_uiDefaultsInitializers.contains(uiDefaultsInitializer)) {
            _uiDefaultsInitializers.add(uiDefaultsInitializer);
        }
    }

    /**
     * Removes an existing UIDefaults initializer you added before.
     *
     * @param uiDefaultsInitializer the UIDefaultsInitializer
     */
    public static void removeUIDefaultsInitializer(UIDefaultsInitializer uiDefaultsInitializer) {
        _uiDefaultsInitializers.remove(uiDefaultsInitializer);
    }

    @SuppressWarnings({"UseOfSystemOutOrSystemErr"})
    public static void verifyDefaults(UIDefaults table, Object[] keyValueList) {
        for (int i = 0, max = keyValueList.length; i < max; i += 2) {
            Object value = keyValueList[i + 1];
            if (value == null) {
                System.out.println("The value for " + keyValueList[i] + " is null");
            }
            else {
                Object oldValue = table.get(keyValueList[i]);
                if (oldValue != null) {
                    System.out.println("The value for " + keyValueList[i] + " exists which is " + oldValue);
                }
            }
        }
    }

    /**
     * Puts a list of UIDefault to the UIDefaults table. The keyValueList is an array with a key and value in pair. If
     * the value is null, this method will remove the key from the table. If the table already has a value for the key,
     * the new value will be ignored. This is the difference from {@link #putDefaults(javax.swing.UIDefaults, Object[])}
     * method. You should use this method in {@link UIDefaultsInitializer} so that it fills in the UIDefault value only
     * when it is missing.
     *
     * @param table         the ui defaults table
     * @param keyValueArray the key value array. It is in the format of a key followed by a value.
     */
    public static void putDefaults(UIDefaults table, Object[] keyValueArray) {
        for (int i = 0, max = keyValueArray.length; i < max; i += 2) {
            Object value = keyValueArray[i + 1];
            if (value == null) {
                table.remove(keyValueArray[i]);
            }
            else {
                if (table.get(keyValueArray[i]) == null) {
                    table.put(keyValueArray[i], value);
                }
            }
        }
    }

    /**
     * Puts a list of UIDefault to the UIDefaults table. The keyValueList is an array with a key and value in pair. If
     * the value is null, this method will remove the key from the table. Otherwise, it will put the new value in even
     * if the table already has a value for the key. This is the difference from {@link
     * #putDefaults(javax.swing.UIDefaults, Object[])} method. You should use this method in {@link
     * UIDefaultsCustomizer} because you always want to override the existing value using the new value.
     *
     * @param table         the ui defaults table
     * @param keyValueArray the key value array. It is in the format of a key followed by a value.
     */
    public static void overwriteDefaults(UIDefaults table, Object[] keyValueArray) {
        for (int i = 0, max = keyValueArray.length; i < max; i += 2) {
            Object value = keyValueArray[i + 1];
            if (value == null) {
                table.remove(keyValueArray[i]);
            }
            else {
                table.put(keyValueArray[i], value);
            }
        }
    }

    private static int _productsUsed = -1;

    public static int getProductsUsed() {
        if (_productsUsed == -1) {
            _productsUsed = 0;
            try {
                Class.forName("com.jidesoft.docking.Product");
                _productsUsed |= PRODUCT_DOCK;
            }
            catch (Throwable e) {
                //
            }
            try {
                Class.forName("com.jidesoft.action.Product");
                _productsUsed |= PRODUCT_ACTION;
            }
            catch (Throwable e) {
                //
            }
            try {
                Class.forName("com.jidesoft.document.Product");
                _productsUsed |= PRODUCT_COMPONENTS;
            }
            catch (Throwable e) {
                //
            }
            try {
                Class.forName("com.jidesoft.grid.Product");
                _productsUsed |= PRODUCT_GRIDS;
            }
            catch (Throwable e) {
                //
            }
            try {
                Class.forName("com.jidesoft.wizard.Product");
                _productsUsed |= PRODUCT_DIALOGS;
            }
            catch (Throwable e) {
                //
            }
            try {
                Class.forName("com.jidesoft.pivot.Product");
                _productsUsed |= PRODUCT_PIVOT;
            }
            catch (Throwable e) {
                //
            }
            try {
                Class.forName("com.jidesoft.shortcut.Product");
                _productsUsed |= PRODUCT_SHORTCUT;
            }
            catch (Throwable e) {
                //
            }
            try {
                Class.forName("com.jidesoft.editor.Product");
                _productsUsed |= PRODUCT_CODE_EDITOR;
            }
            catch (Throwable e) {
                //
            }
            try {
                Class.forName("com.jidesoft.rss.Product");
                _productsUsed |= PRODUCT_FEEDREADER;
            }
            catch (Throwable e) {
                //
            }
            try {
                Class.forName("com.jidesoft.treemap.Product");
                _productsUsed |= PRODUCT_TREEMAP;
            }
            catch (Throwable e) {
                //
            }
            try {
                Class.forName("com.jidesoft.chart.Product");
                _productsUsed |= PRODUCT_CHARTS;
            }
            catch (Throwable e) {
                //
            }
            try {
                Class.forName("com.jidesoft.diff.Product");
                _productsUsed |= PRODUCT_DIFF;
            }
            catch (Throwable e) {
                //
            }
        }
        return _productsUsed;
    }

    /**
     * Sets the products you will use. This is needed so that LookAndFeelFactory knows what UIDefault to initialize. For
     * example, if you use only JIDE Docking Framework and JIDE Grids, you should call
     * <code>setProductUsed(ProductNames.PRODUCT_DOCK | ProductNames.PRODUCT_GRIDS)</code> so that we don't initialize
     * UIDefaults needed by any other products. If you use this class as part of JIDE Common Layer open source project,
     * you should call <code>setProductUsed(ProductNames.PRODUCT_COMMON)</code>. If you want to use all JIDE products,
     * you should call <code>setProductUsed(ProductNames.PRODUCT_ALL)</code>
     *
     * @param productsUsed a bit-wise OR of product values defined in {@link com.jidesoft.utils.ProductNames}.
     */
    public static void setProductsUsed(int productsUsed) {
        _productsUsed = productsUsed;
    }

    public static boolean isMnemonicHidden() {
        return !UIManager.getBoolean("Button.showMnemonics");
    }
}
