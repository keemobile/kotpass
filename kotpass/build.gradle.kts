@file:Suppress("PrivatePropertyName")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

private val ArtifactId = "kotpass"
private val ArtifactGroup = "app.keemobile"
private val ArtifactVersion = "0.4.12"

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.dokka)
    id("maven-publish")
}

group = ArtifactGroup
version = ArtifactVersion

kotlin {
    jvm()

    sourceSets {
        getByName("jvmMain") {
            dependencies {
                implementation(libs.kotlin.xml.builder)
                implementation(libs.okio)
            }
        }

        getByName("jvmTest") {
            dependencies {
                implementation(libs.testing.kotest)
                implementation(libs.kotlin.reflect)
            }
        }
    }
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<Test> {
    useJUnitPlatform()
}
