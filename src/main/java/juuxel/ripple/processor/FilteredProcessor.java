/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.ripple.processor;

import juuxel.ripple.NameType;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/**
 * A name processor that wraps another processor, filtering its changes using a {@link NameType} filter.
 */
public final class FilteredProcessor implements NameProcessor {
    private final NameProcessor parent;
    private final Predicate<? super NameType> filter;

    /**
     * Constructs a filtered name processor.
     *
     * @param parent the parent processor
     * @param filter a filter specifying whether the parent processor should be applied to a given name type
     */
    public FilteredProcessor(final @NotNull NameProcessor parent, final @NotNull Predicate<? super NameType> filter) {
        this.parent = parent;
        this.filter = filter;
    }

    @Override
    public @NotNull String process(final @NotNull String name, final @NotNull NameType type) {
        if (filter.test(type)) {
            return parent.process(name, type);
        } else {
            return name;
        }
    }
}
