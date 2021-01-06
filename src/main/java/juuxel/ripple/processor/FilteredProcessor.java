/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.ripple.processor;

import blue.endless.jankson.JsonArray;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;
import juuxel.ripple.NameType;
import juuxel.ripple.util.Identifier;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A name processor that wraps another processor and applies it only to certain {@linkplain NameType name types}.
 *
 * <table border="1">
 *     <caption>Serialised form</caption>
 *     <tr>
 *         <th>Key</th>
 *         <th>Value</th>
 *     </tr>
 *     <tr>
 *         <td>{@code id}</td>
 *         <td>{@code ripple:filtered}</td>
 *     </tr>
 *     <tr>
 *         <td>{@code source}</td>
 *         <td>{@linkplain NameProcessorIo#toJson(NameProcessor) A serialised name processor}</td>
 *     </tr>
 *     <tr>
 *         <td>{@code filter}</td>
 *         <td>A JSON array of {@linkplain NameType name types}</td>
 *     </tr>
 * </table>
 */
public final class FilteredProcessor<P extends NameProcessor<P>> implements NameProcessor<FilteredProcessor<P>> {
    private final P source;
    private final Set<NameType> filter;

    /**
     * Constructs a filtered name processor.
     *
     * @param source the source processor
     * @param filter the allowed name types
     */
    public FilteredProcessor(final P source, final Set<NameType> filter) {
        this.source = Objects.requireNonNull(source, "source");
        this.filter = Objects.requireNonNull(filter, "filter");
    }

    @Override
    public String process(final String name, final NameType type) {
        if (filter.contains(type)) {
            return source.process(name, type);
        } else {
            return name;
        }
    }

    @Override
    public NameProcessorCodec<FilteredProcessor<P>> codec() {
        return new Codec<>();
    }

    /**
     * The codec of {@link FilteredProcessor}.
     *
     * @since 0.2.0
     */
    public static final class Codec<P extends NameProcessor<P>> implements NameProcessorCodec<FilteredProcessor<P>> {
        private static final Identifier ID = new Identifier("ripple", "filtered");

        @Override
        public Identifier getId() {
            return ID;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Stream<FilteredProcessor<P>> read(JsonObject json) {
            JsonObject sourceJson = json.getObject("source");
            Identifier sourceId = new Identifier(sourceJson.get(String.class, "id"));
            NameProcessorCodec<?> sourceCodec = NameProcessorIo.getCodec(sourceId)
                .orElseThrow(() -> new UnsupportedOperationException("Processor type '" + sourceId + "' is not readable"));

            Set<NameType> filter = json.get(JsonArray.class, "filter").stream()
                .map(it -> (JsonPrimitive) it)
                .map(JsonPrimitive::asString)
                .map(NameType::getByName)
                .collect(Collectors.toSet());

            return sourceCodec.read(sourceJson).map(source -> new FilteredProcessor<>((P) source, filter));
        }

        @Override
        public void write(FilteredProcessor<P> processor, JsonObject json) {
            json.put("source", NameProcessorIo.toJson(processor.source));

            JsonArray filterJson = new JsonArray();
            processor.filter.stream()
                .map(NameType::toString)
                .map(JsonPrimitive::new)
                .forEach(filterJson::add);
            json.put("filter", filterJson);
        }
    }
}
