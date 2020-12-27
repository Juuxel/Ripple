/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.ripple.processor;

import juuxel.ripple.NameType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A name processor that does renaming using {@link String#replace(CharSequence, CharSequence)}.
 *
 * <p>Rename rules can also run in <i>strict mode</i>, where the rule is only applied
 * if the input name is equal to the {@code from} parameter.
 */
public final class RenameRule implements NameProcessor {
    private final String from;
    private final String to;
    private final boolean strict;

    /**
     * Constructs a rename rule.
     *
     * @param from the input string to find
     * @param to   the output string that replaces the input
     */
    public RenameRule(final String from, final String to) {
        this.from = from;
        this.to = to;
        this.strict = false;
    }

    /**
     * Constructs a rename rule.
     *
     * @param from   the input string to find
     * @param to     the output string that replaces the input
     * @param strict if true, runs in strict mode
     */
    public RenameRule(final String from, final String to, final boolean strict) {
        this.from = from;
        this.to = to;
        this.strict = strict;
    }

    private RenameRule(final String[] from, final String[] to, final Function<String[], String> merger, final boolean strict) {
        this(merger.apply(from), merger.apply(to), strict);
    }

    @Override
    public @NotNull String process(final @NotNull String name, @NotNull NameType type) {
        if (strict) {
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
     * Tests whether this rename rule runs in strict mode.
     *
     * @return true if this rule is in strict mode, false otherwise
     */
    public boolean isStrict() {
        return strict;
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
        final RenameRule[] expanded = new RenameRule[3];
        final String[] fromComponents = from.split(" ");
        final String[] toComponents = to.split(" ");

        // lowerCamelCase
        expanded[0] = new RenameRule(
            fromComponents, toComponents,
            components -> components[0] + Stream.of(tail(components))
                .map(RenameRule::capitalise)
                .collect(Collectors.joining()),
            strict
        );

        // UpperCamelCase
        expanded[1] = new RenameRule(
            fromComponents, toComponents,
            components -> Stream.of(components)
                .map(RenameRule::capitalise)
                .collect(Collectors.joining()),
            strict
        );

        // CONSTANT_CASE
        expanded[2] = new RenameRule(
            fromComponents, toComponents,
            components -> Stream.of(components)
                .map(it -> it.toUpperCase(Locale.ROOT))
                .collect(Collectors.joining("_")),
            strict
        );

        return Stream.of(expanded);
    }

    @Override
    public String toString() {
        String result = "RenameRule[" + from + " -> " + to;

        if (strict) {
            result += ", strict";
        }

        result += "]";
        return result;
    }

    private static String capitalise(final String str) {
        if (str.length() == 1) {
            return str.toUpperCase(Locale.ROOT);
        } else {
            final int headCodePoint = Character.toUpperCase(str.codePointAt(0));
            return new String(Character.toChars(headCodePoint)) + str.substring(1);
        }
    }

    private static String[] tail(final String[] parts) {
        return parts.length == 1 ? new String[0] : Arrays.copyOfRange(parts, 1, parts.length);
    }
}
