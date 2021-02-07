/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.ripple.gradle;

import juuxel.ripple.gradle.impl.RippleExtensionImpl;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class RipplePlugin implements Plugin<Project> {
    @Override
    public void apply(Project target) {
        RippleExtensionImpl extension = (RippleExtensionImpl) target.getExtensions().create(RippleExtension.class, "ripple", RippleExtensionImpl.class);
        extension.detachedConfigurationGetter = target.getConfigurations()::detachedConfiguration;
        extension.cache = target.getGradle().getGradleUserHomeDir().toPath().resolve("caches").resolve("ripple-cache");
        extension.fileCollectionCreator = target::files;
        extension.fileResolver = target::file;
        extension.dependencyCreator = target.getDependencies()::create;
        extension.refreshDeps = target.getGradle().getStartParameter().isRefreshDependencies();
    }
}
