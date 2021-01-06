/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.ripple.gradle.dependency

import org.gradle.api.artifacts.FileCollectionDependency
import org.gradle.api.artifacts.SelfResolvingDependency
import org.gradle.api.tasks.TaskDependency
import java.io.File

internal abstract class ComputedDependency(
    private val group: String?,
    private val name: String?,
    private val version: String?
) : SelfResolvingDependency, FileCollectionDependency {
    private var reason: String? = null

    override fun getGroup() = group
    override fun getName() = name
    override fun getVersion() = version

    override fun getReason(): String? = reason
    override fun because(reason: String?) {
        this.reason = reason
    }

    override fun getBuildDependencies() = TaskDependency { emptySet() }
    override fun resolve(transitive: Boolean): Set<File> = resolve()
}
