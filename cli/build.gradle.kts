plugins {
    java
    id("org.cadixdev.licenser")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenCentral()

    maven {
        name = "Fabric"
        url = uri("https://maven.fabricmc.net")
    }
}

dependencies {
    implementation(project(":"))
    implementation(group = "info.picocli", name = "picocli", version = "4.5.2")
    implementation(group = "org.cadixdev", name = "lorenz-io-enigma", version = "0.5.6")
    implementation(group = "net.fabricmc", name = "lorenz-tiny", version = "3.0.0")
}

tasks.jar {
    from(rootProject.file("LICENSE"))
    manifest.attributes("Main-Class" to "juuxel.ripple.cli.Main")
}

tasks.assemble {
    dependsOn(tasks.shadowJar)
}

license {
    header(rootProject.file("HEADER.txt"))
}
