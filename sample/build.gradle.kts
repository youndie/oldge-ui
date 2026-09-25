import org.gradle.api.tasks.PathSensitivity

plugins {
    alias(wip.plugins.kotlinMultiplatform)
    alias(wip.plugins.composeMultiplatform)
    alias(wip.plugins.composeCompiler)
    alias(wip.plugins.ksp)
    alias(libs.plugins.viddik)
    alias(wip.plugins.androidKotlinMultiplatformLibrary)
    id("io.github.youndie.sborka.kmp")
    id("io.github.youndie.sborka.lint")
}

kotlin {
    jvm("desktop")

    // The sample app on iOS (B-41): `binaries.executable` makes a Mach-O with an entry point, and a
    // `.app` for the simulator is a directory holding it and an `Info.plist`, which
    // `scripts/ios-sample-app.sh` assembles, so no Xcode project exists. The simulator only.
    iosSimulatorArm64 {
        binaries.executable {
            entryPoint = "io.github.youndie.oldge.sample.ios.main"
        }
    }

    // A library on Android and an application on the desktop, which is forced: since AGP 9 the
    // application plugin refuses a Kotlin Multiplatform module, so `:sample-android` is the thin
    // activity that hosts this (kvadrant-ui's research §1.13).
    android {
        namespace = "io.github.youndie.oldge.sample"
        compileSdk =
            libs.versions.android.compileSdk
                .get()
                .toInt()
        minSdk =
            libs.versions.android.minSdk
                .get()
                .toInt()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":oldge-core"))
            implementation(wip.compose.runtime)
            implementation(wip.compose.foundation)
            implementation(wip.compose.ui)
        }
        getByName("desktopMain").dependencies {
            implementation(compose.desktop.currentOs)
        }
        // The library as a consumer sees it: these tests compile against oldge-core's public API only
        // (B-51).
        getByName("desktopTest").dependencies {
            implementation(kotlin("test"))
            implementation(wip.compose.ui.test)
            implementation(wip.kotlinx.serialization.json)
        }
    }
}

compose.desktop {
    application {
        mainClass = "io.github.youndie.oldge.sample.MainKt"
    }
}

// The screens' parity (B-37): the same tolerances as oldge-core, read against the same floor
// (research §1.10). The references are the renderer's output for the page previews, written here
// with `--out` (`node scripts/design-references.mjs --only 'AuthScreen_*' --out
// sample/src/desktopTest/snapshots/design`).
viddik {
    verifyOnCheck.set(true)
    designTolerancePercent.set(5.0)
    designChannelTolerance.set(16)
    designStrict.set(false)
}

// A test that reads the golden directory must declare it, or Gradle leaves the test UP-TO-DATE over a
// changed set (oldge-core's block of the same name).
tasks.named<Test>("desktopTest") {
    inputs
        .dir(layout.projectDirectory.dir("src/desktopTest/snapshots"))
        .withPropertyName("viddikSnapshots")
        .withPathSensitivity(PathSensitivity.RELATIVE)
}

// WORKAROUND for youndie/viddik#44, delete with oldge-core's on the viddik bump that carries 10f128b
// (B-35). With a second target the module has a `kspCommonMainKotlinMetadata`, which viddik 0.6.0
// does not order the other tasks after; oldge-core's block of the same name says why each kind of
// task is in the list. With one target there was no such task and the block failed the build (B-37).
val commonKsp = "kspCommonMainKotlinMetadata"
tasks
    .matching { task ->
        task.name != commonKsp &&
            (
                task.name.startsWith("ksp") ||
                    task is org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask<*> ||
                    task is SourceTask ||
                    task.name.contains("Ktlint", ignoreCase = true)
            )
    }.configureEach { dependsOn(commonKsp) }
