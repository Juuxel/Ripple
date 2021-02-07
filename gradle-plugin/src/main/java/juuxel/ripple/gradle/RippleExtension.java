/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.ripple.gradle;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.api.SyntaxError;
import juuxel.ripple.gradle.impl.ProcessedDependency;
import juuxel.ripple.gradle.impl.RippleExtensionImpl;
import juuxel.ripple.processor.NameProcessor;
import juuxel.ripple.processor.NameProcessorIo;
import org.gradle.api.Action;
import org.gradle.api.GradleException;
import org.gradle.api.artifacts.Dependency;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class RippleExtension {
    private static final Action<DependencySpec> EMPTY_ACTION = spec -> {};

    private List<NameProcessor<?>> processors = new ArrayList<>();

    public List<NameProcessor<?>> getProcessors() {
        return processors;
    }

    public void setProcessors(List<NameProcessor<?>> processors) {
        this.processors = Objects.requireNonNull(processors, "processors");
    }

    /**
     * Adds a processor to Ripple.
     *
     * <p>If the processor parameter is a {@link NameProcessor}, it will be attached directly.
     * Otherwise, it'll be resolved as a {@link File} and parsed using {@link NameProcessorIo}.
     *
     * @param processor the processor or the file path to a processor JSON file
     */
    public void processor(Object processor) {
        if (processor instanceof NameProcessor<?>) {
            processors.add((NameProcessor<?>) processor);
        } else {
            try {
                JsonObject json = Jankson.builder().build().load(asImpl().fileResolver.apply(processor));
                NameProcessorIo.readAll(json).forEach(processors::add);
            } catch (IOException e) {
                throw new GradleException("Could not load name processor config file " + processor, e);
            } catch (SyntaxError e) {
                throw new GradleException("Malformed name processor config file " + processor + ": " + e.getCompleteMessage(), e);
            }
        }
    }

    public Dependency process(Dependency dependency, String tag, Action<DependencySpec> action) {
        return new ProcessedDependency(asImpl(), dependency, tag, processedSpecOf(dependency, tag, action));
    }

    public Dependency process(Dependency dependency, String tag) {
        return process(dependency, tag, EMPTY_ACTION);
    }

    public Dependency process(String dependencyNotation, String tag, Action<DependencySpec> action) {
        return process(asImpl().dependencyCreator.apply(dependencyNotation), tag, action);
    }

    public Dependency process(String dependencyNotation, String tag) {
        return process(dependencyNotation, tag, EMPTY_ACTION);
    }

    private static DependencySpec processedSpecOf(Dependency dependency, String tag, Action<DependencySpec> action) {
        DependencySpec spec = new DependencySpec(
            "ripple." + dependency.getGroup(),
            dependency.getName(),
            dependency.getVersion() + '-' + tag
        );
        action.execute(spec);
        return spec;
    }

    private RippleExtensionImpl asImpl() {
        return (RippleExtensionImpl) this;
    }
}
