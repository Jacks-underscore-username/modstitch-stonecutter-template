plugins {
    id("dev.kikugie.stonecutter") version "0.7-alpha.6"
}
stonecutter active "1.21.1-neoforge"

allprojects {
    repositories {
        mavenCentral()
        mavenLocal()
        maven("https://maven.neoforged.net/releases")
        maven("https://maven.fabricmc.net/")
    }
}