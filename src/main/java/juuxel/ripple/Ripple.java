/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.ripple;

import juuxel.ripple.processor.NameProcessor;
import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.model.ClassMapping;
import org.cadixdev.lorenz.model.FieldMapping;
import org.cadixdev.lorenz.model.MethodMapping;
import org.cadixdev.lorenz.model.MethodParameterMapping;
import org.cadixdev.lorenz.model.TopLevelClassMapping;

import java.util.function.BiFunction;

/**
 * An engine for applying {@linkplain NameProcessor name processors} to deobfuscation mappings.
 */
public final class Ripple {
    private final Iterable<? extends NameProcessor<?>> nameProcessors;

    /**
     * Constructs a Ripple engine.
     *
     * @param nameProcessors the name processors used by this engine
     */
    public Ripple(Iterable<? extends NameProcessor<?>> nameProcessors) {
        this.nameProcessors = nameProcessors;
    }

    /**
     * Processes a single name.
     *
     * @param name the name
     * @param type the type of the name
     * @return the name with all processors applied
     */
    public String process(String name, NameType type) {
        for (NameProcessor<?> processor : nameProcessors) {
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
    public MappingSet process(MappingSet mappings) {
        MappingSet result = MappingSet.create();

        for (TopLevelClassMapping oldClass : mappings.getTopLevelClassMappings()) {
            processClass(oldClass, result::createTopLevelClassMapping);
        }

        return result;
    }

    private void processClass(ClassMapping<?, ?> oldClass, BiFunction<String, String, ClassMapping<?, ?>> newClassCreator) {
        String className = process(oldClass.getDeobfuscatedName(), NameType.CLASS);
        ClassMapping<?, ?> newClass = newClassCreator.apply(oldClass.getObfuscatedName(), className);

        for (ClassMapping<?, ?> oldInnerClass : oldClass.getInnerClassMappings()) {
            processClass(oldInnerClass, newClass::createInnerClassMapping);
        }

        for (MethodMapping oldMethod : oldClass.getMethodMappings()) {
            String methodName = process(oldMethod.getDeobfuscatedName(), NameType.METHOD);
            MethodMapping newMethod = newClass.createMethodMapping(oldMethod.getSignature(), methodName);

            for (MethodParameterMapping oldParam : oldMethod.getParameterMappings()) {
                String paramName = process(oldParam.getDeobfuscatedName(), NameType.PARAMETER);
                newMethod.createParameterMapping(oldParam.getIndex(), paramName);
            }
        }

        for (FieldMapping oldField : oldClass.getFieldMappings()) {
            String fieldName = process(oldField.getDeobfuscatedName(), NameType.FIELD);
            newClass.createFieldMapping(oldField.getSignature(), fieldName);
        }
    }
}
