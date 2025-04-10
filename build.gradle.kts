import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.1.20"
    id("dev.isxander.modstitch.base") version "0.5.15-unstable"
    id("dev.kikugie.stonecutter") version "0.7-alpha.6"
}

fun cfg(name: String): String {
    return property("cfg.${name}") as String
}

val isFabric = property("modstitch.platform") == "loom"
val isForge = property("modstitch.platform") == "moddevgradle-legacy"
val isNeoforge = property("modstitch.platform") == "moddevgradle-regular"

val minecraft = cfg("minecraft")

val mod_name = cfg("name")
val mod_id = cfg("id")
val mod_version = cfg("version")
val mod_author = cfg("author")
val mod_group = if (cfg("group") == "") "com.$mod_author.$mod_id" else cfg("group")
val mod_description = cfg("description")
val mod_license = cfg("license")

val java_version =
    if (stonecutter.eval(minecraft, ">=1.20.5")) 21 else 17


tasks {
    named<ProcessResources>("generateModMetadata") {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        dependsOn("stonecutterGenerate")
    }

    named("compileKotlin") {
        dependsOn("stonecutterGenerate")
    }

    processResources {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }
}

modstitch {
    minecraftVersion = minecraft

    javaTarget = java_version

    // If parchment doesn't exist for a version yet you can safely
    // omit the "deps.parchment" property from your versioned gradle.properties
//    parchment {
//        if (isFabric) {
//            mappingsVersion = cfg("parchment")
//        }
////        prop("deps.parchment") { mappingsVersion = it }
//    }

    // This metadata is used to fill out the information inside
    // the metadata files found in the templates folder.
    metadata {
        modId = mod_id
        modName = mod_name
        modVersion = mod_version
        modGroup = mod_group
        modAuthor = mod_author
        modLicense = mod_license

        fun <K, V> MapProperty<K, V>.populate(block: MapProperty<K, V>.() -> Unit) {
            block()
        }

        replacementProperties.populate {
            // You can put any other replacement properties/metadata here that
            // modstitch doesn't initially support. Some examples below.
            put(
                "pack_format", when (minecraft) {
                    "1.19.2" -> 9
                    "1.20.1" -> 15
                    "1.21.1" -> 34
                    else -> throw IllegalArgumentException("Please store the resource pack version for $minecraft in build.gradle.kts! https://minecraft.wiki/w/Pack_format")
                }.toString()
            )
            put("minecraft", minecraft)
            put("forge", if (isForge) cfg("forge") else "")
        }
    }

    // Fabric Loom (Fabric)
    if (isFabric) loom {
        fabricLoaderVersion = cfg("loader")

        // Configure loom like normal in this block.
        configureLoom {
            runConfigs.configureEach {
                ideConfigGenerated(true)
                vmArgs.add("-Dmixin.debug.export=true")
            }
        }
    }

    // ModDevGradle (NeoForge, Forge, Forgelike)
    moddevgradle {
        enable {
            if (isModDevGradleLegacy) forgeVersion = "${minecraft}-${cfg("forge")}"
            if (isModDevGradleRegular) neoForgeVersion = cfg("neoforge")
        }

        // Configures client and server runs for MDG, it is not done by default
        defaultRuns()

        // This block configures the `neoforge` extension that MDG exposes by default,
        // you can configure MDG like normal from here
        configureNeoforge {
            runs.all {
                jvmArguments.add("-Dmixin.debug.export=true")
            }
        }
    }

    mixin {
        // You do not need to specify mixins in any mods.json/toml file if this is set to
        // true, it will automatically be generated.
        addMixinsToModManifest = true

        configs.register(mod_id)

//        if (isFabric) configs.register("$mod_id-fabric")
//        if (isForge) configs.register("$mod_id-forge")
//        if (isNeoforge) configs.register("$mod_id-neoforge")
    }

    kotlin {
        jvmToolchain(java_version)
        compilerOptions.jvmTarget.set(JvmTarget.valueOf("JVM_$java_version"))
    }
}

java {
    withSourcesJar()
    targetCompatibility = JavaVersion.valueOf("VERSION_$java_version")
    sourceCompatibility = JavaVersion.valueOf("VERSION_$java_version")
}

// Stonecutter constants for mod loaders.
val constraint: String = name.split("-")[1]
stonecutter {
    consts(
        "fabric" to (constraint == "fabric"),
        "neoforge" to (constraint == "neoforge"),
        "forge" to (constraint == "forge"),
    )
}

// All dependencies should be specified through modstitch's proxy configuration.
// Wondering where the "repositories" block is? Go to "stonecutter.gradle.kts"
// If you want to create proxy configurations for more source sets, such as client source sets,
// use the modstitch.createProxyConfigurations(sourceSets["client"]) function.
dependencies {
    modstitch.loom {
        if (isFabric) modstitchModImplementation("net.fabricmc.fabric-api:fabric-api:${cfg("fabric")}+${minecraft}")
    }

    // Anything else in the dependencies block will be used for all platforms.
}