plugins {
    id("dev.kikugie.stonecutter") version "0.7.8"
}
stonecutter active "1.21.8-fabric"

allprojects {
    repositories {
        mavenCentral()
        mavenLocal()
        maven("https://maven.neoforged.net/releases")
        maven("https://maven.fabricmc.net/")
        maven("https://maven.parchmentmc.org")
    }
}