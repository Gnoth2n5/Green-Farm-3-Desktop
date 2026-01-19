package com.greenfarm3.assets;

import javafx.scene.image.Image;
import java.awt.image.BufferedImage;

/**
 * Parser for RGB image data from J2ME format.
 * Converts RGB int arrays to JavaFX Images, similar to J2ME's Image.createRGBImage().
 */
public class RGBImageParser {
    
    /**
     * Parse RGB array from byte data.
     * Supports ARGB format (4 bytes per pixel: Alpha, Red, Green, Blue).
     * 
     * @param data Byte array containing RGB data
     * @param width Image width
     * @param height Image height
     * @return RGB int array, or null if invalid
     */
    public static int[] parseRGBArray(byte[] data, int width, int height) {
        if (data == null || width <= 0 || height <= 0) {
            return null;
        }
        
        int pixelCount = width * height;
        int expectedSize = pixelCount * 4; // ARGB = 4 bytes per pixel
        
        if (data.length < expectedSize) {
            // Try RGB format (3 bytes per pixel) if ARGB doesn't fit
            if (data.length >= pixelCount * 3) {
                return parseRGBArray3Bytes(data, width, height);
            }
            return null;
        }
        
        int[] rgb = new int[pixelCount];
        int offset = 0;
        
        // Parse ARGB format (4 bytes per pixel)
        for (int i = 0; i < pixelCount; i++) {
            int a = data[offset++] & 0xFF;
            int r = data[offset++] & 0xFF;
            int g = data[offset++] & 0xFF;
            int b = data[offset++] & 0xFF;
            rgb[i] = (a << 24) | (r << 16) | (g << 8) | b;
        }
        
        return rgb;
    }
    
    /**
     * Parse RGB array from 3-byte RGB format (no alpha).
     * 
     * @param data Byte array containing RGB data
     * @param width Image width
     * @param height Image height
     * @return RGB int array with alpha=255, or null if invalid
     */
    private static int[] parseRGBArray3Bytes(byte[] data, int width, int height) {
        int pixelCount = width * height;
        int[] rgb = new int[pixelCount];
        int offset = 0;
        
        // Parse RGB format (3 bytes per pixel, alpha = 255)
        for (int i = 0; i < pixelCount; i++) {
            int r = data[offset++] & 0xFF;
            int g = data[offset++] & 0xFF;
            int b = data[offset++] & 0xFF;
            rgb[i] = (0xFF << 24) | (r << 16) | (g << 8) | b; // Alpha = 255
        }
        
        return rgb;
    }
    
    /**
     * Convert RGB array to BufferedImage.
     * Similar to J2ME's Image.createRGBImage().
     * 
     * @param rgb RGB int array (ARGB format)
     * @param width Image width
     * @param height Image height
     * @param processAlpha Whether to process alpha channel
     * @return BufferedImage, or null if invalid
     */
    public static BufferedImage rgbArrayToBufferedImage(int[] rgb, int width, int height, boolean processAlpha) {
        if (rgb == null || width <= 0 || height <= 0 || rgb.length < width * height) {
            return null;
        }
        
        int type = processAlpha ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
        BufferedImage bufferedImage = new BufferedImage(width, height, type);
        
        // Set RGB data
        bufferedImage.setRGB(0, 0, width, height, rgb, 0, width);
        
        return bufferedImage;
    }
    
    /**
     * Convert RGB array to JavaFX Image.
     * 
     * @param rgb RGB int array (ARGB format)
     * @param width Image width
     * @param height Image height
     * @param processAlpha Whether to process alpha channel
     * @return JavaFX Image, or null if invalid
     */
    public static Image rgbArrayToJavaFXImage(int[] rgb, int width, int height, boolean processAlpha) {
        BufferedImage bufferedImage = rgbArrayToBufferedImage(rgb, width, height, processAlpha);
        if (bufferedImage == null) {
            return null;
        }
        
        return ImageConverter.bufferedImageToJavaFX(bufferedImage);
    }
    
    /**
     * Try to detect width and height from RGB data size.
     * Uses heuristics: assumes square or common aspect ratios.
     * 
     * @param dataSize Size of RGB data in bytes
     * @param hasAlpha Whether data includes alpha channel
     * @return Array [width, height] or null if cannot determine
     */
    public static int[] detectDimensions(int dataSize, boolean hasAlpha) {
        if (dataSize <= 0) {
            return null;
        }
        
        int bytesPerPixel = hasAlpha ? 4 : 3;
        int pixelCount = dataSize / bytesPerPixel;
        
        if (pixelCount <= 0) {
            return null;
        }
        
        // Try square first
        int sqrt = (int) Math.sqrt(pixelCount);
        if (sqrt * sqrt == pixelCount) {
            return new int[]{sqrt, sqrt};
        }
        
        // Try common aspect ratios
        int[][] commonRatios = {
            {16, 9}, {4, 3}, {3, 2}, {2, 1}, {1, 1}
        };
        
        for (int[] ratio : commonRatios) {
            int w = ratio[0];
            int h = ratio[1];
            if (pixelCount % (w * h) == 0) {
                int scale = pixelCount / (w * h);
                int width = w * scale;
                int height = h * scale;
                if (width > 0 && height > 0) {
                    return new int[]{width, height};
                }
            }
        }
        
        // Try to find factors
        for (int w = 1; w <= Math.sqrt(pixelCount); w++) {
            if (pixelCount % w == 0) {
                int h = pixelCount / w;
                if (h > 0) {
                    return new int[]{w, h};
                }
            }
        }
        
        return null;
    }
    
    /**
     * Parse RGB array and try to auto-detect dimensions.
     * 
     * @param data Byte array containing RGB data
     * @param hasAlpha Whether data includes alpha channel
     * @return Array [rgbArray, width, height] or null if cannot parse
     */
    public static RGBImageData parseRGBArrayWithAutoDimensions(byte[] data, boolean hasAlpha) {
        if (data == null || data.length == 0) {
            return null;
        }
        
        int[] dimensions = detectDimensions(data.length, hasAlpha);
        if (dimensions == null) {
            return null;
        }
        
        int width = dimensions[0];
        int height = dimensions[1];
        int[] rgb = parseRGBArray(data, width, height);
        
        if (rgb == null) {
            return null;
        }
        
        return new RGBImageData(rgb, width, height);
    }
    
    /**
     * Data class for RGB image with dimensions.
     */
    public static class RGBImageData {
        public final int[] rgb;
        public final int width;
        public final int height;
        
        public RGBImageData(int[] rgb, int width, int height) {
            this.rgb = rgb;
            this.width = width;
            this.height = height;
        }
    }
}
