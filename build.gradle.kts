plugins {
    id("java")
    id("io.freefair.lombok") version "9.1.0" apply false
}

subprojects {
    apply(plugin = "java")

    group = rootProject.property("maven_group") as String
    version = "${rootProject.property("mod_version")}+mc${rootProject.property("minecraft_version")}"

    java {
        sourceCompatibility = JavaVersion.VERSION_25
        targetCompatibility = JavaVersion.VERSION_25
    }

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release = 25
    }
}
