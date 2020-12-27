/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.ripple;

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
}
