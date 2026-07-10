import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    id("com.gradleup.shadow") version "8.3.6"
}

group = "me.imdanix"
version = "3.6.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.helpch.at/releases/")
    maven("https://maven.enginehub.org/repo/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.3-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.12.2")
    compileOnly("net.luckperms:api:5.4")

    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.13") {
        exclude(group = "com.sk89q.worldedit", module = "worldedit-bukkit")
        exclude(group = "io.papermc", module = "paperlib")
        exclude(group = "com.sk89q", module = "commandbook")
        exclude(group = "org.bstats", module = "bstats-bukkit")
    }
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.3.10") {
        exclude(group = "io.papermc", module = "paperlib")
        exclude(group = "org.bstats", module = "bstats-bukkit")
    }

    implementation("org.bstats:bstats-bukkit:3.2.1")

    // jetbrains-annotations is provided by paper-api at compile time but needed explicitly in tests
    testImplementation("org.jetbrains:annotations:26.0.2")
    // fastutil is a transitive dep of WorldEdit; needed for test compilation against ModrinthUpdater
    testImplementation("it.unimi.dsi:fastutil:8.5.15")

    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.11.4")
    testImplementation("org.assertj:assertj-core:3.27.7")
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    // Replace ${pluginVersion} in plugin.yml (replaces Maven's ${project.version} filtering)
    filesMatching("plugin.yml") {
        expand(mapOf("pluginVersion" to version))
    }
}

tasks.named<ShadowJar>("shadowJar") {
    archiveFileName = "WGExtenderX-${version}.jar"
    minimize()
    relocate("org.bstats", "wgextender.external.metrics")
    // Exclude META-INF/MANIFEST.MF entries from shaded deps to suppress warnings
    exclude("META-INF/MANIFEST.MF")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

// Make the default 'build' task produce the shaded jar
tasks.named("assemble") {
    dependsOn("shadowJar")
}
