# oldge-ui

A Compose Multiplatform implementation of the **oldge-ui** design system: mobile components in the
aesthetic of mid-2000s software — thick glossy frames, dark bodies with a soft glow, chrome orb
buttons, LCD readouts, XP tray balloons — around a toxic lime accent, in three skins.

<table>
  <tr>
    <td align="center"><img src="sample/src/desktopTest/snapshots/Launcher_Toxic.png" width="260" alt="The Launcher page in the Toxic skin"></td>
    <td align="center"><img src="sample/src/desktopTest/snapshots/Launcher_Media.png" width="260" alt="The Launcher page in the Media skin"></td>
    <td align="center"><img src="sample/src/desktopTest/snapshots/Launcher_Crystal.png" width="260" alt="The Launcher page in the Crystal skin"></td>
  </tr>
  <tr>
    <td align="center"><b>Toxic</b></td>
    <td align="center"><b>Media</b></td>
    <td align="center"><b>Crystal</b></td>
  </tr>
</table>

All 51 components of the design system are built, with the nine pages of its demo as the sample
app. Each component is held to the design by pixel parity against references rendered from the
design system itself.

Every image here is a screenshot golden: the picture `./gradlew check` compares the code's render
against on every build. So it is what the library draws, not a mock-up.

## Pages

<table>
  <tr>
    <td><img src="sample/src/desktopTest/snapshots/FeedScreen_Toxic.png" width="195" alt="The feed page"></td>
    <td><img src="sample/src/desktopTest/snapshots/ChatScreen_Crystal.png" width="195" alt="The chat page"></td>
    <td><img src="sample/src/desktopTest/snapshots/MediaScreen_Media.png" width="195" alt="The photo viewer page"></td>
    <td><img src="sample/src/desktopTest/snapshots/SettingsScreen_Media.png" width="195" alt="The settings page"></td>
  </tr>
</table>

## Components

<table>
  <tr>
    <td><img src="oldge-core/src/desktopTest/snapshots/Button_Toxic.png" width="390" alt="Buttons"></td>
    <td><img src="oldge-core/src/desktopTest/snapshots/Dialog_Media.png" width="390" alt="A dialog"></td>
  </tr>
  <tr>
    <td><img src="oldge-core/src/desktopTest/snapshots/Readout_Toxic.png" width="390" alt="LCD readouts"></td>
    <td><img src="oldge-core/src/desktopTest/snapshots/Balloon_Media.png" width="390" alt="Tray balloons"></td>
  </tr>
  <tr>
    <td><img src="oldge-core/src/desktopTest/snapshots/Menu_Toxic.png" width="390" alt="A menu"></td>
    <td><img src="oldge-core/src/desktopTest/snapshots/DatePicker_Crystal.png" width="390" alt="A date picker"></td>
  </tr>
  <tr>
    <td><img src="oldge-core/src/desktopTest/snapshots/ChatBubble_Toxic.png" width="390" alt="Chat bubbles"></td>
    <td><img src="oldge-core/src/desktopTest/snapshots/Switch_Crystal.png" width="390" alt="Switches"></td>
  </tr>
</table>

[docs/components.md](docs/components.md) lists all of them, with the goldens that show each.

## Using it

```kotlin
repositories {
    maven("https://reposilite.kotlin.website/snapshots") {
        mavenContent { includeGroupAndSubgroups("io.github.youndie") }
    }
}

dependencies {
    implementation("io.github.youndie:oldge-core:0.1.0")
}
```

```kotlin
OldgeTheme(OldgeSkin.Toxic) {
    OldgeScreenBody {
        OldgeButton("Войти", onClick = {}, variant = OldgeButtonVariant.Primary)
    }
}
```

Targets: desktop (JVM), Android, iOS (arm64 and the arm64 simulator), and wasm.

The sample app, with a page and skin switcher: `./gradlew :sample:run`.

## Where things are

[docs/](docs/README.md) holds the documentation. Start with the
[architecture research](docs/research/research-architecture.md), then the [backlog](backlog.md).
The design system itself is vendored in [reference/design-system/](reference/design-system/).

## Licence

- **The code** is under the Apache License 2.0 ([LICENSE](LICENSE)).
- **The bundled fonts** are under their own licences: SIL OFL 1.1, and DejaVu's. Their full texts
  are in `oldge-core/src/commonMain/composeResources/files/`, and the POM declares them.
