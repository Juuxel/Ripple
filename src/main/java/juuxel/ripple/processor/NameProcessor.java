/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.ripple.processor;

import juuxel.ripple.NameType;
import org.jetbrains.annotations.NotNull;

/**
 * A function that modifies identifier names and comments.
 */
@FunctionalInterface
public interface NameProcessor {
    /**
     * Processes a single name.
     *
     * @param name the name
     * @param type the type of the name
     * @return the processed name
     */
    @NotNull String process(@NotNull String name, @NotNull NameType type);
}
