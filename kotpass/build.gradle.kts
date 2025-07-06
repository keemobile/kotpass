@file:Suppress("PropertyName")

import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
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

tasks.withType<KotlinCompile> {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
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
    implementation(libs.okio)

    testImplementation(libs.testing.kotest)
    testImplementation(libs.kotlin.reflect)
    testImplementation(libs.junit.engine)
    testImplementation(kotlin("test-junit", libs.versions.kotlin.get()))
}
