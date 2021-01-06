# Ripple

A library for processing and modifying names in Java deobfuscation mappings.
Ripple currently only supports processing `MappingSet`s from [Lorenz](https://github.com/CadixDev/Lorenz).

## Usage

### Standalone

Ripple has a Java API as well as a CLI app.

The Java API is based around the `juuxel.ripple.Ripple` class, whose docs should be a good starting point.
You can get the library on JCenter at `io.github.juuxel.ripple:ripple:0.2.1`.

Builds of the CLI app are not currently released. You can build them yourself
if you have JDK 8 or newer by running `./gradlew build`. The CLI files will be in `cli/build/libs`.

### Gradle plugin

Ripple also has a Gradle plugin for Minecraft modding, intended to be used with [Fabric Loom](https://github.com/FabricMC/fabric-loom)
or one of its forks. It processes mapping dependencies that contain a Tiny mappings file at `mappings/mappings.tiny`.

To use the Ripple plugin, you need to add my Maven repository to the `pluginManagement` repositories in settings.gradle(.kts):

> This code should work for both Groovy and Kotlin buildscripts.

```kotlin
pluginManagement {
    repositories {
        // ... other repos here
        maven {
            name = "JuuxelBintray"
            url = uri("https://dl.bintray.com/juuxel/maven")
        }
    }
}
```

Then add the plugin to the `plugins` block:

```kotlin
plugins {
    id("io.github.juuxel.ripple") version "0.2.1"
}
```

Ripple processors can be added using the `ripple.processor` method inside build.gradle(.kts).

Finally, the dependency can be declared like this:

```kotlin
dependencies {
    // The version tag passed to ripple.processed needs to be unique for your name processors.
    // There will be invalid global files otherwise.
    // I recommend using either your name or your mod's name.
    mappings(ripple.processed("net.fabricmc:yarn:whatever_version", "<your version tag>"))
}
```

## Limitations

- Due to Lorenz not having comment support, processing Tiny v2 mappings
  using Ripple will erase all javadoc comments.
