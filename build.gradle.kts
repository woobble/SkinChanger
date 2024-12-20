plugins {
    jacoco

    alias(libs.plugins.kotlin.jvm)
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
    compileOnly(libs.packetevents)

    implementation(libs.bundles.kotlinx.coroutines)
    implementation(libs.bundles.ktor)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.mojang.authlib)
    testImplementation(kotlin("test"))
    testImplementation(libs.bundles.logback)
    testImplementation(libs.ktor.client.mock)
    testImplementation(libs.bundles.kotlinx.coroutines.test)
}

tasks {
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
}

kotlin {
    jvmToolchain(8)
    explicitApi()
}
