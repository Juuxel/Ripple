/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

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
