/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.ripple.gradle.dependency

import juuxel.ripple.Ripple
import juuxel.ripple.gradle.RippleExtension
import net.fabricmc.lorenztiny.TinyMappingFormat
import org.gradle.api.artifacts.Dependency
import java.io.File
import java.net.URI
import java.nio.file.FileSystems
import java.nio.file.Files

internal class ProcessedDependency(
    private val extension: RippleExtension,
    private val parent: Dependency,
    private val tag: String,
    override val spec: DependencySpec
) : ComputedDependency(), SpecDependency {
    override fun contentEquals(other: Dependency) =
        other is ProcessedDependency && parent.contentEquals(other.parent)

    override fun copy(): Dependency = ProcessedDependency(extension, parent.copy(), tag, spec.copy())

    override fun resolve(): Set<File> {
        val source = extension.detachedConfigGetter(parent).singleFile.toPath()
        val tree = group.split('.') + name + version
        val versionDirectory = extension.cache.resolve(tree.joinToString(separator = File.separator))
        val target = versionDirectory.resolve("$name-$version.jar")

        Files.createDirectories(versionDirectory)
        if (Files.notExists(target)) {
            Files.copy(source, target)

            FileSystems.newFileSystem(URI.create("jar:${target.toUri()}"), mapOf("create" to true)).use { fs ->
                val mappingsPath = fs.getPath("mappings", "mappings.tiny")
                val oldMappings = TinyMappingFormat.DETECT.read(mappingsPath, "intermediary", "named")
                val ripple = Ripple(extension.processors)
                val newMappings = ripple.process(oldMappings)

                TinyMappingFormat.STANDARD.write(newMappings, mappingsPath, "intermediary", "named")
            }
        }

        return setOf(target.toFile())
    }

    override fun getFiles() = extension.fileCollectionGetter(resolve())
}
