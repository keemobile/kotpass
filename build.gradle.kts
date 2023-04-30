plugins {
    id("maven-publish")
    id("com.diffplug.spotless") version "5.11.1"
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
            ktlint(libs.versions.ktlint.get()).userData(
                mapOf("disabled_rules" to "no-wildcard-imports")
            )
        }
    }
}
