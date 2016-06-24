/*
 * Copyright (c) 2016, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package com.oracle.truffle.api.debug;

import java.net.URI;
import java.util.Objects;

import com.oracle.truffle.api.source.Source;

/**
 * Represents a location where a breakpoint is installed.
 */
final class BreakpointLocation implements Comparable<BreakpointLocation> {

    private final Object key;
    private final int line;
    private final int column;

    BreakpointLocation(Object key, int line, int column) {
        assert key == null || key instanceof Source || key instanceof URI;
        this.key = key;
        this.line = line;
        if (column < 0) {
            this.column = -1;
        } else {
            this.column = column;
        }
    }

    Object getKey() {
        return key;
    }

    public int compareTo(BreakpointLocation o) {
        final Object key1 = key;
        final Object key2 = o.key;
        if (key1 instanceof Source && key2 instanceof Source) {
            final int nameOrder = ((Source) key1).getName().compareTo(((Source) key2).getName());
            if (nameOrder != 0) {
                return nameOrder;
            }
        } else if (key1 instanceof URI && key2 instanceof URI) {
            int uriOrder = key1.toString().compareTo(key2.toString());
            if (uriOrder != 0) {
                return uriOrder;
            }
        } else {
            if (key1 instanceof URI) {
                return 1;
            } else {
                return -1;
            }
        }
        int lineOrder = Integer.compare(line, o.line);
        if (lineOrder != 0) {
            return lineOrder;
        }

        int columnOrder = Integer.compare(column, o.column);
        if (columnOrder != 0) {
            return columnOrder;
        }
        return 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, this.line, this.column);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BreakpointLocation other = (BreakpointLocation) obj;
        if (this.line != other.line) {
            return false;
        }
        if (this.column != other.column) {
            return false;
        }
        if (!Objects.equals(key, other.key)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String keyDescription;
        if (key instanceof Source) {
            keyDescription = "sourceName=" + ((Source) key).getName();
        } else if (key instanceof URI) {
            keyDescription = "uri=" + ((URI) key).toString();
        } else {
            keyDescription = key.toString();
        }
        return keyDescription + ", line=" + line + ", column=" + column;
    }
}
