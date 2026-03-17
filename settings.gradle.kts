enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {

    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0" // Use the latest version
}

gradle.settingsEvaluated {
    pluginManagement {
        repositories {
            gradlePluginPortal()
            google()
            mavenCentral()
        }
    }
}
rootProject.name = "Tabsplitter"
include(":androidApp", ":shared", ":backend")
project(":backend").projectDir = File(rootDir, "backend")