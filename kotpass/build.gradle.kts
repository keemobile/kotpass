@file:Suppress("PropertyName")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ArtifactId = "kotpass"
val ArtifactGroup = "app.keemobile"
val ArtifactVersion = "0.4.12"

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.dokka)
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
        freeCompilerArgs += listOf("-Xopt-in=kotlin.RequiresOptIn")
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

tasks.test {
    useJUnitPlatform()
}

dependencies {
    testImplementation(libs.testing.kotest)
    testImplementation(libs.kotlin.reflect)

    implementation(libs.kotlin.xml.builder)
    implementation(libs.okio)
}
