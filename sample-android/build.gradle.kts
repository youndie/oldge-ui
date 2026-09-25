plugins {
    alias(wip.plugins.androidApplication)
    // No `org.jetbrains.kotlin.android`: since AGP 9 the Android plugin brings Kotlin itself
    // (kvadrant-ui's research §1.13).
    alias(wip.plugins.composeCompiler)
    id("io.github.youndie.sborka.base")
    id("io.github.youndie.sborka.lint")
}

android {
    namespace = "io.github.youndie.oldge.sample.android"
    compileSdk =
        libs.versions.android.compileSdk
            .get()
            .toInt()

    defaultConfig {
        applicationId = "io.github.youndie.oldge.sample"
        minSdk =
            libs.versions.android.minSdk
                .get()
                .toInt()
        targetSdk =
            libs.versions.android.compileSdk
                .get()
                .toInt()
        versionCode = 1
        versionName = "0.1"
    }

    buildFeatures { compose = true }

    // A demo, installed by `installDebug` onto whatever is attached: no signing config, no release.
    buildTypes {
        getByName("debug") { isMinifyEnabled = false }
    }
}

dependencies {
    implementation(project(":sample"))
    implementation(project(":oldge-core"))
    implementation(libs.androidx.activity.compose)
}
