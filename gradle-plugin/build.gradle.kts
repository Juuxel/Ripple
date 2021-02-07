import com.jfrog.bintray.gradle.BintrayExtension

plugins {
    `java-library`
    `java-gradle-plugin`
    `maven-publish`
    id("org.cadixdev.licenser")
    id("com.jfrog.bintray")
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
    header = rootProject.file("HEADER.txt")
}

afterEvaluate {
    publishing {
        publications.getByName<MavenPublication>("pluginMaven") {
            artifactId = base.archivesBaseName
        }
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
        name = "ripple-gradle"
        setLicenses("MPL-2.0")
        vcsUrl = "https://github.com/Juuxel/Ripple"

        version(closureOf<BintrayExtension.VersionConfig> {
            name = project.version.toString()
        })
    })

    afterEvaluate {
        setPublications(*publishing.publications.map { it.name }.toTypedArray())
    }
}
