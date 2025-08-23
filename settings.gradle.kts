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

        // Parchment
        maven("https://maven.parchmentmc.org")
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.7.8"
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
            loaders.forEach { version("$name-$it", mcVersion) }

        // Configure your targets here!
        mc("1.21.8", loaders = listOf("fabric", "neoforge"))
//        mc("1.21.4", loaders = listOf("fabric", "neoforge"))
//        mc("1.21.1", loaders = listOf("fabric", "neoforge"))
//        mc("1.20.1", loaders = listOf("fabric", "forge"))
//        mc("1.19.2", loaders = listOf("fabric", "forge"))

        // This is the default target.
        vcsVersion = "1.21.8-fabric"
    }
}

rootProject.name = extra["cfg.name"] as String
