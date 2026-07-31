package com.jidesoft.utils;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CacheMapTest {
    private static final String DEFAULT_CONTEXT = "default";
    private static final String REQUESTED_CONTEXT = "requested";

    @Test
    void resolvesRequestedContextAcrossClassHierarchyBeforeDefaultContext() {
        CacheMap<String, String> cache = new CacheMap<>(DEFAULT_CONTEXT);
        cache.register(Child.class, "child-default", DEFAULT_CONTEXT);
        cache.register(Parent.class, "parent-requested", REQUESTED_CONTEXT);

        assertEquals("parent-requested", cache.getRegisteredObject(Child.class, REQUESTED_CONTEXT));
        assertEquals("child-default", cache.getRegisteredObject(Child.class, "missing"));
    }

    @Test
    void preservesInterfaceAndSuperclassLookupOrder() {
        CacheMap<String, String> cache = new CacheMap<>(DEFAULT_CONTEXT);
        cache.register(RootInterface.class, "root", REQUESTED_CONTEXT);
        cache.register(Parent.class, "parent", REQUESTED_CONTEXT);
        cache.register(ChildInterface.class, "interface", REQUESTED_CONTEXT);

        assertEquals("interface", cache.getRegisteredObject(Child.class, REQUESTED_CONTEXT));

        cache.unregister(ChildInterface.class, REQUESTED_CONTEXT);
        assertEquals("parent", cache.getRegisteredObject(Child.class, REQUESTED_CONTEXT));

        cache.unregister(Parent.class, REQUESTED_CONTEXT);
        assertEquals("root", cache.getRegisteredObject(Child.class, REQUESTED_CONTEXT));
    }

    @Test
    void cachedHierarchyStillObservesRegistrationChanges() {
        CacheMap<String, String> cache = new CacheMap<>(DEFAULT_CONTEXT);

        assertNull(cache.getRegisteredObject(Child.class, REQUESTED_CONTEXT));

        cache.register(RootInterface.class, "registered", REQUESTED_CONTEXT);
        assertEquals("registered", cache.getRegisteredObject(Child.class, REQUESTED_CONTEXT));

        cache.unregister(RootInterface.class, REQUESTED_CONTEXT);
        assertNull(cache.getRegisteredObject(Child.class, REQUESTED_CONTEXT));
    }

    @Test
    void resolvesPrimitiveAndWrapperTypes() {
        CacheMap<String, String> cache = new CacheMap<>(DEFAULT_CONTEXT);
        cache.register(Integer.class, "integer", REQUESTED_CONTEXT);

        assertEquals("integer", cache.getRegisteredObject(int.class, REQUESTED_CONTEXT));
    }

    @Test
    void supportsNullDefaultContext() {
        CacheMap<String, String> cache = new CacheMap<>(null);
        cache.register(Object.class, "fallback", null);

        assertEquals("fallback", cache.getRegisteredObject(String.class, REQUESTED_CONTEXT));
    }

    @Test
    void returnsDistinctValuesInEncounterOrder() {
        CacheMap<String, String> cache = new CacheMap<>(DEFAULT_CONTEXT);
        cache.register(String.class, "shared", DEFAULT_CONTEXT);
        cache.register(Integer.class, "shared", DEFAULT_CONTEXT);

        assertEquals(List.of("shared"), cache.getValues());
    }

    private interface RootInterface {
    }

    private interface ChildInterface extends RootInterface {
    }

    private static class Parent implements RootInterface {
    }

    private static class Child extends Parent implements ChildInterface {
    }
}
