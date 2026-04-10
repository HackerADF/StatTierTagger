plugins {
    id("fabric-loom") version "1.9-SNAPSHOT"
    id("io.freefair.lombok") version "9.1.0"
}

base {
    archivesName = "${rootProject.property("archives_base_name")}-fabric"
}

repositories {
    maven { url = uri("https://maven.uku3lig.net/releases") }
    maven { url = uri("https://maven.uku3lig.net/snapshots") }
    maven {
        name = "Modrinth"
        url = uri("https://api.modrinth.com/maven")
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${rootProject.property("minecraft_version")}")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${rootProject.property("loader_version")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${rootProject.property("fabric_version")}")

    modImplementation("net.uku3lig:ukulib:${rootProject.property("ukulib_version")}")

    implementation(project(":common"))
}

tasks.processResources {
    inputs.property("version", project.version)
    filteringCharset = "UTF-8"

    filesMatching("fabric.mod.json") {
        expand("version" to project.version)
    }
}

tasks.jar {
    from("../LICENSE_TierTagger") {
        rename { "${it}_${project.base.archivesName.get()}" }
    }
}
