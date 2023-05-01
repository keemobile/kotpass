plugins {
    id("maven-publish")
    id("com.diffplug.spotless") version "6.18.0"
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
