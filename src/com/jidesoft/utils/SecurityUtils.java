/*
 * @(#)SecurityUtils.java
 *
 * Copyright 2002 - 2004 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.utils;

/**
 * Configures optional desktop features.
 */
public class SecurityUtils {
    private static boolean _AWTEventListenerDisabled = false;

    /**
     * Checks if AWTEventListener is disabled. This flag can be set by user. If false, JIDE code will read the value and
     * not use AWTEventListener.
     *
     * @return true if AWTEventListener is disabled.
     */
    public static boolean isAWTEventListenerDisabled() {
        return _AWTEventListenerDisabled;
    }

    /**
     * Enables or disables the usage of AWTEventListener. If you want to change it, you should change the value at the
     * beginning of your main method.
     *
     * @param AWTEventListenerDisabled true or false.
     */
    public static void setAWTEventListenerDisabled(boolean AWTEventListenerDisabled) {
        _AWTEventListenerDisabled = AWTEventListenerDisabled;
    }

}
