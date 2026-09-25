#!/usr/bin/env bash
# The sample app on an iOS simulator (B-41): the same `OldgeSampleApp` the desktop opens and the
# Android build hosts. Adapted from kvadrant-ui's script of the same name.
#
# **No Xcode project.** Kotlin/Native links a Mach-O, and a `.app` for the simulator is a directory
# holding that binary and an `Info.plist`. The thing that runs is built by the same compiler out of
# the same source set as everything it draws. See `IosEntryPoint.kt` for the UIKit under it.
#
# Usage:
#   scripts/ios-sample-app.sh
#   OLDGE_SIMULATOR="iPhone 17" scripts/ios-sample-app.sh
set -euo pipefail

cd "$(dirname "$0")/.."

BUNDLE_ID="io.github.youndie.oldge.sample"
DEVICE="${OLDGE_SIMULATOR:-$(xcrun simctl list devices available -j |
  python3 -c 'import json,sys; d=json.load(sys.stdin)["devices"];
print(next(x["udid"] for k,v in d.items() if "iOS" in k for x in v))')}"

./gradlew :sample:linkDebugExecutableIosSimulatorArm64 -q

BIN="sample/build/bin/iosSimulatorArm64/debugExecutable/sample.kexe"
[ -f "$BIN" ] || { echo "no executable at $BIN"; exit 1; }

APP="$(mktemp -d)/OldgeSample.app"
mkdir -p "$APP"
cp "$BIN" "$APP/OldgeSample"

# **The fonts.** compose-resources on iOS reads `compose-resources/composeResources/…` out of the
# *bundle*, and a hand-assembled `.app` has whatever this script puts in it. Without them the app
# dies on its first frame with a `MissingResourceException` naming a font. The Gradle-assembled
# directory rather than `src/`, so a resource that never made it into the build is missing here too.
RESOURCES="oldge-core/build/processedResources/iosSimulatorArm64/main/composeResources"
[ -d "$RESOURCES" ] || { echo "no assembled resources at $RESOURCES"; exit 1; }
mkdir -p "$APP/compose-resources"
cp -R "$RESOURCES" "$APP/compose-resources/"

cat > "$APP/Info.plist" <<PLIST
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
  <key>CFBundleExecutable</key><string>OldgeSample</string>
  <key>CFBundleIdentifier</key><string>$BUNDLE_ID</string>
  <key>CFBundleName</key><string>oldge-ui</string>
  <key>CFBundlePackageType</key><string>APPL</string>
  <key>CFBundleShortVersionString</key><string>1.0</string>
  <key>CFBundleVersion</key><string>1</string>
  <key>LSRequiresIPhoneOS</key><true/>
  <key>UIDeviceFamily</key><array><integer>1</integer></array>
  <key>MinimumOSVersion</key><string>15.0</string>
  <!-- Compose Multiplatform REFUSES TO START WITHOUT THIS and says so by name: without the key it
       throws a sanity check on the main queue, the window never appears, and the application sits
       alive on the springboard with nothing on screen. Xcode's own templates carry it, so a
       hand-written bundle is the one place it goes missing. -->
  <key>CADisableMinimumFrameDurationOnPhone</key><true/>
  <!-- WITHOUT THIS THE APPLICATION IS LETTERBOXED. A bundle with no launch screen tells iOS it was
       built for a legacy screen, so the system runs it in a compatibility canvas: black bands above
       and below and a window smaller than the display. For a library whose subject is a page whose
       background reaches the glass, that is not cosmetic. An EMPTY dictionary is the whole
       declaration — it says "this application supports whatever screen it is given". -->
  <key>UILaunchScreen</key><dict/>
</dict>
</plist>
PLIST

xcrun simctl boot "$DEVICE" 2>/dev/null || true
xcrun simctl bootstatus "$DEVICE" -b >/dev/null 2>&1 || true
xcrun simctl install "$DEVICE" "$APP"
xcrun simctl launch --terminate-running-process "$DEVICE" "$BUNDLE_ID"
