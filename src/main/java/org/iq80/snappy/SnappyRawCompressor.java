/*
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
import static org.iq80.snappy.SnappyConstants.COPY_1_BYTE_OFFSET;
import static org.iq80.snappy.SnappyConstants.COPY_2_BYTE_OFFSET;
import static org.iq80.snappy.SnappyConstants.SIZE_OF_INT;
import static org.iq80.snappy.SnappyConstants.SIZE_OF_LONG;
import static org.iq80.snappy.SnappyConstants.SIZE_OF_SHORT;
import static org.iq80.snappy.UnsafeUtil.UNSAFE;

final class SnappyRawCompressor {

    // The size of a compression block. Note that many parts of the compression
    // code assumes that BLOCK_SIZE <= 65536; in particular, the hash table
    // can only store 16-bit offsets, and EmitCopy() also assumes the offset
    // is 65535 bytes or less. Note also that if you change this, it will
    // affect the framing format (see framing_format.txt).
    //
    // Note that there might be older data around that is compressed with larger
    // block sizes, so the decompression code should not rely on the
    // non-existence of long back-references.
    private static final int BLOCK_LOG = 16;

    private static final int BLOCK_SIZE = 1 << BLOCK_LOG;

    private static final int INPUT_MARGIN_BYTES = 15;

    private static final int MAX_HASH_TABLE_BITS = 14;

    public static final int MAX_HASH_TABLE_SIZE = 1 << MAX_HASH_TABLE_BITS;

    private SnappyRawCompressor() {
    }

    public static int maxCompressedLength(int sourceLength) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    // suppress warnings is required to use assert
    @SuppressWarnings("IllegalToken")
    public static int compress(final Object inputBase, final long inputAddress, final long inputLimit, final Object outputBase, final long outputAddress, final long outputLimit, final short[] table) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    private static int count(Object inputBase, final long start, long matchStart, long matchLimit) {
        long current = start;
        // first, compare long at a time
        while (current < matchLimit - (SIZE_OF_LONG - 1)) {
            long diff = UNSAFE.getLong(inputBase, matchStart) ^ UNSAFE.getLong(inputBase, current);
            if (diff != 0) {
                current += Long.numberOfTrailingZeros(diff) >> 3;
                return (int) (current - start);
            }
            current += SIZE_OF_LONG;
            matchStart += SIZE_OF_LONG;
        }
        if (current < matchLimit - (SIZE_OF_INT - 1) && UNSAFE.getInt(inputBase, matchStart) == UNSAFE.getInt(inputBase, current)) {
            current += SIZE_OF_INT;
            matchStart += SIZE_OF_INT;
        }
        if (current < matchLimit - (SIZE_OF_SHORT - 1) && UNSAFE.getShort(inputBase, matchStart) == UNSAFE.getShort(inputBase, current)) {
            current += SIZE_OF_SHORT;
            matchStart += SIZE_OF_SHORT;
        }
        if (current < matchLimit && UNSAFE.getByte(inputBase, matchStart) == UNSAFE.getByte(inputBase, current)) {
            ++current;
        }
        return (int) (current - start);
    }

    private static long emitLiteralLength(Object outputBase, long output, int literalLength) {
        // Zero-length literals are disallowed
        int n = literalLength - 1;
        if (n < 60) {
            // Size fits in tag byte
            UNSAFE.putByte(outputBase, output++, (byte) (n << 2));
        } else {
            int bytes;
            if (n < (1 << 8)) {
                UNSAFE.putByte(outputBase, output++, (byte) (59 + 1 << 2));
                bytes = 1;
            } else if (n < (1 << 16)) {
                UNSAFE.putByte(outputBase, output++, (byte) (59 + 2 << 2));
                bytes = 2;
            } else if (n < (1 << 24)) {
                UNSAFE.putByte(outputBase, output++, (byte) (59 + 3 << 2));
                bytes = 3;
            } else {
                UNSAFE.putByte(outputBase, output++, (byte) (59 + 4 << 2));
                bytes = 4;
            }
            // System is assumed to be little endian, so low bytes will be zero for the smaller numbers
            UNSAFE.putInt(outputBase, output, n);
            output += bytes;
        }
        return output;
    }

    private static long fastCopy(final Object inputBase, long input, final Object outputBase, long output, final int literalLength) {
        final long outputLimit = output + literalLength;
        do {
            UNSAFE.putLong(outputBase, output, UNSAFE.getLong(inputBase, input));
            input += SIZE_OF_LONG;
            output += SIZE_OF_LONG;
        } while (output < outputLimit);
        return outputLimit;
    }

    private static long emitCopy(Object outputBase, long output, long input, long matchIndex, int matchLength) {
        long offset = input - matchIndex;
        // Emit 64 byte copies but make sure to keep at least four bytes reserved
        while (matchLength >= 68) {
            UNSAFE.putByte(outputBase, output++, (byte) (COPY_2_BYTE_OFFSET + ((64 - 1) << 2)));
            UNSAFE.putShort(outputBase, output, (short) offset);
            output += SIZE_OF_SHORT;
            matchLength -= 64;
        }
        // Emit an extra 60 byte copy if have too much data to fit in one copy
        // length < 68
        if (matchLength > 64) {
            UNSAFE.putByte(outputBase, output++, (byte) (COPY_2_BYTE_OFFSET + ((60 - 1) << 2)));
            UNSAFE.putShort(outputBase, output, (short) offset);
            output += SIZE_OF_SHORT;
            matchLength -= 60;
        }
        // Emit remainder
        if ((matchLength < 12) && (offset < 2048)) {
            int lenMinus4 = matchLength - 4;
            UNSAFE.putByte(outputBase, output++, (byte) (COPY_1_BYTE_OFFSET + ((lenMinus4) << 2) + ((offset >>> 8) << 5)));
            UNSAFE.putByte(outputBase, output++, (byte) (offset));
        } else {
            UNSAFE.putByte(outputBase, output++, (byte) (COPY_2_BYTE_OFFSET + ((matchLength - 1) << 2)));
            UNSAFE.putShort(outputBase, output, (short) offset);
            output += SIZE_OF_SHORT;
        }
        return output;
    }

    @SuppressWarnings("IllegalToken")
    private static int getHashTableSize(int inputSize) {
        // Use smaller hash table when input.size() is smaller, since we
        // fill the table, incurring O(hash table size) overhead for
        // compression, and if the input is short, we won't need that
        // many hash table entries anyway.
        assert (MAX_HASH_TABLE_SIZE >= 256);
        // smallest power of 2 larger than inputSize
        int target = Integer.highestOneBit(inputSize - 1) << 1;
        // keep it between MIN_TABLE_SIZE and MAX_TABLE_SIZE
        return Math.max(Math.min(target, MAX_HASH_TABLE_SIZE), 256);
    }

    // Any hash function will produce a valid compressed stream, but a good
    // hash function reduces the number of collisions and thus yields better
    // compression for compressible input, and more speed for incompressible
    // input. Of course, it doesn't hurt if the hash function is reasonably fast
    // either, as it gets called a lot.
    private static int hashBytes(int value, int shift) {
        return (value * 0x1e35a7bd) >>> shift;
    }

    private static int log2Floor(int n) {
        return n == 0 ? -1 : 31 ^ Integer.numberOfLeadingZeros(n);
    }

    private static final int HIGH_BIT_MASK = 0x80;

    /**
     * Writes the uncompressed length as variable length integer.
     */
    private static long writeUncompressedLength(Object outputBase, long outputAddress, int uncompressedLength) {
        if (uncompressedLength < (1 << 7) && uncompressedLength >= 0) {
            UNSAFE.putByte(outputBase, outputAddress++, (byte) (uncompressedLength));
        } else if (uncompressedLength < (1 << 14) && uncompressedLength > 0) {
            UNSAFE.putByte(outputBase, outputAddress++, (byte) (uncompressedLength | HIGH_BIT_MASK));
            UNSAFE.putByte(outputBase, outputAddress++, (byte) (uncompressedLength >>> 7));
        } else if (uncompressedLength < (1 << 21) && uncompressedLength > 0) {
            UNSAFE.putByte(outputBase, outputAddress++, (byte) (uncompressedLength | HIGH_BIT_MASK));
            UNSAFE.putByte(outputBase, outputAddress++, (byte) ((uncompressedLength >>> 7) | HIGH_BIT_MASK));
            UNSAFE.putByte(outputBase, outputAddress++, (byte) (uncompressedLength >>> 14));
        } else if (uncompressedLength < (1 << 28) && uncompressedLength > 0) {
            UNSAFE.putByte(outputBase, outputAddress++, (byte) (uncompressedLength | HIGH_BIT_MASK));
            UNSAFE.putByte(outputBase, outputAddress++, (byte) ((uncompressedLength >>> 7) | HIGH_BIT_MASK));
            UNSAFE.putByte(outputBase, outputAddress++, (byte) ((uncompressedLength >>> 14) | HIGH_BIT_MASK));
            UNSAFE.putByte(outputBase, outputAddress++, (byte) (uncompressedLength >>> 21));
        } else {
            UNSAFE.putByte(outputBase, outputAddress++, (byte) (uncompressedLength | HIGH_BIT_MASK));
            UNSAFE.putByte(outputBase, outputAddress++, (byte) ((uncompressedLength >>> 7) | HIGH_BIT_MASK));
            UNSAFE.putByte(outputBase, outputAddress++, (byte) ((uncompressedLength >>> 14) | HIGH_BIT_MASK));
            UNSAFE.putByte(outputBase, outputAddress++, (byte) ((uncompressedLength >>> 21) | HIGH_BIT_MASK));
            UNSAFE.putByte(outputBase, outputAddress++, (byte) (uncompressedLength >>> 28));
        }
        return outputAddress;
    }
}
