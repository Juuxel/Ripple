plugins {
    `java-library`
    `maven-publish`
    id("org.cadixdev.licenser") version "0.5.0"
}

group = "io.github.juuxel"
version = "0.1.0"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8

    withJavadocJar()
    withSourcesJar()
}

repositories {
    mavenCentral()
}

dependencies {
    api(group = "org.cadixdev", name = "lorenz", version = "0.5.6")
    api(group = "org.jetbrains", name = "annotations", version = "20.1.0")
}

tasks.jar {
    from(file("LICENSE"))
}

license {
    header = file("HEADER.txt")
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components.getByName("java"))
    }
}
