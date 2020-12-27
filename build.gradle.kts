import com.jfrog.bintray.gradle.BintrayExtension

plugins {
    `java-library`
    `maven-publish`
    id("org.cadixdev.licenser") version "0.5.0"
    id("com.jfrog.bintray") version "1.8.5"
}

base {
    archivesBaseName = "ripple"
}

group = "io.github.juuxel"
version = "0.1.0"

if (file("private.gradle").exists()) {
    apply(from = "private.gradle")
}

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
        artifactId = "ripple"

        from(components.getByName("java"))
    }
}

bintray {
    if (project.hasProperty("bintrayUser")) {
        user = project.property("bintrayUser").toString()
        key = project.property("bintrayKey").toString()
    } else {
        println("'bintrayUser' not found -- please set up 'bintrayUser' and 'bintrayKey' before publishing")
    }

    pkg(closureOf<BintrayExtension.PackageConfig> {
        repo = "maven"
        name = "ripple"
        setLicenses("MPL-2.0")
        vcsUrl = "https://github.com/Juuxel/Ripple"

        version(closureOf<BintrayExtension.VersionConfig> {
            name = project.version.toString()
        })
    })

    setPublications("maven")
}
