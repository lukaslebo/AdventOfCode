plugins {
    kotlin("jvm") version "2.2.20"
    kotlin("plugin.serialization") version "2.2.20"
}

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")

    sourceSets {
        main {
            kotlin.srcDir("src")
        }
    }

    dependencies {
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
        implementation("tools.aqua:z3-turnkey:4.14.1")

        if (project.name != "common")
            implementation(project(":common"))
    }
}

tasks {
    wrapper {
        gradleVersion = "9.1.0"
    }
}
