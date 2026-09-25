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
            // `api`: DatePicker's value is a `LocalDate` (B-19), so a consumer compiles against it.
            api(wip.kotlinx.datetime)
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
            // OldgeTokensTest reads tokens.json on its own, as a second reading of the generator's source.
            implementation(wip.kotlinx.serialization.json)
        }
    }
}

compose.resources {
    packageOfResClass = "io.github.youndie.oldge.resources"
}

viddik {
    verifyOnCheck.set(true)
    // Design parity (research §1.10). The floor — what "only glyph edges" measures here — is 0.73–0.75 %
    // on Divider, a component with little text; a probe of large LCD digits is 3.4 % with nothing but
    // text wrong. One percentage cannot tell a broken component with little text from a correct one
    // with much, so the tolerance stays at viddik's 5 %, written here to say it was chosen, and parity
    // stays a report: every component item reads its number against the floor in its commit body.
    designTolerancePercent.set(5.0)
    designChannelTolerance.set(16)
    designStrict.set(false)
}

// B-05. The token layer is generated from the vendored tokens.json and committed; this fails `check`
// when the two have drifted. Standard-library python, so any machine with a python3 runs it.
val checkOldgeTokens by tasks.registering(Exec::class) {
    description = "Fail if OldgeTokens.kt no longer matches reference/design-system/tokens.json."
    val root = rootProject.layout.projectDirectory
    inputs.file(root.file("scripts/generate_tokens.py"))
    inputs.file(root.file("reference/design-system/tokens.json"))
    inputs.file(layout.projectDirectory.file("src/commonMain/kotlin/io/github/youndie/oldge/tokens/OldgeTokens.kt"))
    outputs.upToDateWhen { true }
    workingDir = root.asFile
    commandLine("python3", "scripts/generate_tokens.py", "--check")
}

tasks.named("check") { dependsOn(checkOldgeTokens) }

// B-11. The icon set, generated from the vendored bundle.js the same way.
val checkOldgeIcons by tasks.registering(Exec::class) {
    description = "Fail if OldgeIcons.kt no longer matches reference/design-system/components/bundle.js."
    val root = rootProject.layout.projectDirectory
    inputs.file(root.file("scripts/generate_icons.py"))
    inputs.file(root.file("reference/design-system/components/bundle.js"))
    inputs.file(layout.projectDirectory.file("src/commonMain/kotlin/io/github/youndie/oldge/icons/OldgeIcons.kt"))
    outputs.upToDateWhen { true }
    workingDir = root.asFile
    commandLine("python3", "scripts/generate_icons.py", "--check")
}

tasks.named("check") { dependsOn(checkOldgeIcons) }

// A test that reads the golden directory must declare it, or Gradle leaves the test UP-TO-DATE over a
// changed set and reports the last run's verdict (kvadrant-ui's lesson).
tasks.named<Test>("desktopTest") {
    inputs
        .dir(layout.projectDirectory.dir("src/desktopTest/snapshots"))
        .withPropertyName("viddikSnapshots")
        .withPathSensitivity(PathSensitivity.RELATIVE)
    // OldgeTokensTest reads tokens.json and DesignStringCoverageTest the previews. Undeclared, a
    // changed design system left the test UP-TO-DATE and `check` green over it — measured in B-05,
    // where a mutated colour failed only the generator's --check.
    inputs
        .dir(rootProject.layout.projectDirectory.dir("reference/design-system"))
        .withPropertyName("designSystem")
        .withPathSensitivity(PathSensitivity.RELATIVE)
    // ScreenshotSuiteTest counts a page as built from sample's goldens (B-40).
    inputs
        .dir(rootProject.layout.projectDirectory.dir("sample/src/desktopTest/snapshots"))
        .withPropertyName("sampleSnapshots")
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

// The shared iOS metadata compilation, and only it, without warnings-as-errors. CMP 1.12.0's own graph
// puts two lifecycle klibs with one `unique_name` on it — JetBrains' lifecycle fork 2.9.6 redirects
// to androidx.lifecycle 2.11.0 for iOS (`dependencyInsight` on iosArm64CompileKlibraries shows the
// redirect) — and the KLIB loader's warning about that is not something this module can fix. The
// per-target iOS compilations keep -Werror, so a warning in this library's own code still fails.
// viddik met the same pair and made the same cut (its compileIosMainKotlinMetadata).
tasks
    .matching {
        it.name in
            setOf("compileIosMainKotlinMetadata", "compileAppleMainKotlinMetadata", "compileNativeMainKotlinMetadata")
    }.configureEach {
        (this as org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask<*>).compilerOptions.allWarningsAsErrors.set(
            false,
        )
    }
