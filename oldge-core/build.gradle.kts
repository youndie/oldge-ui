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

    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        // Required by the Compose plugin's own check (CMP-4906): without an executable binary the
        // Skiko runtime cannot be bundled for the target's tests, and `build` fails on it.
        binaries.executable()
    }

    iosArm64()
    iosSimulatorArm64()

    android {
        // Off by default in AGP's KMP plugin, and with it off the AAR ships without the bundled
        // fonts, green (kvadrant-ui B-37).
        androidResources { enable = true }

        namespace = "io.github.youndie.oldge"
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
            implementation(wip.compose.runtime)
            implementation(wip.compose.foundation)
            implementation(wip.compose.ui)
            // Not in `wip`, and the plugin's accessor is the one place its version follows the
            // Compose plugin's own; the deprecation warning is the price of not spelling it twice.
            implementation(compose.components.resources)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
        getByName("desktopTest").dependencies {
            // The host's skia. Never in a published source set: the POM would pin the machine
            // that published.
            implementation(compose.desktop.currentOs)
            implementation(wip.compose.ui.test)
        }
    }
}

compose.resources {
    packageOfResClass = "io.github.youndie.oldge.resources"
}

viddik {
    verifyOnCheck.set(true)
}

// A test that reads the golden directory must declare it, or Gradle leaves the test UP-TO-DATE over a
// changed set and reports the last run's verdict (kvadrant-ui's lesson).
tasks.named<Test>("desktopTest") {
    inputs
        .dir(layout.projectDirectory.dir("src/desktopTest/snapshots"))
        .withPropertyName("viddikSnapshots")
        .withPathSensitivity(PathSensitivity.RELATIVE)
}

// WORKAROUND for youndie/viddik#44, delete on the viddik bump that carries 10f128b (B-35).
//
// viddik 0.6.0 puts build/generated/ksp/metadata/commonMain/kotlin on commonMain whether or not
// `showroomTargets` is on, but orders the other tasks after `kspCommonMainKotlinMetadata` only when
// it is. That task runs in every KMP module with KSP, Gradle matches outputs to inputs by location,
// and `./gradlew build` then fails every per-target KSP and compile task with "uses this output of
// task ':oldge-core:kspCommonMainKotlinMetadata' without declaring an explicit or implicit
// dependency" — reproduced here before this block existed. `viddikRecord` alone does not schedule
// the metadata task and passes with the bug in place, which is why B-01's acceptance is `build`.
// The ordering below is the one the plugin itself declares when the feature is on, widened to every
// task that reads source files: compilation alone was the first attempt, and ktlint's check over
// commonMain then failed the same way — the directory is a commonMain source root, so anything that
// walks the source set reads it. ktlint's tasks are not `SourceTask`s, hence the name match.
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
