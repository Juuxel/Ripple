package juuxel.ripple.gradle.dependency

import org.gradle.api.artifacts.Dependency

internal interface SpecDependency : Dependency {
    val spec: DependencySpec

    override fun getGroup() = spec.group
    override fun getName() = spec.name
    override fun getVersion() = spec.version
}
