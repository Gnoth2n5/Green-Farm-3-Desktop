package com.greenfarm3.assets;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Parser for J2ME custom binary resource format.
 * Based on analysis of decompiled code from g.java and m.java.
 * 
 * Format structure (similar to /dataIGP):
 * - 2 bytes: count (little-endian short)
 * - 4*count bytes: offset table (little-endian ints)
 * - Data sections starting after offset table
 */
public class J2MEResourceParser {
    
    /**
     * Parse offset table from J2ME resource file.
     * Format: [2 bytes: count][4*count bytes: offsets]
     * 
     * @param data File data
     * @return Array of offsets, or null if invalid
     */
    public static int[] parseOffsetTable(byte[] data) {
        if (data == null || data.length < 2) {
            return null;
        }
        
        // Read count (2 bytes, little-endian)
        int count = readLittleEndianShort(data, 0);
        
        if (count < 0 || count > 10000) {
            // Sanity check: count should be reasonable
            return null;
        }
        
        // Calculate required size: 2 (count) + 4*count (offsets) + at least 1 offset for end
        int requiredSize = 2 + 4 * (count + 1);
        if (data.length < requiredSize) {
            return null;
        }
        
        // Read offset table (count + 1 offsets, where last is end offset)
        int[] offsets = new int[count + 1];
        for (int i = 0; i <= count; i++) {
            offsets[i] = readLittleEndianInt(data, 2 + i * 4);
            
            // Validate offset is within file bounds
            if (offsets[i] < 0 || offsets[i] > data.length) {
                return null;
            }
        }
        
        return offsets;
    }
    
    /**
     * Read data section at given index from offset table.
     * Based on m.java: inputStream.skip(2 + 4 * ab + b[n2])
     * 
     * @param data Full file data
     * @param offsets Offset table (from parseOffsetTable, has count+1 entries)
     * @param index Index into offset table (0-based)
     * @return Data section as byte array, or null if invalid index
     */
    public static byte[] readDataSection(byte[] data, int[] offsets, int index) {
        if (data == null || offsets == null || index < 0 || index >= offsets.length - 1) {
            return null;
        }
        
        // Get count (first entry in offsets array is actually count, but we use offsets.length-1)
        int count = offsets.length - 1;
        
        // Calculate header size: 2 bytes (count) + 4*count bytes (offset table)
        int headerSize = 2 + 4 * count;
        
        // Get offsets (relative to start of data, after header)
        int startOffset = offsets[index];
        int endOffset = offsets[index + 1];
        int size = endOffset - startOffset;
        
        if (size < 0) {
            return null;
        }
        
        // Calculate actual positions in file
        int actualStart = headerSize + startOffset;
        int actualEnd = headerSize + endOffset;
        
        if (actualStart < 0 || actualEnd > data.length || actualStart >= actualEnd) {
            return null;
        }
        
        byte[] section = new byte[size];
        System.arraycopy(data, actualStart, section, 0, size);
        
        return section;
    }
    
    /**
     * Read little-endian short (2 bytes) from byte array.
     * 
     * @param data Byte array
     * @param offset Offset to read from
     * @return Short value (as int, unsigned)
     */
    public static int readLittleEndianShort(byte[] data, int offset) {
        if (data == null || offset < 0 || offset + 1 >= data.length) {
            return 0;
        }
        return (data[offset] & 0xFF) | ((data[offset + 1] & 0xFF) << 8);
    }
    
    /**
     * Read little-endian int (4 bytes) from byte array.
     * 
     * @param data Byte array
     * @param offset Offset to read from
     * @return Int value
     */
    public static int readLittleEndianInt(byte[] data, int offset) {
        if (data == null || offset < 0 || offset + 3 >= data.length) {
            return 0;
        }
        return (data[offset] & 0xFF) |
               ((data[offset + 1] & 0xFF) << 8) |
               ((data[offset + 2] & 0xFF) << 16) |
               ((data[offset + 3] & 0xFF) << 24);
    }
    
    /**
     * Detect if data appears to be J2ME resource format.
     * Checks for valid count and offset table structure.
     * 
     * @param data File data
     * @return true if appears to be J2ME format
     */
    public static boolean isJ2MEFormat(byte[] data) {
        if (data == null || data.length < 6) {
            return false;
        }
        
        int count = readLittleEndianShort(data, 0);
        
        // Sanity checks
        if (count < 0 || count > 10000) {
            return false;
        }
        
        // Check if we have enough data for header + at least one offset
        if (data.length < 2 + 4 * (count + 1)) {
            return false;
        }
        
        // Check if offsets are in reasonable range
        int firstOffset = readLittleEndianInt(data, 2);
        if (firstOffset < 0 || firstOffset > data.length) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Get the number of data sections in the file.
     * 
     * @param data File data
     * @return Number of sections, or -1 if invalid format
     */
    public static int getSectionCount(byte[] data) {
        if (data == null || data.length < 2) {
            return -1;
        }
        
        int count = readLittleEndianShort(data, 0);
        if (count < 0 || count > 10000) {
            return -1;
        }
        
        return count;
    }
}
