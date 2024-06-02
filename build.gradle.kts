import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    alias(libs.plugins.spotless)
    alias(libs.plugins.versions)
    id("maven-publish")
    id("signing")
}

subprojects {
    apply {
        plugin("com.diffplug.spotless")
        plugin("maven-publish")
        plugin("signing")
    }

    spotless {
        kotlin {
            target("**/*.kt")
            targetExclude("$buildDir/**/*.kt")
            targetExclude("bin/**/*.kt")
            ktlint()
        }
    }

    signing {
        useGpgCmd()
        sign(publishing.publications)
    }
}

tasks.withType<DependencyUpdatesTask> {
    rejectVersionIf {
        val version = candidate.version.lowercase()

        listOf("-alpha", "-beta", "-rc")
            .any { it in version }
    }
}
