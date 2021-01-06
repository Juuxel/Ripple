/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.ripple.processor;

import juuxel.ripple.NameType;

/**
 * A function that modifies identifier names and comments.
 *
 * @param <P> the type of this processor
 */
public interface NameProcessor<P extends NameProcessor<P>> {
    /**
     * Processes a single name.
     *
     * @param name the name
     * @param type the type of the name
     * @return the processed name
     */
    String process(String name, NameType type);

    /**
     * Gets this processor's codec.
     *
     * @return the codec
     * @since 0.2.0
     */
    NameProcessorCodec<P> codec();
}
