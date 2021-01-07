package juuxel.ripple.gradle.dependency

/**
 * Contains the modifiable data of a dependency.
 *
 * @property group   the Maven group
 * @property name    the artifact ID
 * @property version the artifact version
 */
data class DependencySpec internal constructor(
    var group: String,
    var name: String,
    var version: String
)
