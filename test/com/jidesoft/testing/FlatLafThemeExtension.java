package com.jidesoft.testing;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.jidesoft.plaf.LookAndFeelFactory;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.lang.reflect.InvocationTargetException;

final class FlatLafThemeExtension implements BeforeEachCallback, AfterEachCallback {
    private static final ExtensionContext.Namespace NAMESPACE =
            ExtensionContext.Namespace.create(FlatLafThemeExtension.class);
    private static final String PREVIOUS_LOOK_AND_FEEL = "previousLookAndFeel";

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        LookAndFeel previous = callOnEventDispatchThread(UIManager::getLookAndFeel);
        context.getStore(NAMESPACE).put(PREVIOUS_LOOK_AND_FEEL, previous);

        String theme = System.getProperty(FlatLafTest.THEME_PROPERTY, "light");
        runOnEventDispatchThread(() -> {
            boolean installed = switch (theme) {
                case "light" -> FlatLightLaf.setup();
                case "dark" -> FlatDarkLaf.setup();
                default -> throw new IllegalArgumentException("Unsupported FlatLaf test theme: " + theme);
            };
            if (!installed) {
                throw new IllegalStateException("Could not install FlatLaf " + theme + " theme");
            }
            LookAndFeelFactory.installJideExtension();
        });
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        LookAndFeel previous = context.getStore(NAMESPACE)
                .remove(PREVIOUS_LOOK_AND_FEEL, LookAndFeel.class);
        if (previous != null) {
            runOnEventDispatchThread(() -> {
                UIManager.setLookAndFeel(previous);
                LookAndFeelFactory.installJideExtension();
            });
        }
    }

    private static void runOnEventDispatchThread(ThrowingRunnable action) throws Exception {
        callOnEventDispatchThread(() -> {
            action.run();
            return null;
        });
    }

    private static <T> T callOnEventDispatchThread(ThrowingSupplier<T> action) throws Exception {
        if (SwingUtilities.isEventDispatchThread()) {
            return action.get();
        }

        Result<T> result = new Result<>();
        try {
            SwingUtilities.invokeAndWait(() -> {
                try {
                    result.value = action.get();
                }
                catch (Throwable throwable) {
                    result.failure = throwable;
                }
            });
        }
        catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw exception;
        }
        catch (InvocationTargetException exception) {
            throw new IllegalStateException("FlatLaf setup failed on the event dispatch thread",
                    exception.getCause());
        }

        if (result.failure instanceof Exception exception) {
            throw exception;
        }
        if (result.failure instanceof Error error) {
            throw error;
        }
        return result.value;
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Exception;
    }

    @FunctionalInterface
    private interface ThrowingSupplier<T> {
        T get() throws Exception;
    }

    private static final class Result<T> {
        private T value;
        private Throwable failure;
    }
}
