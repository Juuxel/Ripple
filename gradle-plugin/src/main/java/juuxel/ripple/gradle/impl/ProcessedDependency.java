/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.ripple.gradle.impl;

import juuxel.ripple.Ripple;
import juuxel.ripple.gradle.DependencySpec;
import net.fabricmc.lorenztiny.TinyMappingFormat;
import org.cadixdev.lorenz.MappingSet;
import org.gradle.api.GradleException;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.file.FileCollection;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ProcessedDependency extends ComputedDependency implements SpecDependency {
    private final RippleExtensionImpl extension;
    private final Dependency parent;
    private final String tag;
    private final DependencySpec spec;

    public ProcessedDependency(RippleExtensionImpl extension, Dependency parent, String tag, DependencySpec spec) {
        this.extension = extension;
        this.parent = parent;
        this.tag = tag;
        this.spec = spec;
    }

    @Override
    public DependencySpec getSpec() {
        return spec;
    }

    @Override
    public Set<File> resolve() {
        try {
            Path source = extension.detachedConfigurationGetter.apply(parent).getSingleFile().toPath();

            List<String> tree = new ArrayList<>(Arrays.asList(spec.getGroup().split("\\.")));
            tree.add(spec.getName());
            tree.add(spec.getVersion());

            Path versionDirectory = extension.cache.resolve(String.join(File.separator, tree));
            Path target = versionDirectory.resolve(String.format("%s-%s.jar", spec.getName(), spec.getVersion()));
            Files.createDirectories(versionDirectory);

            if (Files.notExists(target) || extension.refreshDeps) {
                Files.copy(source, target);
                HashMap<String, Object> parameters = new HashMap<>();
                parameters.put("create", false);

                try (FileSystem fs = FileSystems.newFileSystem(URI.create("jar:" + target.toUri()), parameters)) {
                    Path mappingsPath = fs.getPath("mappings", "mappings.tiny");
                    MappingSet oldMappings = TinyMappingFormat.DETECT.read(mappingsPath, "intermediary", "named");
                    MappingSet newMappings = new Ripple(extension.getProcessors()).process(oldMappings);

                    TinyMappingFormat.STANDARD.write(newMappings, mappingsPath, "intermediary", "named");
                }
            }

            return Collections.singleton(target.toFile());
        } catch (IOException e) {
            throw new GradleException("Could not process " + parent, e);
        }
    }

    @Override
    public FileCollection getFiles() {
        return extension.fileCollectionCreator.apply(resolve());
    }

    @Override
    public boolean contentEquals(Dependency dependency) {
        return dependency instanceof ProcessedDependency && parent.contentEquals(((ProcessedDependency) dependency).parent);
    }

    @Override
    public Dependency copy() {
        return new ProcessedDependency(extension, parent.copy(), tag, spec.copy());
    }
}
