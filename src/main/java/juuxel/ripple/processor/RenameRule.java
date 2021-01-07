/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.ripple.processor;

import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;
import juuxel.ripple.NameType;
import juuxel.ripple.util.Identifier;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A name processor that does renaming using {@link String#replace(CharSequence, CharSequence)}.
 *
 * <p>Rename rules can also run in <i>exact mode</i>, where the rule is only applied
 * if the input name is equal to the {@code from} parameter.
 *
 * <table border="1">
 *     <caption>Serialised form</caption>
 *     <tr>
 *         <th>Key</th>
 *         <th>Value</th>
 *     </tr>
 *     <tr>
 *         <td>{@code processor}</td>
 *         <td>{@code ripple:rename}</td>
 *     </tr>
 *     <tr>
 *         <td>{@code from}</td>
 *         <td>{@link #getFrom()}</td>
 *     </tr>
 *     <tr>
 *         <td>{@code to}</td>
 *         <td>{@link #getTo()}</td>
 *     </tr>
 *     <tr>
 *         <td>{@code exact} (optional)</td>
 *         <td>{@link #isExact()} (if absent, assumed to be false)</td>
 *     </tr>
 *     <tr>
 *         <td>{@code expand} (optional)</td>
 *         <td>Whether to {@linkplain #expand() expand the rename rule} (if absent, assumed to be false)</td>
 *     </tr>
 * </table>
 */
public final class RenameRule implements NameProcessor<RenameRule> {
    private final String from;
    private final String to;
    private final boolean exact;

    /**
     * Constructs a rename rule.
     *
     * @param from the input string to find
     * @param to   the output string that replaces the input
     */
    public RenameRule(String from, String to) {
        this(from, to, false);
    }

    /**
     * Constructs a rename rule.
     *
     * @param from   the input string to find
     * @param to     the output string that replaces the input
     * @param exact if true, runs in strict mode
     */
    public RenameRule(String from, String to, boolean exact) {
        this.from = Objects.requireNonNull(from, "from");
        this.to = Objects.requireNonNull(to, "to");
        this.exact = exact;
    }

    private RenameRule(String[] from, String[] to, Function<String[], String> merger, boolean exact) {
        this(merger.apply(from), merger.apply(to), exact);
    }

    @Override
    public String process(String name, NameType type) {
        if (exact) {
            if (name.equals(from)) {
                return to;
            } else {
                return name;
            }
        } else {
            return name.replace(from, to);
        }
    }

    /**
     * Gets the input name pattern of this rule.
     *
     * @return the input name pattern
     */
    public String getFrom() {
        return from;
    }

    /**
     * Gets the output name replacement of this rule.
     *
     * @return the output name
     */
    public String getTo() {
        return to;
    }

    /**
     * Tests whether this rename rule runs in exact mode.
     *
     * @return true if this rule is in exact mode, false otherwise
     */
    public boolean isExact() {
        return exact;
    }

    @Override
    public NameProcessorCodec<RenameRule> codec() {
        return new Codec();
    }

    /**
     * Expands this rename rule into its casing variants.
     *
     * <p>Currently includes three variants: {@code lowerCamelCase}, {@code UpperCamelCase} and {@code CONSTANT_CASE}.
     *
     * <p>The expansion is done by converting the input and output patterns
     * (assumed to be in {@code spaced lowercase format}) to the different target casing formats.
     *
     * @return the expanded rule variants of this rule
     */
    public Stream<RenameRule> expand() {
        RenameRule[] expanded = new RenameRule[3];
        String[] fromComponents = from.split(" ");
        String[] toComponents = to.split(" ");

        // lowerCamelCase
        expanded[0] = new RenameRule(
            fromComponents, toComponents,
            components -> components[0] + Stream.of(tail(components))
                .map(RenameRule::capitalise)
                .collect(Collectors.joining()),
            exact
        );

        // UpperCamelCase
        expanded[1] = new RenameRule(
            fromComponents, toComponents,
            components -> Stream.of(components)
                .map(RenameRule::capitalise)
                .collect(Collectors.joining()),
            exact
        );

        // CONSTANT_CASE
        expanded[2] = new RenameRule(
            fromComponents, toComponents,
            components -> Stream.of(components)
                .map(it -> it.toUpperCase(Locale.ROOT))
                .collect(Collectors.joining("_")),
            exact
        );

        return Stream.of(expanded);
    }

    @Override
    public String toString() {
        String result = "RenameRule[" + from + " -> " + to;

        if (exact) {
            result += ", exact";
        }

        result += "]";
        return result;
    }

    private static String capitalise(String str) {
        if (str.length() == 1) {
            return str.toUpperCase(Locale.ROOT);
        } else {
            int headCodePoint = Character.toUpperCase(str.codePointAt(0));
            return new String(Character.toChars(headCodePoint)) + str.substring(1);
        }
    }

    private static String[] tail(String[] parts) {
        return parts.length == 1 ? new String[0] : Arrays.copyOfRange(parts, 1, parts.length);
    }

    /**
     * The codec of {@link RenameRule}.
     *
     * @since 0.2.0
     */
    public static final class Codec implements NameProcessorCodec<RenameRule> {
        private static final Identifier ID = new Identifier("ripple", "rename");

        @Override
        public Identifier getId() {
            return ID;
        }

        @Override
        public Stream<RenameRule> read(JsonObject json) {
            String from = json.get(String.class, "from");
            String to = json.get(String.class, "to");
            boolean expand = json.getBoolean("expand", false);
            boolean exact = json.getBoolean("exact", false);

            RenameRule baseRule = new RenameRule(from, to, exact);
            return expand ? baseRule.expand() : Stream.of(baseRule);
        }

        @Override
        public void write(RenameRule processor, JsonObject json) {
            json.put("from", new JsonPrimitive(processor.from));
            json.put("to", new JsonPrimitive(processor.to));
            json.put("exact", new JsonPrimitive(processor.exact));
        }
    }
}
