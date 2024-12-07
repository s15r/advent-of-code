plugins {
    kotlin("jvm") version "2.0.21"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

sourceSets {
    main {
        kotlin.setSrcDirs(listOf("src"))
    }
}

tasks {
    wrapper {
        gradleVersion = "8.11"
    }
}

tasks.test {
    useJUnitPlatform()
}
