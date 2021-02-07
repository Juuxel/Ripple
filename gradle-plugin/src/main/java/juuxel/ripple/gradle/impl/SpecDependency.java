/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.ripple.gradle.impl;

import juuxel.ripple.gradle.DependencySpec;
import org.gradle.api.artifacts.Dependency;
import org.jetbrains.annotations.Nullable;

public interface SpecDependency extends Dependency {
    DependencySpec getSpec();

    @Nullable
    @Override
    default String getGroup() {
        return getSpec().getGroup();
    }

    @Override
    default String getName() {
        return getSpec().getName();
    }

    @Nullable
    @Override
    default String getVersion() {
        return getSpec().getVersion();
    }
}
