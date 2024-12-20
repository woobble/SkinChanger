plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.shadow)
}

group = "me.woobb.skinchanger"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()

    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")

    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/repositories/central")

    maven("https://libraries.minecraft.net/")

    maven("https://repo.codemc.io/repository/maven-releases/")
}

dependencies {
    implementation(project(":"))
    implementation(libs.bundles.kotlinx.coroutines)

    compileOnly(libs.spigot.api)
    compileOnly(libs.packetevents)
}

tasks {
    build {
        dependsOn(shadowJar)
    }
}

kotlin {
    jvmToolchain(8)
}
