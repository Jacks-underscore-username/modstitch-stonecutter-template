pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()

        // Modstitch
        maven("https://maven.isxander.dev/releases/")

        // Loom platform
        maven("https://maven.fabricmc.net/")

        // MDG platform
        maven("https://maven.neoforged.net/releases/")

        // Stonecutter
        maven("https://maven.kikugie.dev/releases")
        maven("https://maven.kikugie.dev/snapshots")

        // Modstitch
        maven("https://maven.isxander.dev/releases")
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.7-alpha.6"
}

stonecutter {
    kotlinController = true
    centralScript = "build.gradle.kts"

    create(rootProject) {
        /**
         * @param mcVersion The base minecraft version.
         * @param loaders A list of loaders to target, supports "fabric" (1.14+), "neoforge"(1.20.6+), "vanilla"(any) or "forge"(<=1.20.1)
         */
        fun mc(mcVersion: String, name: String = mcVersion, loaders: Iterable<String>) =
            loaders.forEach { vers("$name-$it", mcVersion) }

        // Configure your targets here!
        mc("1.21.1", loaders = listOf("fabric", "neoforge"))
        mc("1.20.1", loaders = listOf("fabric", "forge"))
        mc("1.19.2", loaders = listOf("fabric", "forge"))

        // This is the default target.
        vcsVersion = "1.21.1-neoforge"
    }
}

rootProject.name = extra["cfg.name"] as String
