/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.ripple.processor;

import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.PrintWriter;
import java.io.Writer;

/**
 * A writer for {@linkplain RenameRule rename rules}.
 *
 * <p>The format is described in the documentation of {@link RenameRuleReader}.
 *
 * @see RenameRuleReader
 */
public final class RenameRuleWriter implements Closeable {
    private final PrintWriter writer;

    /**
     * Constructs a rename rule writer that writes rules to a {@link Writer}.
     *
     * @param writer the output writer
     */
    public RenameRuleWriter(final @NotNull Writer writer) {
        this.writer = toPrintWriter(writer);
    }

    private static PrintWriter toPrintWriter(final Writer writer) {
        return writer instanceof PrintWriter ? (PrintWriter) writer : new PrintWriter(writer);
    }

    /**
     * Writes the header of rule file. This should be called before any other {@code write} calls.
     */
    public void writeHeader() {
        writer.print(RenameRuleReader.FORMAT_NAME);
        writer.print(' ');
        writer.println(RenameRuleReader.FORMAT_VERSION);
    }

    /**
     * Writes a rename rule.
     *
     * @param rule the written rename rule
     */
    public void write(final @NotNull RenameRule rule) {
        if (rule.isStrict()) {
            writer.print("[strict] ");
        }

        writer.print(rule.getFrom());
        writer.print(" -> ");
        writer.println(rule.getTo());
    }

    /**
     * Writes multiple rename rules.
     *
     * @param rules the rename rules
     */
    public void write(final @NotNull Iterable<RenameRule> rules) {
        for (RenameRule rule : rules) {
            write(rule);
        }
    }

    /**
     * Closes the backing {@link Writer} of this rename rule writer.
     */
    @Override
    public void close() {
        writer.close();
    }
}
