/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.ripple.gradle;

import java.util.Objects;

public final class DependencySpec {
    private String group;
    private String name;
    private String version;

    public DependencySpec(String group, String name, String version) {
        this.group = Objects.requireNonNull(group, "group");
        this.name = Objects.requireNonNull(name, "name");
        this.version = Objects.requireNonNull(version, "version");
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = Objects.requireNonNull(group, "group");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = Objects.requireNonNull(name, "name");
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = Objects.requireNonNull(version, "version");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DependencySpec that = (DependencySpec) o;
        return group.equals(that.group) && name.equals(that.name) && version.equals(that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(group, name, version);
    }

    @Override
    public String toString() {
        return group + ':' + name + ':' + version;
    }

    public DependencySpec copy() {
        return new DependencySpec(group, name, version);
    }
}
