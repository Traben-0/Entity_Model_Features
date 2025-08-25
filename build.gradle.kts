
//import gg.essential.gradle.multiversion.StripReferencesTransform.Companion.registerStripReferencesAttribute
import gg.essential.gradle.util.*
//import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    // If you're using Kotlin, it needs to be applied before the multi-version plugin
   // kotlin("jvm")

    // Apply the multi-version plugin, this does all the configuration necessary for the preprocessor to
    // work. In particular it also applies `com.replaymod.preprocess`.
    // In addition it primarily also provides a `platform` extension which you can use in this build script
    // to get the version and mod loader of the current project.
    id("gg.essential.multi-version")
    // If you do not care too much about the details, you can just apply essential-gradle-toolkits' defaults for
    // Minecraft, fabric-loader, forge, mappings, etc. versions.
    // You can also overwrite some of these if need be. See the `gg.essential.defaults.loom` README section.
    // Otherwise you'll need to configure those as usual for (architectury) loom.
    id("gg.essential.defaults")
}

//tasks.compileKotlin.setJvmDefault("all")
//tasks.withType<KotlinCompile> {
//    kotlinOptions {
//        languageVersion = "1.9"
//        apiVersion = "1.9"
//    }
//}

repositories {
    mavenCentral()
    maven("https://api.modrinth.com/maven")
    maven("https://mvnrepository.com/artifact/com.demonwav.mcdev/annotations")
    maven("https://maven.terraformersmc.com/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
}

val mcVersion = platform.mcVersion
val modVersion = properties["mod_version"].toString()

base.archivesName.set("entity_model_features-$modVersion-${project.name}")

// todo figure out why preprocessor wont work with these
val accessWidener = "entity_model_features_" + when {
    mcVersion >= 12106 -> 11
    mcVersion >= 12105 -> 10
    mcVersion >= 12104 -> 9
    mcVersion >= 12102 -> 8
    mcVersion >= 12100 -> 7
    mcVersion >= 12006 -> 6
    mcVersion >= 12004 -> 5
    mcVersion >= 12002 -> 4
    mcVersion >= 12001 -> 3
    else -> throw IllegalStateException("Unsupported version: $mcVersion")
} + ".accesswidener"

//val common = registerStripReferencesAttribute("common") {
//    excludes.add("net.minecraft")
//}

dependencies {
    // If you are depending on a multi-version library following the same scheme as the Essential libraries (that is
    // e.g. `elementa-1.8.9-forge`), you can `toString` `platform` directly to get the respective artifact id.
//    api("gg.essential:elementa:710")


    // universalcraft neoforge is just forge currently
//    val ucVer =
//        if(platform.isNeoForge) platform.toString().replace("neo","")
//        else platform.toString()
//
//    compileOnly("gg.essential:universalcraft-$ucVer:415"){
//        // Setting the attribute to `true` will cause the transformer to apply to this specific artifact
//        attributes { attribute(common, true) }
//    }
//    compileOnly("gg.essential:elementa:710"){
//        attributes { attribute(common, true) }
//    }
//    compileOnly("gg.essential:vigilance:306"){
//        attributes { attribute(common, true) }
//        exclude(group = "gg.essential", module = "elementa")
//    }
//
//    implementation(include("gg.essential:elementa:710")!!)
//    implementation(include("gg.essential:universalcraft-$ucVer:415")!!)
//    implementation(include("gg.essential:vigilance:306")!!)



    fun modImpl(modPrefix: String, vararg versions: Pair<Int, String?>) {
        for ((versionMC, versionMod) in versions) {
            if (platform.mcVersion >= versionMC) {
                if (versionMod != null) {
                    modImplementation("$modPrefix$versionMod") {
                        exclude("net.fabricmc.fabric-api")
                        isTransitive = true
                    }
                }
                break
            }
        }
    }

    fun ver(fabric: String?, forge: String?, neoforge: String?): String?  = when {
        platform.isFabric -> fabric
        platform.isForge -> forge
        else -> neoforge
    }

    // if you are cloning the EMF repo, you will also want to clone and build the ETF repo next to it
    // otherwise you can copy the things I do below for other external mods and go find all the ETF version ids on Modrinth
    val etf = "entity_texture_features-${properties["etf_version"]}-${project.name}"
    modImplementation(files(File(rootDir.parent, "Entity_Texture_Features/jars/$etf.jar")))

//    modImpl("maven.modrinth:ebe:",
//        12104 to "YokFoILZ",
//        12102 to "fOVHsM6M",
//        12100 to "HBZAPs3u",
//        )

    modImpl("maven.modrinth:iris:",
        12105 to "N0ln8GKQ",
        12100 to "kuOV4Ece",
        12006 to "1bvcmYOc",
        12004 to "hq98tuSS",
        12002 to "Cjwm9s3i",
        12000 to ver("s5eFLITc", null,  null),
        )
    modImpl("maven.modrinth:oculus:",
        12002 to null,
        12000 to ver(null, "iQ1SwGc3", null),
    )
    modImpl("maven.modrinth:modmenu:",
        12105 to "R7uVB42W",
        12102 to "PcJvQYqu",
        12100 to "9FL4cmP7",
        12006 to "mtTzRMV2",
        12004 to "sjtVVlsA",
        12002 to "TwfjidT5",
        12000 to "RTFDnTKf",
        )

    if (platform.isNeoForge && mcVersion < 12002) { // NeoForge 20.2.84+ added it themselves
        include("io.github.llamalad7:mixinextras-neoforge:0.4.1:slim")
    }
    if (platform.isForge) {
        compileOnly(annotationProcessor("io.github.llamalad7:mixinextras-common:0.4.1")!!)
        implementation(include("io.github.llamalad7:mixinextras-forge:0.4.1")!!)
    }

    implementation("com.demonwav.mcdev:annotations:2.1.0")

}

tasks.processResources {
    // Expansions are already set up for `version` (or `file.jarVersion`) and `mcVersionStr`.
    // You do not need to set those up manually.
}

loom {
    // If you need to use a tweaker on legacy (1.12.2 and below) forge:
//    if (platform.isLegacyForge) {
//        launchConfigs.named("client") {
//            arg("--tweakClass", "gg.essential.loader.stage0.EssentialSetupTweaker")
//            // And maybe a core mod?
//            property("fml.coreMods.load", "com.example.asm.CoreMod")
//        }
//    }
//    // Mixin on forge? (for legacy forge you will still need to register a tweaker to set up mixin)



    accessWidenerPath = project.parent!!.file("src/main/resources/$accessWidener")
    if (isForge) {
        forge {
            mixinConfig("entity_model_features.mixins.json")
            // And maybe an access transformer?
            // Though try to avoid these, cause they are not automatically translated to Fabric's access widener
            //accessTransformer(project.parent!!.file("src/main/resources/entity_model_features.access"))
            convertAccessWideners = true

        }
    }
    if (isNeoForge) {
        neoForge {
        }
    }
}

loom.noServerRunConfigs()

tasks.remapJar {
    injectAccessWidener = true
    if (!platform.isFabric) atAccessWideners.add(accessWidener)
}

tasks.processResources {
    inputs.property("project_version", modVersion)
    filesMatching("fabric.mod.json") {
        expand(mapOf("version" to modVersion))
    }
    filesMatching("META-INF/mods.toml") {
        if (platform.isNeoForge || platform.isFabric) {
            exclude()
        } else {
            expand(mapOf("version" to modVersion))
        }
    }
    filesMatching("META-INF/neoforge.mods.toml") {
        if (platform.isFabric || platform.isForge) {
            exclude()
        } else {
            expand(mapOf("version" to modVersion))
            if (platform.isNeoForge && platform.mcVersion < 12005) {
                // NeoForge still uses the old mods.toml name until 1.20.5
                name = "mods.toml"
            }
        }
    }
    filesMatching("entity_model_features_*.accesswidener") {
        if (this.name != accessWidener) this.exclude()
    }
}

tasks.register<Copy>("copyArtifacts") {
    from(layout.buildDirectory.dir("libs").get())
    into("${rootDir}\\jars")
    mustRunAfter(tasks.build)
    delete(layout.buildDirectory.dir("libs").get())
}


tasks.build {
    finalizedBy("copyArtifacts")
}