plugins {
    id("dev.isxander.modstitch.base") version "0.5.12"
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

modstitch {
    minecraftVersion = minecraft

    // Alternatively use stonecutter.eval if you have a lot of versions to target.
    // https://stonecutter.kikugie.dev/stonecutter/guide/setup#checking-versions
    javaTarget = when (minecraft) {
        "1.20.1" -> 17
        "1.21.4" -> 21
        else -> throw IllegalArgumentException("Please store the java version for $minecraft in build.gradle.kts!")
    }

    // If parchment doesnt exist for a version yet you can safely
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
                    "1.20.1" -> 15
                    "1.21.4" -> 46
                    else -> throw IllegalArgumentException("Please store the resource pack version for $minecraft in build.gradle.kts! https://minecraft.wiki/w/Pack_format")
                }.toString()
            )
        }
    }

    // Fabric Loom (Fabric)
    loom {
        // It's not recommended to store the Fabric Loader version in properties.
        // Make sure its up to date.
        fabricLoaderVersion = "0.16.10"

        // Configure loom like normal in this block.
        configureLoom {

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
                disableIdeRun()
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
}

// Stonecutter constants for mod loaders.
// See https://stonecutter.kikugie.dev/stonecutter/guide/comments#condition-constants
var constraint: String = name.split("-")[1]
stonecutter {
    consts(
        "fabric" to constraint.equals("fabric"),
        "neoforge" to constraint.equals("neoforge"),
        "forge" to constraint.equals("forge"),
    )
}

// All dependencies should be specified through modstitch's proxy configuration.
// Wondering where the "repositories" block is? Go to "stonecutter.gradle.kts"
// If you want to create proxy configurations for more source sets, such as client source sets,
// use the modstitch.createProxyConfigurations(sourceSets["client"]) function.
dependencies {
    modstitch.loom {
        modstitchModImplementation("net.fabricmc.fabric-api:fabric-api:0.112.0+1.21.4")
    }

    // Anything else in the dependencies block will be used for all platforms.
}