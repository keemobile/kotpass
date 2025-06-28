import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.dokka)
    alias(libs.plugins.kover)
    id("java-library")
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
	compileOnly(kotlin("reflect", libs.versions.kotlin.get()))

	implementation(libs.apache.commons.lang)

    testImplementation(libs.testing.kotest)
	testImplementation(kotlin("reflect", libs.versions.kotlin.get()))
	testImplementation(kotlin("test-junit", libs.versions.kotlin.get()))
}
