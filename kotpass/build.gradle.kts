@file:Suppress("PropertyName")

import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.dokka)
    alias(libs.plugins.kover)
    alias(libs.plugins.maven.publish)
    id("java-library")
    id("maven-publish")
}

group = properties["GROUP"].toString()
version = properties["VERSION_NAME"].toString()

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<Jar> {
    manifest.attributes(
        mapOf(
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version
        )
    )
}

tasks.withType<DokkaTask>().configureEach {
    dokkaSourceSets {
        configureEach { includes.from("README.md") }
    }
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    testImplementation(libs.testing.kotest)
    testImplementation(libs.kotlin.reflect)

    implementation(libs.kotlin.xml.builder)
    implementation(libs.okio)
}
