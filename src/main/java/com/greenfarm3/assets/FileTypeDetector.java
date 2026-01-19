package com.greenfarm3.assets;

import java.io.IOException;
import java.io.InputStream;

/**
 * Utility class for detecting image file formats from magic bytes.
 * Supports PNG, JPEG, GIF, and BMP formats.
 */
public class FileTypeDetector {
    
    // Magic bytes for different image formats
    private static final byte[] PNG_HEADER = {(byte)0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
    private static final byte[] JPEG_HEADER = {(byte)0xFF, (byte)0xD8, (byte)0xFF};
    private static final byte[] GIF_HEADER_87 = {0x47, 0x49, 0x46, 0x38, 0x37, 0x61}; // GIF87a
    private static final byte[] GIF_HEADER_89 = {0x47, 0x49, 0x46, 0x38, 0x39, 0x61}; // GIF89a
    private static final byte[] BMP_HEADER = {0x42, 0x4D}; // BM
    
    /**
     * Detect image format from InputStream by reading magic bytes.
     * The stream will be reset after reading, so it can be used again.
     * 
     * @param is InputStream to detect format from
     * @return Format string ("PNG", "JPEG", "GIF", "BMP") or null if not a supported image format
     */
    public static String detectImageFormat(InputStream is) {
        if (is == null) {
            return null;
        }
        
        try {
            // Mark position so we can reset
            if (!is.markSupported()) {
                return null;
            }
            
            is.mark(16); // Read up to 16 bytes for detection
            
            byte[] header = new byte[16];
            int bytesRead = is.read(header);
            
            // Reset stream to beginning
            is.reset();
            
            if (bytesRead < 2) {
                return null; // Not enough data
            }
            
            return detectImageFormat(header, bytesRead);
        } catch (IOException e) {
            return null;
        }
    }
    
    /**
     * Detect image format from byte array header.
     * 
     * @param header Byte array containing file header
     * @param length Number of bytes actually read
     * @return Format string or null
     */
    public static String detectImageFormat(byte[] header, int length) {
        if (header == null || length < 2) {
            return null;
        }
        
        // Check PNG (needs at least 8 bytes)
        if (length >= 8 && matchesHeader(header, PNG_HEADER, 8)) {
            return "PNG";
        }
        
        // Check JPEG (needs at least 3 bytes)
        if (length >= 3 && matchesHeader(header, JPEG_HEADER, 3)) {
            return "JPEG";
        }
        
        // Check GIF87a (needs at least 6 bytes)
        if (length >= 6 && matchesHeader(header, GIF_HEADER_87, 6)) {
            return "GIF";
        }
        
        // Check GIF89a (needs at least 6 bytes)
        if (length >= 6 && matchesHeader(header, GIF_HEADER_89, 6)) {
            return "GIF";
        }
        
        // Check BMP (needs at least 2 bytes)
        if (length >= 2 && matchesHeader(header, BMP_HEADER, 2)) {
            return "BMP";
        }
        
        return null; // Unknown format
    }
    
    /**
     * Check if header matches a specific pattern.
     * 
     * @param header Byte array to check
     * @param pattern Pattern to match against
     * @param length Number of bytes to compare
     * @return true if header matches pattern
     */
    private static boolean matchesHeader(byte[] header, byte[] pattern, int length) {
        if (header.length < length || pattern.length < length) {
            return false;
        }
        
        for (int i = 0; i < length; i++) {
            if (header[i] != pattern[i]) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Check if a file is an image file based on header bytes.
     * 
     * @param header Byte array containing file header
     * @return true if header indicates an image file
     */
    public static boolean isImageFile(byte[] header) {
        return detectImageFormat(header, header != null ? header.length : 0) != null;
    }
    
    /**
     * Get file extension for a detected format.
     * 
     * @param format Format string ("PNG", "JPEG", "GIF", "BMP")
     * @return File extension (e.g., ".png", ".jpg") or null
     */
    public static String getFormatExtension(String format) {
        if (format == null) {
            return null;
        }
        
        switch (format.toUpperCase()) {
            case "PNG":
                return ".png";
            case "JPEG":
            case "JPG":
                return ".jpg";
            case "GIF":
                return ".gif";
            case "BMP":
                return ".bmp";
            default:
                return null;
        }
    }
    
    /**
     * Get MIME type for a detected format.
     * 
     * @param format Format string
     * @return MIME type or null
     */
    public static String getMimeType(String format) {
        if (format == null) {
            return null;
        }
        
        switch (format.toUpperCase()) {
            case "PNG":
                return "image/png";
            case "JPEG":
            case "JPG":
                return "image/jpeg";
            case "GIF":
                return "image/gif";
            case "BMP":
                return "image/bmp";
            default:
                return null;
        }
    }
    
    /**
     * Find PNG or JPEG signature in file data by scanning through the file.
     * This is useful for files with custom headers where the image data starts at an offset.
     * 
     * @param data Byte array containing file data
     * @return Offset where signature was found, or -1 if not found
     */
    public static int findImageSignatureInFile(byte[] data) {
        if (data == null || data.length < 3) {
            return -1;
        }
        
        // Search first 1KB thoroughly (most custom headers are small)
        int searchLimit = Math.min(1024, data.length - 8);
        for (int i = 0; i < searchLimit; i++) {
            if (isPNGAtOffset(data, i)) {
                return i;
            }
            if (isJPEGAtOffset(data, i)) {
                return i;
            }
        }
        
        // For larger files, sample at specific offsets
        // This handles cases where header might be larger than 1KB
        int[] sampleOffsets = {100, 500, 1000, 2000, 5000, 10000, 20000, 50000};
        for (int offset : sampleOffsets) {
            if (offset < data.length - 8) {
                if (isPNGAtOffset(data, offset)) {
                    return offset;
                }
                if (isJPEGAtOffset(data, offset)) {
                    return offset;
                }
            }
        }
        
        return -1; // Not found
    }
    
    /**
     * Check if PNG signature exists at a specific offset in the data.
     * PNG signature: 89 50 4E 47 0D 0A 1A 0A
     * 
     * @param data Byte array to check
     * @param offset Offset to check at
     * @return true if PNG signature found at offset
     */
    private static boolean isPNGAtOffset(byte[] data, int offset) {
        if (data == null || offset < 0 || offset + 8 > data.length) {
            return false;
        }
        
        return data[offset] == (byte)0x89 &&
               data[offset + 1] == 0x50 &&
               data[offset + 2] == 0x4E &&
               data[offset + 3] == 0x47 &&
               data[offset + 4] == 0x0D &&
               data[offset + 5] == 0x0A &&
               data[offset + 6] == 0x1A &&
               data[offset + 7] == 0x0A;
    }
    
    /**
     * Check if JPEG signature exists at a specific offset in the data.
     * JPEG signature: FF D8 FF
     * 
     * @param data Byte array to check
     * @param offset Offset to check at
     * @return true if JPEG signature found at offset
     */
    private static boolean isJPEGAtOffset(byte[] data, int offset) {
        if (data == null || offset < 0 || offset + 3 > data.length) {
            return false;
        }
        
        return data[offset] == (byte)0xFF &&
               data[offset + 1] == (byte)0xD8 &&
               data[offset + 2] == (byte)0xFF;
    }
    
    /**
     * Detect image format from data starting at a specific offset.
     * Useful after finding signature with findImageSignatureInFile().
     * 
     * @param data Byte array containing file data
     * @param offset Offset where image data starts
     * @return Format string ("PNG", "JPEG") or null
     */
    public static String detectFormatAtOffset(byte[] data, int offset) {
        if (data == null || offset < 0 || offset >= data.length) {
            return null;
        }
        
        if (isPNGAtOffset(data, offset)) {
            return "PNG";
        }
        
        if (isJPEGAtOffset(data, offset)) {
            return "JPEG";
        }
        
        return null;
    }
}
