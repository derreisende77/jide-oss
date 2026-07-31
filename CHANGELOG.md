# Changelog

This file records the changes made after this repository diverged from
[JFormDesigner/jide-oss](https://github.com/JFormDesigner/jide-oss) at JIDE
Common Layer 3.7.15.

It is a living document rather than a release history. Builds are distributed
exclusively through JitPack.

## Changes since the fork

### FlatLaf compatibility

- Made FlatLaf the only supported look and feel.
- Preserved the `LookAndFeelFactory` initializer and customizer registration
  API required by `flatlaf-jide-oss` 3.7.2.
- Resolved registered hooks for the active FlatLaf class and its superclasses.
- Ran registered and explicitly added initializers before installing JIDE
  defaults, followed by registered and explicitly added customizers.
- Made `JideSplitButton` delegate look-and-feel updates to `JMenu` so an
  existing popup menu also switches to the active FlatLaf theme.
- Added compatibility tests that install JIDE defaults with FlatLaf and verify
  hook ordering, component initialization, and split-button popup updates.
- Use only the official Maven Central `flatlaf` and `flatlaf-jide-oss` 3.7.2
  releases for tests and the showcase, while excluding the add-on's transitive
  `jide-oss` artifact so it exercises this fork.

### Fixes

- Corrected `JideButton` hyperlink cursor restoration after text or icon
  changes and maximum-size calculation for vertically oriented HTML content.
- Corrected vertical `JideLabel` sizing and painting with asymmetric insets,
  ensured orientation changes repaint, and rejected unsupported orientations.
- Corrected `JidePopupMenu` tooltip-state restoration for overlapping menus,
  removed scrolling listeners when their popup closes, and aligned the
  visible-menu-item-count property event with its JavaBeans property name.
- Corrected `RangeSlider#setLowValue` so it preserves the upper endpoint and
  clamps the lower endpoint, and made upper-thumb horizontal keyboard actions
  follow right-to-left component orientation.
- Corrected `JideSplitButton` programmatic clicks to complete the press/release
  cycle and consistently support custom `SplitButtonModel` implementations in
  both component state access and UI painting.
- Corrected `JideTabbedPane` tab moving to preserve per-tab properties and
  closability, restored focus state after failed moves, and kept component
  replacement focus and closability bookkeeping consistent.
- Made `JideToggleButton` safely serializable with action-selection
  synchronization intact and aligned grouped keyboard-focus traversal with
  current `JToggleButton` behavior.
- Corrected `JideToggleSplitButton` item and accessibility state, action-backed
  serialization and selection synchronization, custom `SplitButtonModel`
  support, and `SplitButtonGroup` selection reporting and clearing.
- Corrected `TristateCheckBox` mixed-state item and accessibility events,
  `ButtonGroup` exclusivity, custom-icon restoration, invalid-state handling,
  serialization, and FlatLaf light/dark indeterminate rendering.
- Made `CheckBoxList` mouse hit-testing use the checkbox icon bounds produced
  by the active cell renderer and look and feel, including nested renderers,
  insets, explicit icons, cell containment, and right-to-left orientation.
- Corrected `CheckBoxList` replacement selection-model listener wiring and
  clearing checkbox selections for null or missing selected values.
- Corrected `JidePopup` owner retention, popup-type transitions, reentrant
  hiding, timer cleanup, detached-property events, and failed lightweight
  popup creation.

### Java and API modernization

- Require Java 17 or newer and generate Java 17 bytecode.
- Replaced the bundled SwingWorker implementation with
  `javax.swing.SwingWorker`.
- Replaced the custom Base64 implementation with `java.util.Base64`.
- Replaced internal JDK APIs used by folder chooser, image, security, and Swing
  utility code with supported public APIs.
- Removed references to the `java.applet.Applet` and `javax.swing.JApplet`
  classes from `JidePopup`, `JideSwingUtilities`,
  `ResizableMouseInputAdapter`, and `PortingUtils` for JDK 26 compatibility.
- Removed obsolete JDK-version and operating-system detection branches.
- Removed Security Manager and privileged-action support.
- Simplified `PortingUtils`, `SecurityUtils`, and other legacy compatibility
  helpers by calling the standard JDK APIs directly.
- Removed reflection helpers where direct Java 17 APIs are available.
- Removed unnecessary boxing, temporary wrapper collections, and deprecated
  wrapper constructors while preserving property-event and calculation
  behavior.

### Look-and-feel cleanup

- Removed the Aqua, Metal, Windows, VSNet, Eclipse, Xerto, Metro, Office 2003,
  Office 2007, and Office 2010 implementations and their resources.
- Removed Windows XP theme detection and desktop-property integration.
- Removed legacy look-and-feel factories, extensions, icons, cached
  look-and-feel state, and fallback selection logic.
- Retained and simplified the basic JIDE delegates used by the FlatLaf addon.

### Utility improvements

- Optimized `CacheMap` lookups and cache rebuilding in `CachedArrayList` and
  `CachedVector`.
- Simplified RGB/HSL color conversion and derived-color calculation.
- Simplified variance calculation to avoid allocating a temporary collection.
- Replaced boxed line-width and selection-index collections with primitive
  arrays.
- Removed unused legacy checkbox-tree classes.

### Build and testing

- Added an interactive FlatLaf light/dark showcase for all branch-maintained
  controls, including tab moving, grouped toggle focus, saved toggle state, and
  verified toggle-split events, action state, accessibility and serialization,
  grouped tristate behavior, custom-icon restoration, and both `CheckBoxList`
  click-hit modes, plus content replacement, runnable with `./gradlew runDemo`,
  with a headless smoke task.
- Refresh detached popup-menu component trees when switching showcase themes,
  so retained scrolling menus and their items immediately adopt light or dark
  FlatLaf styling.
- Modernized the build around Gradle and added the Gradle 9.6.1 wrapper for
  reproducible and JitPack-compatible builds.
- Enabled the Gradle configuration cache.
- Added source and Javadoc JAR generation, local Maven installation, and
  JitPack publication support.
- Converted the legacy automated tests to JUnit Jupiter.
- Added dedicated `flatLafLightTest` and `flatLafDarkTest` tasks and made the
  standard `test` target run each Swing component test under both official
  FlatLaf themes while running non-UI tests once.
- Added shared JUnit theme setup and restoration with a guard that verifies the
  requested light or dark theme is active.
- Corrected malformed Javadoc markup, links, parameter descriptions, and
  inherited exception documentation so Javadoc generation completes without
  warnings.
- Removed the legacy Sonatype/OSSRH repository, artifact signing, credentials,
  and `-Drelease` version switch while retaining the Maven publication required
  by JitPack and `publishToMavenLocal`.
- Enabled native access for Gradle test JVMs as required by FlatLaf on current
  JDKs.
- Added focused tests for FlatLaf compatibility, public JDK API replacements,
  utility optimizations, property-change behavior, calculations, selection
  handling, styled-label sizing, `JideButton` cursor and vertical HTML sizing,
  `JidePopupMenu` lifecycle behavior, `RangeSlider` endpoint and right-to-left
  keyboard behavior, and `JideLabel` vertical layout and orientation behavior.
- Removed obsolete Ant and Maven build files, CI configuration, IDE metadata,
  Sonatype instructions, and old build documentation.

### Removed compatibility APIs

The fork intentionally removes APIs that only supported obsolete runtimes or
look and feels, including:

- `ActionSupportForJDK5`
- the bundled `Base64` and SwingWorker implementations
- `ExtWindowsDesktopProperty` and `WindowsDesktopProperty`
- obsolete compatibility members from the retained `SystemInfo`,
  `PortingUtils`, and `SecurityUtils` classes
- legacy checkbox-tree implementations
- internal look-and-feel implementations and their public support classes

### Removed original JIDE utility and compatibility types

Compared with the JIDE Common Layer 3.7.15 source tree, this fork removes the
following 12 original utility and compatibility source types. Types provided
in both the JDK 8 and JDK 9 source trees are listed once by their fully
qualified name; removed `package-info.java` files are not included.

- `com.jidesoft.jdk`: `JdkSpecificClass`
- `com.jidesoft.utils`: `AccumulativeRunnable`, `ActionSupportForJDK5`,
  `Base64`, `FontFilesResource`, `ReflectionUtils`, `SwingWorker`
- `com.jidesoft.swing`: `LegacyCheckBoxTree`,
  `LegacyCheckBoxTreeCellRenderer`, `LegacyCheckBoxTreeSelectionModel`,
  `LegacyTristateCheckBox`, `TristateCheckBoxIcon`

Applications using any of these APIs must migrate to the corresponding JDK API
or remove the obsolete look-and-feel-specific integration before upgrading.
