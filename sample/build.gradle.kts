import org.gradle.api.tasks.PathSensitivity

plugins {
    alias(wip.plugins.kotlinMultiplatform)
    alias(wip.plugins.composeMultiplatform)
    alias(wip.plugins.composeCompiler)
    alias(wip.plugins.ksp)
    alias(libs.plugins.viddik)
    id("io.github.youndie.sborka.kmp")
    id("io.github.youndie.sborka.lint")
}

kotlin {
    // Desktop only until B-41, which adds Android and iOS once there are screens to show on them.
    jvm("desktop")

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

// No workaround for youndie/viddik#44 here, unlike oldge-core: with one target there is no
// kspCommonMainKotlinMetadata task, which is the task the bug fails to order (B-37).
