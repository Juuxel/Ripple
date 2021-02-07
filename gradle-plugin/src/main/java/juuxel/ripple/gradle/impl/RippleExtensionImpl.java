/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.ripple.gradle.impl;

import juuxel.ripple.gradle.RippleExtension;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.file.FileCollection;

import java.io.File;
import java.nio.file.Path;
import java.util.Set;
import java.util.function.Function;

public class RippleExtensionImpl extends RippleExtension {
    public Path cache;
    public boolean refreshDeps;
    public Function<Set<File>, FileCollection> fileCollectionCreator;
    public Function<Dependency, Configuration> detachedConfigurationGetter;
    public Function<Object, File> fileResolver;
    public Function<Object, Dependency> dependencyCreator;
}
