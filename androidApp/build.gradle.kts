plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.compose.compiler)
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.gianluca_gdc.tabsplitter.android"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.gianluca_gdc.tabsplitter.android"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(project(":shared"))
    implementation(libs.activity.compose)
    implementation(libs.compose.material3)
    implementation("com.google.android.material:material:1.9.0")
    implementation(libs.ui.tooling.preview.android)
    implementation(libs.glance.preview)
    implementation(libs.room.runtime.android)
    implementation(libs.firebase.crashlytics.buildtools)
    implementation("io.ktor:ktor-client-core:2.3.4")
    implementation("io.ktor:ktor-client-cio:2.3.4")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.4")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.4")

}