/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.ripple.processor;

import blue.endless.jankson.JsonObject;
import juuxel.ripple.util.Identifier;

import java.util.stream.Stream;

/**
 * A codec that handles JSON reading and writing of {@link NameProcessor} instances.
 *
 * <p>Codecs are loaded as {@link java.util.ServiceLoader} services for deserialisation purposes.
 *
 * @param <P> the input/output {@link NameProcessor} type
 * @since 0.2.0
 */
public interface NameProcessorCodec<P extends NameProcessor<P>> {
    /**
     * Gets the unique identifier of this codec.
     *
     * @return this codec's ID
     */
    Identifier getId();

    /**
     * Reads name processors from a JSON object.
     *
     * @param json the JSON object
     * @return a stream of the read processors
     */
    Stream<P> read(JsonObject json);

    /**
     * Writes a name processor to a JSON object.
     *
     * @param processor the name processor
     * @param json      the JSON object
     */
    void write(P processor, JsonObject json);
}
