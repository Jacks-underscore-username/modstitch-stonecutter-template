plugins {
    id("dev.kikugie.stonecutter") version "0.6-beta.2"
}
stonecutter active "1.21.1-neoforge"

stonecutter registerChiseled tasks.register("chiseledBuild", stonecutter.chiseled) {
    group = "project"
    ofTask("build")
}

//tasks.register("runCurrentClient") {
//    group = "runs"
//    dependsOn(":${stonecutter.current.project}:runClient")
//}
//tasks.register("runCurrentServer") {
//    group = "runs"
//    dependsOn(":${stonecutter.current.project}:runServer")
//}
//
//
//for (version in stonecutter.versions) {
//    tasks.register("${version.project}-runClient") {
//        group = "runs"
//        dependsOn("Set active project to ${version.project}")
//        dependsOn(":${version.project}:runClient")
//        tasks.getByPath(":${version.project}:runClient")
//            .mustRunAfter(tasks.getByName("Set active project to ${version.project}").path)
//    }
//}

allprojects {
    repositories {
        mavenCentral()
        mavenLocal()
        maven("https://maven.neoforged.net/releases")
        maven("https://maven.fabricmc.net/")
    }
}
