import xyz.jpenilla.runtask.task.AbstractRun

plugins {
    kotlin("jvm") version "2.3.0"
    id("com.gradleup.shadow") version "9.3.1"
    id("io.github.revxrsal.bukkitkobjects") version "0.0.5"
    id("xyz.jpenilla.run-paper") version "3.0.2"
}

bukkitKObjects {
    classes.add("gg.aquatic.eventsmania.EventsMania")
}

group = "gg.aquatic.eventsmania"
version = "26.0.1"

repositories {
    mavenCentral()
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        name = "aquatic-releases"
        url = uri("https://repo.nekroplex.com/releases")
    }
    maven("https://jitpack.io")
    maven("https://mvn.lumine.io/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

val exposedVersion = "0.61.0"
dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    compileOnly("gg.aquatic:Waves:26.0.12")
    compileOnly("me.clip:placeholderapi:2.11.7")
    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    testImplementation(kotlin("test"))

    compileOnly("org.jetbrains.exposed:exposed-core:$exposedVersion")
    compileOnly("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    compileOnly("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    compileOnly("com.github.ben-manes.caffeine:caffeine:3.2.3")
    compileOnly("com.zaxxer:HikariCP:7.0.2")
}

kotlin {
    jvmToolchain(21)
}

tasks {
    runServer {
        minecraftVersion("1.21.11")
    }

    build {
        dependsOn(shadowJar)
    }

    test {
        useJUnitPlatform()
    }

    build {
        dependsOn(shadowJar)
    }
}
tasks.withType(AbstractRun::class) {
    javaLauncher = javaToolchains.launcherFor {
        vendor = JvmVendorSpec.JETBRAINS
        languageVersion = JavaLanguageVersion.of(21)
    }
    jvmArgs("-XX:+AllowEnhancedClassRedefinition")
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    fun reloc(pkg: String) {
        relocate(pkg, "gg.aquatic.waves.dependency.$pkg")
    }

    archiveFileName.set("EventsMania2-${project.version}.jar")
    archiveClassifier.set("")

    dependencies {
        exclude(dependency("org.jetbrains.kotlin:.*:.*"))
        exclude(dependency("org.jetbrains.kotlinx:.*:.*"))
        exclude(dependency("org.jetbrains:annotations:.*"))
        exclude(dependency("com.intellij:annotations:.*"))

        exclude(dependency("net.kyori:adventure-api:.*"))
        exclude(dependency("org.javassist:javassist:.*"))
        exclude(dependency("javax.annotation:javax.annotation-api:.*"))
        exclude(dependency("com.google.code.findbugs:jsr305:.*"))
        exclude(dependency("org.slf4j:.*:.*"))
    }

    reloc("kotlinx")
    reloc("org.jetbrains.kotlin")
    reloc("kotlin")
    reloc("org.bstats")
    reloc("com.zaxxer.hikari")
}

