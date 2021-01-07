/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.ripple.processor;

import blue.endless.jankson.JsonArray;
import blue.endless.jankson.JsonObject;
import juuxel.ripple.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Stream;

/**
 * Handles JSON serialisation and deserialisation of name processors.
 *
 * @since 0.2.0
 */
public final class NameProcessorIo {
    private static final String PROCESSOR_KEY = "processor";
    private static final String ALL_PROCESSORS_KEY = "processors";
    private static final Map<Identifier, NameProcessorCodec<?>> CODECS_BY_ID = new HashMap<>();

    static {
        for (final NameProcessorCodec<?> codec : ServiceLoader.load(NameProcessorCodec.class)) {
            CODECS_BY_ID.put(codec.getId(), codec);
        }
    }

    /**
     * Gets a name processor codec by its {@linkplain NameProcessorCodec#getId() ID} if available.
     *
     * @param id the ID
     * @return the codec, or empty if there's no codec with the specified ID
     */
    public static Optional<NameProcessorCodec<?>> getCodec(@NotNull Identifier id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(CODECS_BY_ID.get(id));
    }

    /**
     * Reads all processors from a single JSON-formatted name processor definition.
     *
     * @param json the JSON-formatted definition
     * @return the read processors
     */
    public static Stream<? extends NameProcessor<?>> readSingle(JsonObject json) {
        Objects.requireNonNull(json, "json");

        Identifier id = new Identifier(json.get(String.class, PROCESSOR_KEY));
        NameProcessorCodec<?> codec = getCodec(id)
            .orElseThrow(() -> new UnsupportedOperationException("Processor type '" + id + "' is not readable"));

        return codec.read(json);
    }

    /**
     * Reads all processors from an array of entries inside a JSON object using {@link #readSingle(JsonObject)}.
     *
     * @param json the JSON array
     * @return the read processors
     */
    public static Stream<? extends NameProcessor<?>> readAll(JsonObject json) {
        return json.get(JsonArray.class, ALL_PROCESSORS_KEY).stream()
            .map(entry -> {
                if (entry instanceof JsonObject) {
                    return (JsonObject) entry;
                } else {
                    throw new IllegalArgumentException("Processor array contains non-object child " + entry.toJson());
                }
            })
            .flatMap(NameProcessorIo::readSingle);
    }

    /**
     * Converts a name processor to JSON.
     *
     * @param processor the name processor
     * @return the JSON representation of the name processor
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static JsonObject toJson(NameProcessor<?> processor) {
        JsonObject json = new JsonObject();
        NameProcessorCodec codec = processor.codec();
        codec.write(processor, json);
        json.put(PROCESSOR_KEY, codec.getId().toJson());
        return json;
    }

    /**
     * Converts a collection of name processors to JSON.
     *
     * @param processors the name processors
     * @return the JSON representation of the name processors
     */
    public static JsonObject toJson(Iterable<? extends NameProcessor<?>> processors) {
        JsonArray processorArray = new JsonArray();
        for (NameProcessor<?> processor : processors) {
            processorArray.add(toJson(processor));
        }

        JsonObject json = new JsonObject();
        json.put(ALL_PROCESSORS_KEY, processorArray);
        return json;
    }
}
