# JIDE OSS — FlatLaf-specific Java 17 fork

This repository is a fork of
[JFormDesigner/jide-oss](https://github.com/JFormDesigner/jide-oss), which is
itself a mirror of the original
[jidesoft/jide-oss](https://github.com/jidesoft/jide-oss) project.

This fork modernizes the JIDE Common Layer for applications that use
[FlatLaf](https://www.formdev.com/flatlaf/) and do not need the legacy
look-and-feel implementations or compatibility code retained by the upstream
project.

## Motivation

I originally wanted to contribute fixes and modernization work through the
official JIDE channels. I tried reaching out by email, attempted to register for
the JIDE forum, and opened an issue in the JIDE GitHub project, but none of
those attempts received a response or provided a workable path for
collaboration.

Eventually, carrying local patches while waiting for an upstream conversation
became impractical. I created this fork so applications that still depend on
the valuable JIDE Common Layer can use it with current Java releases and
FlatLaf through a maintained, tested, and reproducible build.

This fork is not meant to discount the substantial work behind JIDE OSS. It is
a pragmatic continuation focused on the needs of modern FlatLaf-based Swing
applications.

## Maintenance and contributions

This fork is maintained on a needs-driven basis for as long as changes are
necessary to support my other open-source projects. It is not intended to
promise indefinite, full-time maintenance or to restore the complete scope of
the original JIDE product family.

Contributions that improve the maintained Common Layer, preserve FlatLaf
compatibility, or add focused regression coverage are welcome. Please include
tests where practical and keep changes aligned with the Java 17 and
FlatLaf-specific direction of this fork.

## Compared with JIDE 3.8.1

JIDE publicly lists
[version 3.8.1](https://www.jidesoft.com/history/index.php#3.8.1), released on
May 26, 2025, but the corresponding implementation was not published as an
updated open-source JIDE Common Layer release and was available only through
the customer distribution. This means the comparison below is necessarily an
estimate based on JIDE's public release notes rather than a source-level diff.

The public notes say that 3.8.0 added JDK 9 and newer support, limited JDK 17
support, official FlatLaf support, and HiDPI icon work. The only change listed
for 3.8.1 is removal of Applet usage for Java 26 compatibility.

| Area | JIDE 3.8.1 release notes | This fork |
| --- | --- | --- |
| Availability | Release notes are public, but the implementation was distributed to paying customers rather than published as an updated OSS source tree. | Public source, reproducible Gradle build, tests, and runnable demo. |
| Java platform | Supports newer JDKs and removes Applet usage for Java 26. | Requires and targets Java 17; removes Applet and JApplet usage, Security Manager code, compatibility backports, and unsupported internal JDK APIs; builds and tests on Java 26. |
| FlatLaf | Declares official FlatLaf support. | Is deliberately FlatLaf-specific, tested with `flatlaf-jide-oss` 3.7.2, and includes additional theme-refresh, state, sizing, popup, and interaction fixes. |
| Build and verification | The release notes do not describe its build or regression coverage. | Uses the Gradle 9.6.1 wrapper, JUnit Jupiter, focused Swing regression tests, light/dark rendering checks, and an interactive showcase. |
| HiDPI icons | Describes 1x, 2x, and 3x assets, `MultiResolutionImage`, and `IconFactory.getScaledIcon()`. | Does not currently contain an equivalent `IconFactory` or the documented multi-resolution asset work; 3.8.1 may be ahead in this area. |
| Scope | Covers JIDE's broader commercial product family as well as the Common Layer. | Intentionally focuses on the open-source Common Layer and removes legacy look-and-feel implementations and compatibility machinery. |

**Best estimate:** for an open-source Swing application using Java 17 or newer
and FlatLaf, this fork is more modern in its toolchain, maintainability,
testability, and targeted integration work. It should not be presented as a
feature-complete successor to every part of the commercial JIDE 3.8.1 release,
especially its HiDPI asset work and non-OSS product modules.

See [CHANGELOG.md](CHANGELOG.md) for a categorized list of changes made since
the fork.

## Requirements

- JDK 17 or newer is required to build and run this library.
- Gradle is invoked through the included wrapper; a separate Gradle
  installation is not required.

The generated classes target Java 17. They cannot be used on older
runtimes.

## Changes in this fork

Compared with the upstream JIDE OSS project, this fork:

- targets FlatLaf and removes the obsolete Aqua, Metal, Windows, VSNet,
  Eclipse, Xerto, Office, Metro, and other legacy look-and-feel machinery;
- preserves the registration hooks required by `flatlaf-jide-oss` 3.7.2,
  including registrations made for FlatLaf superclasses, and refreshes
  `JideSplitButton` popup menus when switching themes, while keeping
  programmatic clicks and custom split-button models functional;
- restores custom cursors after `JideButton` leaves hyperlink mode, including
  after text or icon changes, and correctly sizes vertical HTML buttons;
- correctly sizes and paints vertical `JideLabel` content with asymmetric
  insets and validates orientation changes;
- balances shared tooltip state and scrolling listeners across
  `JidePopupMenu` lifecycles and emits its visible-item-count property event
  under the JavaBeans property name;
- preserves the upper `RangeSlider` endpoint when changing its lower value and
  applies right-to-left direction to upper-thumb horizontal keyboard actions;
- preserves `JideTabbedPane` properties and closability while moving tabs and
  maintains focus tracking when replacing tab content;
- makes `JideToggleButton` state serializable and directs grouped keyboard
  focus to the selected eligible toggle;
- keeps `JideToggleSplitButton` item, accessibility, action, serialized, and
  grouped selection state synchronized across supported split-button models;
- keeps `TristateCheckBox` mixed states synchronized with item events,
  accessibility, button groups, custom icons, serialization, and FlatLaf
  indeterminate rendering;
- requires and targets JDK 17, with obsolete JDK-version detection,
  Security Manager support, compatibility backports, and internal JDK API usage
  removed;
- removes references to the `java.applet.Applet` and `javax.swing.JApplet`
  classes from `JidePopup`, `JideSwingUtilities`,
  `ResizableMouseInputAdapter`, and `PortingUtils` for JDK 26 compatibility;
- replaces legacy helpers with standard JDK APIs, including SwingWorker and
  Base64 functionality;
- removes unused Windows XP integration, desktop-property helpers, old checkbox
  tree implementations, build files, IDE metadata, and publication
  documentation;
- uses a current Gradle wrapper, Gradle configuration cache, JUnit Jupiter
  tests, Javadoc and source JAR generation, Maven-local publication, and JitPack
  compatible builds;
- simplifies utility APIs and optimizes frequently used `CacheMap` lookup and
  color conversion paths.

See the
[removed original JIDE utility and compatibility types](CHANGELOG.md#removed-original-jide-utility-and-compatibility-types)
section for the package-by-package inventory.

This is intentionally not a general-purpose continuation of every historical
JIDE look and feel. Applications requiring those implementations should use an
upstream release instead.

## Building

```shell
./gradlew clean build
```

### Testing with FlatLaf

The standard test target runs non-UI tests once and runs every tagged Swing
component test under both supported FlatLaf themes:

```shell
./gradlew test
```

The themed test groups can also be run independently:

```shell
./gradlew flatLafLightTest
./gradlew flatLafDarkTest
```

Each themed test installs the corresponding official `FlatLightLaf` or
`FlatDarkLaf` release together with the JIDE defaults supplied by
`flatlaf-jide-oss`. Theme setup and restoration run on the Swing event dispatch
thread, and the compatibility tests verify that the requested light or dark
theme is actually active.

### Interactive FlatLaf control showcase

Run the demo application with:

```shell
./gradlew runDemo
```

The showcase includes `JideButton`, `JideLabel`, `JidePopupMenu`,
`JideSplitButton`, `JideTabbedPane`, `JideToggleButton`,
`JideToggleSplitButton`, `RangeSlider`, `TristateCheckBox`, `CheckBoxList`,
and `JidePopup`. Its header switches the live application between FlatLaf
light and dark themes. The tabbed-pane page can also move selected tabs and
replace their content interactively, while the buttons page demonstrates
grouped toggle focus, saved toggle state, and verified `JideToggleSplitButton`
item events, action synchronization, accessibility, grouping, and
serialization. Retained scrolling popup menus are also refreshed when switching
between light and dark themes. The selection page demonstrates balanced
tristate events, custom-icon restoration, exclusive grouped mixed states, and
both whole-row and checkbox-only `CheckBoxList` toggling.

To install the snapshot into the local Maven repository:

```shell
./gradlew publishToMavenLocal
```

The local publication uses these coordinates:

```text
com.formdev:jide-oss:3.7.15.1-SNAPSHOT
```

Use the locally published artifact from Maven:

```xml
<dependency>
    <groupId>com.formdev</groupId>
    <artifactId>jide-oss</artifactId>
    <version>3.7.15.1-SNAPSHOT</version>
</dependency>
```

## Using the library from JitPack

### Gradle

Add JitPack to the consumer's repositories and depend on a published commit:

```kotlin
repositories {
    mavenCentral()
    maven("https://jitpack.io")
}
```

```kotlin
dependencies {
    implementation("com.github.derreisende77:jide-oss:COMMIT_SHA")
}
```

### Maven

Add the JitPack repository and dependency:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.derreisende77</groupId>
        <artifactId>jide-oss</artifactId>
        <version>COMMIT_SHA</version>
    </dependency>
</dependencies>
```

Replace `COMMIT_SHA` with the desired commit SHA. A release tag can be used
instead when one is available.

JitPack uses the project's `maven-publish` publication and invokes
`publishToMavenLocal` to collect the library, source, and Javadoc artifacts.
This fork does not configure direct Sonatype/OSSRH publication or artifact
signing.

## FlatLaf compatibility

The test suite and demo use the official Maven Central releases
`flatlaf` 3.7.2 and `flatlaf-jide-oss` 3.7.2. Snapshots and builds made directly
from the FlatLaf Git repository are intentionally not used. The transitive
released `jide-oss` artifact is excluded so that the FlatLaf integration is
compiled and tested against this repository's Common Layer implementation.

The tests verify the initializer and customizer registration contract used when
FlatLaf installs JIDE defaults, the FlatLaf-specific UI delegates, light and
dark component behavior, and look-and-feel updates for existing
`JideSplitButton` popup menus.

## License

JIDE Common Layer is dual-licensed under the GPL with classpath exception and a
free commercial license. See [LICENSE.txt](LICENSE.txt).
