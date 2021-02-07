/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.ripple.gradle.impl;

import org.gradle.api.artifacts.FileCollectionDependency;
import org.gradle.api.artifacts.SelfResolvingDependency;
import org.gradle.api.artifacts.component.ComponentIdentifier;
import org.gradle.api.internal.artifacts.dependencies.SelfResolvingDependencyInternal;
import org.gradle.api.tasks.TaskDependency;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collections;
import java.util.Set;

public abstract class ComputedDependency implements SelfResolvingDependency, SelfResolvingDependencyInternal, FileCollectionDependency {
    private @Nullable String reason;

    @Nullable
    @Override
    public String getReason() {
        return reason;
    }

    @Override
    public void because(@Nullable String reason) {
        this.reason = reason;
    }

    @Override
    public Set<File> resolve(boolean transitive) {
        return resolve();
    }

    @Override
    public TaskDependency getBuildDependencies() {
        return task -> Collections.emptySet();
    }

    @Nullable
    @Override
    public ComponentIdentifier getTargetComponentId() {
        return null;
    }
}
