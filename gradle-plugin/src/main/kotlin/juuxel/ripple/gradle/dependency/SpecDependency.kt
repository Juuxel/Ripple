/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.ripple.gradle.dependency

import org.gradle.api.artifacts.Dependency

internal interface SpecDependency : Dependency {
    val spec: DependencySpec

    override fun getGroup() = spec.group
    override fun getName() = spec.name
    override fun getVersion() = spec.version
}
