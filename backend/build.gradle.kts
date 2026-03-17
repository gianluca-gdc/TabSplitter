plugins {
    kotlin("jvm")
    application
    id("io.ktor.plugin") version "2.3.7"
    kotlin("plugin.serialization") version "2.1.20"
}

application {
    mainClass.set("com.gianluca_gdc.tabsplitter.backend.ApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.ktor.server.core.jvm)
    implementation(libs.ktor.server.netty.jvm)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.auth)
    implementation(libs.logback.classic)
    implementation(libs.libphonenumber)
}