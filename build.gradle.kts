plugins {
    jacoco

    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.shadow)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.kotlinx.atomicfu)
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
    compileOnly(libs.spigot.api)

    implementation(libs.bundles.kotlinx.coroutines)
    implementation(libs.bundles.ktor)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.packetevents)

    testImplementation(libs.mojang.authlib)
    testImplementation(kotlin("test"))
    testImplementation(libs.bundles.logback)
    testImplementation(libs.ktor.client.mock)
    testImplementation(libs.bundles.kotlinx.coroutines.test)
}

tasks {
    build {
        dependsOn(shadowJar)
    }

    test {
        useJUnitPlatform()
        finalizedBy(jacocoTestReport)
    }

    jacocoTestReport {
        reports {
            xml.required.set(true)
            html.required.set(true)
        }

        dependsOn(test)
    }

    shadowJar {
        destinationDirectory.set(file("/home/woobb/Documents/Minecraft/spigot-1.21/plugins"))
        isEnableRelocation = true
        relocationPrefix = "me.woobb"
    }
}

kotlin {
    jvmToolchain(8)
    explicitApi()
}
