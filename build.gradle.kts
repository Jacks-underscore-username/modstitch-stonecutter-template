import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.HashMap

plugins {
    kotlin("jvm") version "2.1.20"
    id("dev.isxander.modstitch.base") version "0.7.0-unstable"
    id("dev.kikugie.stonecutter") version "0.7.8"
}

fun cfg(name: String): String {
    return try {
        property("cfg.${name}") as String
    } catch (e: Exception) {
        ""
    }
}
val config = HashMap<String, String>()

val is_fabric = property("modstitch.platform") == "loom"
config["is_fabric"] = is_fabric.toString()
val is_forge = property("modstitch.platform") == "moddevgradle-legacy"
config["is_forge"] = is_forge.toString()
val is_neoforge = property("modstitch.platform") == "moddevgradle-regular"
config["is_neoforge"] = is_neoforge.toString()

val minecraft_version = cfg("minecraft")
config["minecraft_version"] = minecraft_version
val mod_name = cfg("name")
config["mod_name"] = mod_name
val mod_id = cfg("id")
config["mod_id"] = mod_id
val mod_version = cfg("version")
config["mod_version"] = mod_version
val mod_author = cfg("author")
config["mod_author"] = mod_author
val mod_group = if (cfg("group") == "") "com.$mod_author.$mod_id" else cfg("group")
config["mod_group"] = mod_group
val mod_description = cfg("description")
config["mod_description"] = mod_description
val mod_license = cfg("license")
config["mod_license"] = mod_license
val loader: String = name.split("-")[1]
config["loader"] = loader
val fabric_loader = cfg("loader")
config["fabric_loader"] = fabric_loader
val fabric_version = cfg("fabric")
config["fabric_version"] = fabric_version
val forge_version = cfg("forge")
config["forge_version"] = forge_version
val neoforge_version = cfg("neoforge")
config["neoforge_version"] = neoforge_version
val java_version = if (stonecutter.eval(minecraft_version, ">=1.20.5")) 21 else 17
config["java_version"] = java_version.toString()

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
    minecraftVersion = minecraft_version

    // If parchment doesn't exist for a version yet you can safely
    // omit the "deps.parchment" property from your versioned gradle.properties
    parchment {
//        if (isFabric) {
//            mappingsVersion = cfg("parchment")
//        }
//        mappingsVersion = cfg("parchment")
    }

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
            put(
                "pack_format", when (minecraft_version) {
                    "1.16.5" -> 6
                    "1.18.2" -> 9
                    "1.19.2" -> 10
                    "1.20.1" -> 15
                    "1.21.1" -> 34
                    "1.21.4" -> 46
                    "1.21.8" -> 64
                    else -> throw IllegalArgumentException("Please store the resource pack version for $minecraft_version in build.gradle.kts! https://minecraft.wiki/w/Pack_format")
                }.toString()
            )
            for (entry in config.entries)
                put(entry.key, entry.value)
        }
    }

    // Fabric Loom (Fabric)
    if (is_fabric) loom {
        fabricLoaderVersion = fabric_loader

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
            if (isModDevGradleLegacy) forgeVersion = "${minecraft_version}-${forge_version}"
            if (isModDevGradleRegular) neoForgeVersion = neoforge_version

        // Configures client and server runs for MDG, it is not done by default
        defaultRuns()

        configureNeoForge {
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

// Add loads of info to stonecutter for use in the actual code
stonecutter {
    // Makes the loader comparable for stonecutter comments
    constants.match(
        loader,
        "fabric",
        "neoforge",
        "forge",
    )

    // Adds swaps so the java side can see all the config if needed
    fun swap (name: String, value: String){
        swaps["${name}_string"] = "\"${value}\""
        swaps[name]=value
    }
    for (entry in config.entries)
        swap(entry.key, entry.value)
}

// All dependencies should be specified through modstitch's proxy configuration.
// Wondering where the "repositories" block is? Go to "stonecutter.gradle.kts"
// If you want to create proxy configurations for more source sets, such as client source sets,
// use the modstitch.createProxyConfigurations(sourceSets["client"]) function.
dependencies {
    modstitch.loom {
        if (is_fabric)
            modstitchModImplementation("net.fabricmc.fabric-api:fabric-api:${cfg("fabric")}+${minecraft_version}")
        if (is_neoforge)
            modstitchModImplementation("net.neoforged:neoforge:${neoforge_version}")
    }
}