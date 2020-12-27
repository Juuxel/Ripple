/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.ripple;

import juuxel.ripple.processor.NameProcessor;
import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.model.FieldMapping;
import org.cadixdev.lorenz.model.MethodMapping;
import org.cadixdev.lorenz.model.MethodParameterMapping;
import org.cadixdev.lorenz.model.TopLevelClassMapping;
import org.jetbrains.annotations.NotNull;

/**
 * An engine for applying {@linkplain NameProcessor name processors} to deobfuscation mappings.
 */
public final class Ripple {
    private final Iterable<? extends NameProcessor> nameProcessors;

    /**
     * Constructs a Ripple engine.
     *
     * @param nameProcessors the name processors used by this engine
     */
    public Ripple(final @NotNull Iterable<? extends NameProcessor> nameProcessors) {
        this.nameProcessors = nameProcessors;
    }

    /**
     * Processes a single name.
     *
     * @param name the name
     * @param type the type of the name
     * @return the name with all processors applied
     */
    public @NotNull String process(@NotNull String name, final @NotNull NameType type) {
        for (final NameProcessor processor : nameProcessors) {
            name = processor.process(name, type);
        }

        return name;
    }

    /**
     * Processes a Lorenz {@link MappingSet}.
     *
     * @param mappings the input mapping set
     * @return the processed mapping set
     */
    public @NotNull MappingSet process(final @NotNull MappingSet mappings) {
        final MappingSet result = MappingSet.create();

        for (final TopLevelClassMapping oldClass : mappings.getTopLevelClassMappings()) {
            final String className = process(oldClass.getDeobfuscatedName(), NameType.CLASS);
            final TopLevelClassMapping newClass = result.createTopLevelClassMapping(oldClass.getObfuscatedName(), className);

            for (final MethodMapping oldMethod : oldClass.getMethodMappings()) {
                final String methodName = process(oldMethod.getDeobfuscatedName(), NameType.METHOD);
                final MethodMapping newMethod = newClass.createMethodMapping(oldMethod.getSignature(), methodName);

                for (final MethodParameterMapping oldParam : oldMethod.getParameterMappings()) {
                    final String paramName = process(oldParam.getDeobfuscatedName(), NameType.PARAMETER);
                    newMethod.createParameterMapping(oldParam.getIndex(), paramName);
                }
            }

            for (final FieldMapping oldField : oldClass.getFieldMappings()) {
                final String fieldName = process(oldField.getDeobfuscatedName(), NameType.FIELD);
                newClass.createFieldMapping(oldField.getSignature(), fieldName);
            }
        }

        return result;
    }
}
