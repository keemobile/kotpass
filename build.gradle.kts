import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    id("maven-publish")
    id("com.diffplug.spotless") version "6.18.0"
    id("com.github.ben-manes.versions") version "0.46.0"
}

subprojects {
    apply {
        plugin("com.diffplug.spotless")
    }

    spotless {
        kotlin {
            target("**/*.kt")
            targetExclude("$buildDir/**/*.kt")
            targetExclude("bin/**/*.kt")
            ktlint(libs.versions.ktlint.get())
        }
    }
}

tasks.withType<DependencyUpdatesTask> {
    rejectVersionIf {
        val version = candidate.version.lowercase()

        listOf("-alpha", "-beta", "-rc")
            .any { it in version }
    }
}
