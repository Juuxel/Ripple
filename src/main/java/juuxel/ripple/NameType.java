/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.ripple;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * The types of processed names.
 */
public enum NameType {
    /**
     * A class.
     *
     * <p>Classes use slash format, eg. {@code java/lang/String} and {@code java/util/Map$Entry}.
     */
    CLASS,

    /** A method. */
    METHOD,
    /** A field. */
    FIELD,
    /** A parameter inside a method. */
    PARAMETER,

    // Not produced yet (included for forwards compatibility)

    /** A local variable inside a method. */
    LOCAL_VARIABLE,
    /** A (javadoc) comment. */
    COMMENT,
    ;

    private static final Map<String, NameType> VALUES_BY_NAME = new HashMap<>();

    static {
        for (NameType value : values()) {
            VALUES_BY_NAME.put(value.name(), value);
        }
    }

    /**
     * Gets a name type by its name.
     *
     * @param name the name
     * @return the name type with the name
     * @throws NoSuchElementException if there is no name type with the specified name
     * @since 0.2.0
     */
    public static NameType getByName(String name) {
        @Nullable NameType type = VALUES_BY_NAME.get(name);

        if (type == null) {
            throw new NoSuchElementException("Unknown name type: " + name);
        }

        return type;
    }
}
