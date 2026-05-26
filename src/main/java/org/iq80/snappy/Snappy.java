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

import java.util.Arrays;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static sun.misc.Unsafe.ARRAY_BYTE_BASE_OFFSET;

public final class Snappy {

    private Snappy() {
    }

    public static int getUncompressedLength(byte[] compressed, int compressedOffset) throws CorruptionException {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    public static byte[] uncompress(byte[] compressed, int compressedOffset, int compressedSize) throws CorruptionException {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    public static int uncompress(byte[] compressed, int compressedOffset, int compressedSize, byte[] uncompressed, int uncompressedOffset) throws CorruptionException {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    public static int uncompress(byte[] compressed, int compressedOffset, int compressedSize, byte[] uncompressed, int uncompressedOffset, int uncompressedLength) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    public static int maxCompressedLength(int sourceLength) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    public static int compress(byte[] uncompressed, int uncompressedOffset, int uncompressedLength, byte[] compressed, int compressedOffset) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    public static int compress(CompressionContext context, byte[] uncompressed, int uncompressedOffset, int uncompressedLength, byte[] compressed, int compressedOffset, int maxCompressedLength) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    public static byte[] compress(byte[] data) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    public static final class CompressionContext {

        private final short[] table = new short[SnappyRawCompressor.MAX_HASH_TABLE_SIZE];

        private short[] getTable() {
            return table;
        }
    }

    private static void verifyRange(byte[] data, int offset, int length) {
        requireNonNull(data, "data is null");
        if (offset < 0 || length < 0 || offset + length > data.length) {
            throw new IllegalArgumentException(format("Invalid offset or length (%s, %s) in array of length %s", offset, length, data.length));
        }
    }
}
