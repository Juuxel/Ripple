/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.ripple.processor;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A parser for {@linkplain RenameRule rename rules}.
 *
 * <h2>Format</h2>
 * The format parsed by this reader class contains:
 *
 * <ul>
 *     <li>The first line, which is a header line specifying the format version (currently {@code ripple v1})</li>
 *     <li>Rule lines: {@code (parameters) (from) -> (to)} where <ul>
 *         <li>{@code (from)} and {@code (to)} are the input and output name patterns</li>
 *         <li>{@code (parameters)} is an optional set of comma-separated parameters,
 *         see section <i>Parameters</i> below</li>
 *     </ul></li>
 *     <li>Empty lines and comment lines beginning with {@code #} that are ignored</li>
 * </ul>
 *
 * <h2>Parameters</h2>
 * There are currently two parameters: {@code expand} and {@code strict}.
 *
 * <p>{@code expand} runs the {@linkplain RenameRule#expand() {@code RenameRule} expansion} on the rule.
 *
 * <p>{@code strict} enables the strict mode on the rule, see {@link RenameRule} docs.
 *
 * @see RenameRuleWriter
 */
public final class RenameRuleReader implements Closeable {
    static final String FORMAT_NAME = "ripple";
    static final String FORMAT_VERSION = "v1";

    private final BufferedReader reader;

    /**
     * Constructs a rename rule reader that reads from a {@link BufferedReader}.
     *
     * @param reader the data reader to read rules from
     */
    public RenameRuleReader(final @NotNull BufferedReader reader) {
        this.reader = reader;
    }

    /**
     * Constructs a rename rule reader that reads from a {@link Path}.
     *
     * @param path the file path to read rules from
     * @throws IOException if an IO error occurs when creating a reader from the file path
     */
    public RenameRuleReader(final @NotNull Path path) throws IOException {
        this(Files.newBufferedReader(path));
    }

    /**
     * Constructs a rename rule reader that reads from a list of lines.
     *
     * @param lines the lines to read rules from
     */
    public RenameRuleReader(final @NotNull List<String> lines) {
        this(new BufferedReader(new StringReader(String.join("\n", lines))));
    }

    /**
     * Reads the rename rule file.
     *
     * @return the read rules
     * @throws IOException         if an IO error occurs while reading
     * @throws RuleFormatException if the rule format is incorrect
     */
    public @NotNull List<RenameRule> read() throws IOException, RuleFormatException {
        final String header;

        try {
            header = reader.readLine();
        } catch (IOException e) {
            throw new IOException("Could not read header", e);
        }

        validateHeader(header);

        return reader.lines()
            .filter(line -> !line.isEmpty() && !line.startsWith("#")) // Remove blanks and comments
            .flatMap(rawLine -> {
                String line = rawLine;
                Set<Attribute> attributes = new HashSet<>();
                if (rawLine.startsWith("[")) {
                    int closing = rawLine.indexOf(']');

                    for (final String attributeStr : rawLine.substring(1, closing).split(", *")) {
                        final Attribute attribute = Attribute.of(attributeStr);
                        attributes.add(attribute);

                        if (attribute == Attribute.UNKNOWN) {
                            throw new RuleFormatException("Unknown attribute: " + attributeStr);
                        }
                    }

                    line = rawLine.substring(closing + 1);
                }

                final int arrow = line.indexOf("->");
                if (arrow == -1) {
                    throw new RuleFormatException("Line does not contain '->': '" + line + "'");
                }

                final String from = line.substring(0, arrow).trim();
                final String to = line.substring(arrow + 2).trim();
                final RenameRule rule = new RenameRule(from, to, attributes.contains(Attribute.STRICT));

                if (attributes.contains(Attribute.EXPAND)) {
                    return rule.expand();
                } else {
                    return Stream.of(rule);
                }
            })
            .collect(Collectors.toList());
    }

    private static void validateHeader(final String header) throws RuleFormatException {
        final String[] headerParts = header.split(" ");

        if (!headerParts[0].equals(FORMAT_NAME)) {
            throw new RuleFormatException("Invalid format name: '" + headerParts[0] + "'");
        } else if (!headerParts[1].equals(FORMAT_VERSION)) {
            throw new RuleFormatException("Unsupported format version: '" + headerParts[1] + "'");
        }
    }

    /**
     * Closes the IO reader used by this rule reader.
     *
     * @throws IOException if an IO error occurs
     */
    @Override
    public void close() throws IOException {
        reader.close();
    }

    /**
     * An exception that is thrown when incorrect rename rule formats are encountered.
     */
    public static final class RuleFormatException extends RuntimeException {
        /**
         * Constructs a rule format exception.
         *
         * @param message the exception message
         */
        public RuleFormatException(String message) {
            super(message);
        }
    }

    private enum Attribute {
        EXPAND,
        STRICT,
        UNKNOWN;

        static Attribute of(String str) {
            str = str.trim();

            switch (str) {
                case "expand":
                    return EXPAND;
                case "strict":
                    return STRICT;
                default:
                    return UNKNOWN;
            }
        }
    }
}
