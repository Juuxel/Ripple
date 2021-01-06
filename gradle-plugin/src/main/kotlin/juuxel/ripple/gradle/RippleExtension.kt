/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.ripple.gradle

import blue.endless.jankson.Jankson
import juuxel.ripple.gradle.dependency.ProcessedDependency
import juuxel.ripple.processor.NameProcessor
import juuxel.ripple.processor.NameProcessorIo
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency
import org.gradle.api.file.FileCollection
import java.io.File
import java.nio.file.Path
import kotlin.streams.asSequence

open class RippleExtension {
    internal lateinit var detachedConfigGetter: (Dependency) -> Configuration
    internal lateinit var cache: Path
    internal lateinit var fileCollectionGetter: (Set<File>) -> FileCollection
    internal lateinit var fileResolver: (Any) -> File
    internal lateinit var dependencyCreator: (Any) -> Dependency

    internal val processors: MutableList<NameProcessor<*>> = ArrayList()

    /**
     * Adds a [processor] to Ripple.
     *
     * If the [processor] parameter is a [NameProcessor], it will be attached directly.
     * Otherwise, it'll be resolved as a [File] and parsed using [RenameRuleReader].
     */
    fun processor(processor: Any) {
        if (processor is NameProcessor<*>) {
            processors += processor
        } else {
            val json = Jankson.builder().build().load(fileResolver(processor))
            processors += NameProcessorIo.readAll(json).asSequence()
        }
    }

    /**
     * Creates a processed mapping dependency from the [dependencyNotation].
     */
    fun processed(dependencyNotation: String): Dependency = processed(dependencyCreator(dependencyNotation))

    /**
     * Creates a processed mapping dependency from the [dependency].
     */
    fun processed(dependency: Dependency): Dependency = ProcessedDependency(this, dependency)
}
