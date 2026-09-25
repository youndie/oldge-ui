plugins {
    alias(wip.plugins.kotlinMultiplatform)
    alias(wip.plugins.composeMultiplatform)
    alias(wip.plugins.composeCompiler)
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
        }
    }
}

compose.desktop {
    application {
        mainClass = "io.github.youndie.oldge.sample.MainKt"
    }
}
