plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.android)
}

repositories {
    google()
    mavenCentral()
}

android {
    namespace = "com.gianluca_gdc.tabsplitter.android"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.gianluca_gdc.tabsplitter.android"
        minSdk = 24
        targetSdk = 36
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
    implementation(libs.compose.runtime)
    implementation(libs.compose.foundation)
    implementation(libs.compose.components.resources)
    implementation(libs.compose.material)

    implementation(libs.compose.material)
    implementation(libs.android.material)

    implementation(libs.ui.tooling.preview.android)
    implementation(libs.glance.preview)
    implementation(libs.room.runtime.android)
    implementation(libs.firebase.crashlytics.buildtools)
    implementation(libs.ktor.client.core.v234)
    implementation(libs.ktor.client.cio.v234)
    implementation(libs.ktor.client.content.negotiation.v234)
    implementation(libs.ktor.serialization.kotlinx.json.v234)
    implementation(libs.text.recognition)
    implementation(libs.datastore.preferences)
    implementation(libs.core.ktx)
}

