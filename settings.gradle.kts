enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
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