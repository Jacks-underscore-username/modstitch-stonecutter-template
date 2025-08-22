plugins {
    id("dev.kikugie.stonecutter") version "0.7.8"
}
stonecutter active "1.21.8-neoforge"

allprojects {
    repositories {
        mavenCentral()
        mavenLocal()
        maven("https://maven.neoforged.net/releases")
        maven("https://maven.fabricmc.net/")
    }
}