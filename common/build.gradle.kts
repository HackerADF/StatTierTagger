plugins {
    id("fabric-loom") version "1.9-SNAPSHOT"
    id("io.freefair.lombok") version "9.1.0"
}

repositories {
    maven { url = uri("https://maven.uku3lig.net/releases") }
    maven { url = uri("https://maven.uku3lig.net/snapshots") }
}

dependencies {
    minecraft("com.mojang:minecraft:${rootProject.property("minecraft_version")}")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${rootProject.property("loader_version")}")

    modImplementation("net.uku3lig:ukulib:${rootProject.property("ukulib_version")}")
}
