plugins {
    kotlin("jvm")
    application
    id("io.ktor.plugin") version "2.3.7"
}

application {
    mainClass.set("com.gianluca_gdc.tabsplitter.backend.ApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:2.3.7")
    implementation("io.ktor:ktor-server-netty-jvm:2.3.7")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.7")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.7")
    implementation("io.ktor:ktor-client-core:2.3.7")
    implementation("io.ktor:ktor-client-cio:2.3.7")
    implementation("io.ktor:ktor-client-auth:2.3.7")
    implementation("ch.qos.logback:logback-classic:1.4.11")
}