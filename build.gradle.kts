import com.jfrog.bintray.gradle.BintrayExtension

plugins {
    `java-library`
    `maven-publish`
    signing
    id("org.cadixdev.licenser") version "0.5.0"
    id("com.jfrog.bintray") version "1.8.5"
}

base {
    archivesBaseName = "ripple"
}

allprojects {
    apply(plugin = "signing")

    group = "io.github.juuxel.ripple"
    version = "0.4.1"

    if (rootProject.file("private.gradle").exists()) {
        apply(from = rootProject.file("private.gradle"))
    }

    afterEvaluate {
        if (plugins.hasPlugin("maven-publish")) {
            publishing {
                publications.withType<MavenPublication> {
                    pom {
                        name.set(base.archivesBaseName)
                        url.set("https://github.com/Juuxel/Ripple")

                        licenses {
                            license {
                                name.set("Mozilla Public License Version 2.0")
                                url.set("https://www.mozilla.org/en-US/MPL/2.0/")
                            }
                        }

                        developers {
                            developer {
                                id.set("Juuxel")
                                name.set("Juuxel")
                                email.set("juuzsmods@gmail.com")
                            }
                        }

                        scm {
                            connection.set("scm:git:git://github.com/Juuxel/Ripple.git")
                            developerConnection.set("scm:git:ssh://github.com:Juuxel/Ripple.git")
                            url.set("https://github.com/Juuxel/Ripple")
                        }
                    }
                }

                repositories {
                    if (project.hasProperty("artifactoryUsername")) {
                        maven {
                            name = "Cotton"
                            url = uri("https://server.bbkr.space/artifactory/libs-release")

                            credentials {
                                username = project.property("artifactoryUsername").toString()
                                password = project.property("artifactoryPassword").toString()
                            }
                        }
                    } else {
                        println("Cannot configure artifactory; please define ext.artifactoryUsername and ext.artifactoryPassword before running artifactoryPublish")
                    }
                }
            }

            if (project.hasProperty("signing.keyId")) {
                signing {
                    sign(publishing.publications)
                }
            }
        }
    }
}

subprojects {
    apply(plugin = "base")

    base {
        archivesBaseName = "${rootProject.base.archivesBaseName}-${this@subprojects.project.name}"
    }
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
    api(group = "blue.endless", name = "jankson", version = "1.2.0")
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

        from(components["java"])

        pom {
            description.set("A processor library for deobfuscation mappings.")
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
        name = "ripple"
        setLicenses("MPL-2.0")
        vcsUrl = "https://github.com/Juuxel/Ripple"

        version(closureOf<BintrayExtension.VersionConfig> {
            name = project.version.toString()
        })
    })

    setPublications("maven")
}
