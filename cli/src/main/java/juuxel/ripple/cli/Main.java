/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.ripple.cli;

import blue.endless.jankson.*;
import juuxel.ripple.Ripple;
import juuxel.ripple.processor.NameProcessor;
import juuxel.ripple.processor.NameProcessorIo;
import net.fabricmc.lorenztiny.TinyMappingFormat;
import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.io.MappingFormat;
import org.cadixdev.lorenz.io.MappingFormats;
import org.cadixdev.lorenz.io.MappingsReader;
import org.cadixdev.lorenz.io.MappingsWriter;
import org.jetbrains.annotations.Nullable;
import picocli.CommandLine;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@CommandLine.Command(name = "ripple", mixinStandardHelpOptions = true)
public final class Main implements Callable<Integer> {
    @CommandLine.Option(names = "-i", description = "input mappings file", required = true)
    private Path input;

    @CommandLine.Option(names = {"-r", "--rules"}, description = "rename rule file", required = true)
    private Path rules;

    @CommandLine.Option(names = "-o", description = "output mappings file", required = true)
    private Path output;

    @CommandLine.Option(names = {"-f", "--input-format"}, description = "input mapping format", required = true)
    private String inputFormat;

    @CommandLine.Option(names = "--output-format", description = "output mapping format (will use input format if missing)")
    private @Nullable String outputFormat;

    @CommandLine.Option(names = "--expanded-rules-output", description = "an optional file where to write the expanded rename rules")
    private @Nullable Path expandedRulesOutput;

    @Override
    public Integer call() throws Exception {
        if (this.outputFormat == null) {
            this.outputFormat = this.inputFormat;
        }

        final @Nullable MappingFormat inputFormat = getMappingFormat(this.inputFormat);
        final @Nullable MappingFormat outputFormat = getMappingFormat(this.outputFormat);

        if (inputFormat == null) {
            printMappingFormats(this.inputFormat);
            return 1;
        } else if (outputFormat == null) {
            printMappingFormats(this.outputFormat);
            return 1;
        } else if (Files.notExists(input)) {
            System.err.println("Input file '" + input + "' does not exist.");
            return 1;
        }else if (Files.notExists(rules)) {
            System.err.println("Rule file '" + rules + "' does not exist.");
            return 1;
        }

        List<? extends NameProcessor<?>> nameProcessors;

        try (InputStream in = Files.newInputStream(rules)) {
            Jankson jankson = Jankson.builder().build();
            JsonObject json = jankson.load(in);
            nameProcessors = NameProcessorIo.readAll(json).collect(Collectors.toList());
        }

        final MappingSet inputMappings;
        try (final MappingsReader reader = inputFormat.createReader(input)) {
            inputMappings = reader.read();
        }

        final MappingSet outputMappings = new Ripple(nameProcessors).process(inputMappings);

        try (final MappingsWriter writer = outputFormat.createWriter(output)) {
            writer.write(outputMappings);
        }

        if (expandedRulesOutput != null) {
            Files.write(
                expandedRulesOutput,
                NameProcessorIo.toJson(nameProcessors).toJson(true, true).getBytes(StandardCharsets.UTF_8)
            );
        }

        return 0;
    }

    private static MappingFormat getMappingFormat(final String format) {
        if (format.matches("^tiny(v1|v2)?:.+:.+$")) {
            final String[] parts = format.split(":");

            switch (parts[0]) {
                case "tiny":
                    return new TinyMappingFormatWrapper(TinyMappingFormat.DETECT, parts[1], parts[2]);
                case "tinyv1":
                    return new TinyMappingFormatWrapper(TinyMappingFormat.LEGACY, parts[1], parts[2]);
                case "tinyv2":
                default:
                    return new TinyMappingFormatWrapper(TinyMappingFormat.STANDARD, parts[1], parts[2]);
            }
        } else {
            return MappingFormats.byId(format);
        }
    }

    private static void printMappingFormats(final String format) {
        System.err.println("Unknown format: " + format);
        System.err.println("Available formats: tiny:from:to, tinyv1:from:to, tinyv2:from:to, " + String.join(", ", MappingFormats.REGISTRY.keys()));
    }

    public static void main(final String[] args) {
        new CommandLine(new Main()).execute(args);
    }

    private static class TinyMappingFormatWrapper implements MappingFormat {
        private final TinyMappingFormat format;
        private final String from;
        private final String to;

        TinyMappingFormatWrapper(final TinyMappingFormat format, final String from, final String to) {
            this.format = format;
            this.from = from;
            this.to = to;
        }

        @Deprecated
        @Override
        public MappingsReader createReader(final InputStream stream) throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public MappingsReader createReader(Path path) throws IOException {
            return format.createReader(path, from, to);
        }

        @Override
        public MappingsWriter createWriter(final OutputStream stream) {
            return format.createWriter(new OutputStreamWriter(stream), from, to);
        }

        @Override
        public Optional<String> getStandardFileExtension() {
            return Optional.of("tiny");
        }
    }
}
