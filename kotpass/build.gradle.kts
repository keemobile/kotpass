@file:Suppress("PropertyName")

import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ArtifactId = "kotpass"
val ArtifactGroup = "app.keemobile"
val ArtifactVersion = "0.7.0"

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.dokka)
    alias(libs.plugins.kover)
    id("java-library")
    id("maven-publish")
}

group = ArtifactGroup
version = ArtifactVersion

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
    withSourcesJar()

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

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = ArtifactGroup
            artifactId = ArtifactId
            version = ArtifactVersion

            from(components["java"])
        }
    }
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
