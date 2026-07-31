package com.jidesoft.testing;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks Swing tests that must run under every supported FlatLaf theme.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Tag("flatlaf")
@ExtendWith(FlatLafThemeExtension.class)
public @interface FlatLafTest {
    String THEME_PROPERTY = "jide.test.flatlaf.theme";
}
