plugins {
    id("net.neoforged.moddev") version "2.0.+"
    id("io.freefair.lombok") version "9.1.0"
}

base {
    archivesName = "${rootProject.property("archives_base_name")}-neoforge"
}

repositories {
    maven { url = uri("https://maven.uku3lig.net/releases") }
    maven { url = uri("https://maven.uku3lig.net/snapshots") }
}

neoForge {
    version = rootProject.property("neoforge_version") as String

    runs {
        register("client") {
            client()
        }
    }

    mods {
        register("stattier") {
            sourceSet(sourceSets.main.get())
            sourceSet(project(":common").sourceSets.main.get())
        }
    }
}

dependencies {
    implementation(project(":common"))
}

tasks.jar {
    from("../LICENSE_TierTagger") {
        rename { "${it}_${project.base.archivesName.get()}" }
    }
}
