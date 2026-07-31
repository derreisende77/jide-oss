/*
 * @(#)SystemInfo.java
 *
 * Copyright 2002 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.utils;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.Locale;


/**
 * A utility class can detect OS system information.
 */
final public class SystemInfo {

    /**
     * Variable for whether or not we're on Windows.
     */
    private static boolean _isWindows = false;

    /**
     * Variable for whether or not we're on MacOSX.
     */
    private static boolean _isMacOSX = false;

    /**
     * Initialize the settings statically.
     */
    static {
        // get the operating system
        String os = System.getProperty("os.name", "");

        // set the operating system variables
        _isWindows = os.indexOf("Windows") != -1;
        if (os.startsWith("Mac OS")) {
            _isMacOSX = true;
        }
    }

    /**
     * Make sure the constructor can never be called.
     */
    private SystemInfo() {
    }

    /**
     * Returns the version of java we're using.
     *
     * @return the java version.
     */
    public static String getJavaVersion() {
        return System.getProperty("java.version", "1.4.2");
    }

    /**
     * Returns the vendor for java we're using.
     *
     * @return the java vendor.
     */
    public static String getJavaVendor() {
        return System.getProperty("java.vendor", "");
    }

    /**
     * Returns the version of the java class we're using.
     *
     * @return the java class version.
     */
    public static String getJavaClassVersion() {
        return System.getProperty("java.class.version", "");
    }

    /**
     * Returns the operating system.
     *
     * @return the os name.
     */
    public static String getOS() {
        return System.getProperty("os.name", "Unknown");
    }

    /**
     * Returns the operating system version.
     *
     * @return the os version.
     */
    public static String getOSVersion() {
        return System.getProperty("os.version", "");
    }

    /**
     * Returns the operating system architecture.
     *
     * @return the os architecture.
     */
    public static String getOSArchitecture() {
        return System.getProperty("os.arch", "");
    }

    /**
     * Returns the user's home directory.
     *
     * @return the user home .
     */
    public static String getCurrentDirectory() {
        return System.getProperty("user.dir", "");
    }

    /**
     * Returns whether or not the os is some version of Windows.
     *
     * @return <tt>true</tt> if the application is running on some Windows version, <tt>false</tt> otherwise.
     */
    public static boolean isWindows() {
        return _isWindows;
    }

    /**
     * Returns whether or not the os is Mac OSX.
     *
     * @return <tt>true</tt> if the application is running on Mac OSX, <tt>false</tt> otherwise.
     */
    public static boolean isMacOSX() {
        return _isMacOSX;
    }

    /**
     * Returns whether the default locale is one of the three language - Chinese, Japanese or Korean - also known as
     * CJK.
     *
     * @return true if the default locale is in CJK.
     */
    public static boolean isCJKLocale() {
        return isCJKLocale(Locale.getDefault());
    }

    /**
     * Returns whether the locale is one of the three language - Chinese, Japanese or Korean - also known as CJK.
     *
     * @param locale the locale to be checked.
     * @return true if the default locale is in CJK.
     */
    public static boolean isCJKLocale(Locale locale) {
        return locale.equals(Locale.CHINA)
                || locale.equals(Locale.CHINESE)
                || locale.equals(new Locale("zh", "HK"))
                || locale.equals(Locale.TAIWAN)
                || locale.equals(Locale.JAPAN)
                || locale.equals(Locale.JAPANESE)
                || locale.equals(Locale.KOREA)
                || locale.equals(Locale.KOREAN);
    }

    public static int getDisplayScale() {
        if (GraphicsEnvironment.isHeadless()) {
            return 1;
        }

        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = graphicsEnvironment.getDefaultScreenDevice();
        try {
            Field field = device.getClass().getDeclaredField("scale");
            if (field != null) {
                field.setAccessible(true);
                Object scale = field.get(device);
                if (scale instanceof Integer integerScale) {
                    return integerScale;
                }
            }
        }
        catch (Exception ignore) {
        }
        return 1;
    }

    /**
     * Gets the state of the hide mnemonic flag. This only has meaning
     * if this feature is supported by the underlying OS.
     *
     * @return true if mnemonics are hidden, otherwise, false
     * @since 3.7.2
     */
    public static boolean isMnemonicHidden() {
        boolean isMnemonicHidden = true;
        if (UIManager.getBoolean("Button.showMnemonics")) {
            // Do not hide mnemonics if the UI defaults do not support this
            isMnemonicHidden = false;
        }
        return isMnemonicHidden;
    }

}
