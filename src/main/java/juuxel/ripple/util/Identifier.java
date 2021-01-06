/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.ripple.util;

import blue.endless.jankson.JsonPrimitive;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * A namespaced identifier containing a namespace and a path.
 *
 * <p>The namespace should be a unique string such as the name of your project.
 * The path should be unique within the namespace. For example, the ID {@link juuxel.ripple.processor.RenameRule ripple:rename}
 * has the namespace {@code ripple} and the path {@code rename}.
 *
 * <p>The string format of an identifier is {@code namespace:path}, like with the IDs used in Minecraft.
 *
 * <p>Both components of an identifier can only contain lowercase ASCII letters ({@code a-z}), numbers ({@code 0-9}),
 * underscores ({@code _}) and periods ({@code .}).
 *
 * @since 0.2.0
 */
public final class Identifier {
    private static final Pattern COMPONENT_PATTERN = Pattern.compile("^[a-z0-9_.]+$");

    private final String namespace;
    private final String path;

    /**
     * Constructs an identifier from a namespace and a path.
     *
     * @param namespace the namespace
     * @param path      the path
     * @throws IllegalArgumentException if the format is invalid
     */
    public Identifier(String namespace, String path) {
        this.namespace = validateComponent(namespace, "namespace");
        this.path = validateComponent(path, "path");
    }

    /**
     * Parses a string in the {@code namespace:path} format into an identifier.
     *
     * @param identifier the parsed identifier string
     * @throws IllegalArgumentException if the format is invalid
     */
    public Identifier(String identifier) {
        String[] split = Objects.requireNonNull(identifier, "identifier string").split(":");

        if (split.length != 2) {
            throw new IllegalArgumentException("Identifier '" + identifier + "' should have exactly two components separated by :");
        }

        this.namespace = validateComponent(split[0], "namespace");
        this.path = validateComponent(split[1], "path");
    }

    private static String validateComponent(String component, String type) {
        Objects.requireNonNull(component, type);

        if (!COMPONENT_PATTERN.matcher(component).matches()) {
            throw new IllegalArgumentException("Identifier " + type + " '" + component + "' does not match regex " + COMPONENT_PATTERN.pattern());
        }

        return component;
    }

    /**
     * Gets the namespace of this ID.
     *
     * @return the namespace
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * Gets the path of this ID.
     *
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * Converts this ID to a JSON primitive.
     *
     * @return the serialised JSON form of this ID
     */
    public JsonPrimitive toJson() {
        return new JsonPrimitive(toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Identifier)) return false;
        Identifier that = (Identifier) o;
        return namespace.equals(that.namespace) && path.equals(that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(namespace, path);
    }

    @Override
    public String toString() {
        return namespace + ':' + path;
    }
}
