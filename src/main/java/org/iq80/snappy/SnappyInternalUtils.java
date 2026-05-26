/*
 * Copyright (C) 2011 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.iq80.snappy;

import java.io.IOException;
import java.io.InputStream;

final class SnappyInternalUtils {

    private SnappyInternalUtils() {
    }

    //
    // Copied from Guava Preconditions
    static <T> T checkNotNull(T reference, String errorMessageTemplate, Object... errorMessageArgs) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    static void checkArgument(boolean expression, String errorMessageTemplate, Object... errorMessageArgs) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    static void checkPositionIndexes(int start, int end, int size) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    private static String badPositionIndexes(int start, int end, int size) {
        if (start < 0 || start > size) {
            return badPositionIndex(start, size, "start index");
        }
        if (end < 0 || end > size) {
            return badPositionIndex(end, size, "end index");
        }
        // end < start
        return String.format("end index (%s) must not be less than start index (%s)", end, start);
    }

    private static String badPositionIndex(int index, int size, String desc) {
        if (index < 0) {
            return String.format("%s (%s) must not be negative", desc, index);
        } else if (size < 0) {
            throw new IllegalArgumentException("negative size: " + size);
        } else {
            // index > size
            return String.format("%s (%s) must not be greater than size (%s)", desc, index, size);
        }
    }

    /**
     * Reads <i>length</i> bytes from <i>source</i> into <i>dest</i> starting at <i>offset</i>. <br/>
     * <p/>
     * The only case where the <i>length</i> <tt>byte</tt>s will not be read is if <i>source</i> returns EOF.
     *
     * @param source The source of bytes to read from. Must not be <code>null</code>.
     * @param dest The <tt>byte[]</tt> to read bytes into. Must not be <code>null</code>.
     * @param offset The index in <i>dest</i> to start filling.
     * @param length The number of bytes to read.
     * @return Total number of bytes actually read.
     * @throws IndexOutOfBoundsException if <i>offset</i> or <i>length</i> are invalid.
     */
    static int readBytes(InputStream source, byte[] dest, int offset, int length) throws IOException {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    static int skip(InputStream source, int skip) throws IOException {
        throw new UnsupportedOperationException("STUB: not implemented");
    }
}
