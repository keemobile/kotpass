@file:Suppress("UnstableApiUsage")

rootProject.name = "kotpass"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://jitpack.io")
    }
}

include(":kotpass")
