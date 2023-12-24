plugins {
    kotlin("jvm") version "1.9.21"
    kotlin("plugin.serialization") version "1.9.21"
}

sourceSets {
    main {
        kotlin.srcDir("src")
    }
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0-RC")
    implementation("tools.aqua:z3-turnkey:4.12.2.1")
}

tasks {
    wrapper {
        gradleVersion = "8.5"
    }
}
