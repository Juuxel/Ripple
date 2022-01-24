plugins {
    `java-library`
    `java-gradle-plugin`
    `maven-publish`
    id("org.cadixdev.licenser")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8

    withSourcesJar()
}

repositories {
    mavenCentral()

    maven {
        name = "Fabric"
        url = uri("https://maven.fabricmc.net")
    }
}

dependencies {
    api(project(":"))
    implementation("net.fabricmc", "lorenz-tiny", "3.0.0")
}

tasks {
    jar {
        from(rootProject.file("LICENSE"))
    }
}

gradlePlugin {
    plugins {
        create("ripplePlugin") {
            id = "io.github.juuxel.ripple"
            implementationClass = "juuxel.ripple.gradle.RipplePlugin"
        }
    }
}

license {
    header(rootProject.file("HEADER.txt"))
}

afterEvaluate {
    publishing {
        publications.getByName<MavenPublication>("pluginMaven") {
            artifactId = base.archivesBaseName
        }
    }
}
