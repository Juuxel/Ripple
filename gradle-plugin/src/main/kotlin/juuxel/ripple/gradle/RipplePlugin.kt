/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.ripple.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class RipplePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val extension = target.extensions.create("ripple", RippleExtension::class.java)
        extension.detachedConfigGetter = {
            target.configurations.detachedConfiguration(it)
        }
        extension.cache = target.projectDir.toPath().resolve(".gradle").resolve("ripple-cache")
        extension.fileCollectionGetter = { target.files(it) }
        extension.fileResolver = target::file
        extension.dependencyCreator = { target.dependencies.create(it) }
    }
}
