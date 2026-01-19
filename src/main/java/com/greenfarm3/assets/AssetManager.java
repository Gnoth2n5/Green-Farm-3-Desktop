package com.greenfarm3.assets;

import javafx.scene.image.Image;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.imageio.ImageIO;

/**
 * Manages loading and caching of game assets (images, sounds).
 * Replaces J2ME resource loading.
 */
public class AssetManager {
    
    private static AssetManager instance;
    private final Map<String, Image> images;
    private final Set<String> skippedFiles; // Track files we've already logged as skipped
    private boolean debugMode = true;
    
    private AssetManager() {
        this.images = new HashMap<>();
        this.skippedFiles = new HashSet<>();
    }
    
    public static AssetManager getInstance() {
        if (instance == null) {
            instance = new AssetManager();
        }
        return instance;
    }
    
    /**
     * Load an image from resources
     * @param path Path to image resource (e.g., "/images/icon.png")
     * @return Loaded Image, or null if failed
     */
    public Image loadImage(String path) {
        if (images.containsKey(path)) {
            if (debugMode) {
                System.out.println("[AssetManager] Using cached image: " + path);
            }
            return images.get(path);
        }
        
        try {
            InputStream is = getClass().getResourceAsStream(path);
            if (is == null) {
                if (debugMode) {
                    System.out.println("[AssetManager] Image not found: " + path);
                }
                return null;
            }
            
            // If path has no extension, try to detect format
            if (!path.contains(".") || path.endsWith("/")) {
                return loadImageFromStream(is, path);
            }
            
            // Load with known extension
            Image image = new Image(is);
            if (image.isError()) {
                if (debugMode) {
                    System.err.println("[AssetManager] Failed to load image: " + path + " - Image error");
                }
                return null;
            }
            
            // Validate image
            if (!validateImage(image, path)) {
                return null;
            }
            
            images.put(path, image);
            if (debugMode) {
                System.out.println("[AssetManager] Loaded image: " + path + 
                    " (" + (int)image.getWidth() + "x" + (int)image.getHeight() + ")");
            }
            return image;
        } catch (Exception e) {
            if (debugMode) {
                System.err.println("[AssetManager] Exception loading image: " + path + " - " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }
    }
    
    /**
     * Load an image using ImageIO (with TwelveMonkeys extensions).
     * This method tries ImageIO first, which can handle many formats automatically.
     * 
     * @param is InputStream to load from
     * @param path Path for caching and logging
     * @return Loaded Image, or null if failed
     */
    private Image loadImageWithImageIO(InputStream is, String path) {
        try {
            // Try to load with ImageIO (supports many formats including custom ones)
            BufferedImage bufferedImage = ImageIO.read(is);
            
            if (bufferedImage == null) {
                if (debugMode) {
                    System.out.println("[AssetManager] ImageIO could not read image: " + path);
                }
                return null;
            }
            
            // Validate BufferedImage
            if (!ImageConverter.isValidBufferedImage(bufferedImage)) {
                if (debugMode) {
                    System.err.println("[AssetManager] Invalid BufferedImage: " + path + 
                        " (width: " + bufferedImage.getWidth() + ", height: " + bufferedImage.getHeight() + ")");
                }
                return null;
            }
            
            // Convert BufferedImage to JavaFX Image
            Image javafxImage = ImageConverter.bufferedImageToJavaFX(bufferedImage);
            
            if (javafxImage == null || javafxImage.isError()) {
                if (debugMode) {
                    System.err.println("[AssetManager] Failed to convert BufferedImage to JavaFX Image: " + path);
                }
                return null;
            }
            
            // Validate JavaFX Image
            if (!validateImage(javafxImage, path)) {
                return null;
            }
            
            // Get format info if available
            String formatInfo = "ImageIO";
            try {
                String[] readerFormatNames = ImageIO.getReaderFormatNames();
                if (readerFormatNames != null && readerFormatNames.length > 0) {
                    formatInfo = "ImageIO/" + readerFormatNames[0];
                }
            } catch (Exception e) {
                // Ignore format detection errors
            }
            
            // Cache with ImageIO indicator
            String cacheKey = path + "[ImageIO]";
            images.put(cacheKey, javafxImage);
            
            if (debugMode) {
                System.out.println("[AssetManager] Loaded image with ImageIO: " + path + 
                    " (format: " + formatInfo + ", " + 
                    (int)javafxImage.getWidth() + "x" + (int)javafxImage.getHeight() + ")");
            }
            
            return javafxImage;
        } catch (IOException e) {
            if (debugMode) {
                System.err.println("[AssetManager] IOException loading with ImageIO: " + path + " - " + e.getMessage());
            }
            return null;
        } catch (Exception e) {
            if (debugMode) {
                System.err.println("[AssetManager] Exception loading with ImageIO: " + path + " - " + e.getMessage());
            }
            return null;
        }
    }
    
    /**
     * Load an image from InputStream with format detection.
     * Detects format from magic bytes if file has no extension.
     * 
     * @param is InputStream to load from
     * @param path Path for caching and logging
     * @return Loaded Image, or null if failed
     */
    private Image loadImageFromStream(InputStream is, String path) {
        // Strategy 1: Try ImageIO first (best compatibility, can handle many formats)
        try {
            // Need to read stream to byte array first (ImageIO needs to read it, and we may need it again)
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] data = new byte[8192];
            int nRead;
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            byte[] imageData = buffer.toByteArray();
            
            // Try ImageIO
            Image imageIO = loadImageWithImageIO(new ByteArrayInputStream(imageData), path);
            if (imageIO != null) {
                return imageIO; // Success with ImageIO
            }
            
            // Strategy 2: Try format detection and JavaFX direct load
            String format = FileTypeDetector.detectImageFormat(new ByteArrayInputStream(imageData));
            
            if (format != null) {
                if (debugMode) {
                    System.out.println("[AssetManager] Detected format: " + format + " for " + path + 
                        " (fallback after ImageIO failed)");
                }
                
                // Try JavaFX Image direct load
                Image image = new Image(new ByteArrayInputStream(imageData));
                
                if (!image.isError() && validateImage(image, path)) {
                    // Cache with detected format
                    String cacheKey = path + "[" + format + "]";
                    images.put(cacheKey, image);
                    
                    if (debugMode) {
                        System.out.println("[AssetManager] Loaded image with JavaFX: " + path + 
                            " (format: " + format + ", " + 
                            (int)image.getWidth() + "x" + (int)image.getHeight() + ")");
                    }
                    return image;
                }
            }
            
            // Strategy 3: NEW - Try J2ME custom format parser (for numbered resource files)
            Image j2meImage = loadJ2MEFormat(imageData, path);
            if (j2meImage != null) {
                return j2meImage; // Success with J2ME format
            }
            
            // Strategy 4: Try deep signature search + extract (for custom formats with headers)
            Image extractedImage = extractImageFromCustomFormat(imageData, path);
            if (extractedImage != null) {
                return extractedImage; // Success with extraction
            }
            
            // Strategy 5: Try JavaFX direct load without format detection (last resort)
            Image directImage = new Image(new ByteArrayInputStream(imageData));
            if (!directImage.isError() && validateImage(directImage, path)) {
                String cacheKey = path + "[Direct]";
                images.put(cacheKey, directImage);
                
                if (debugMode) {
                    System.out.println("[AssetManager] Loaded image with direct JavaFX: " + path + 
                        " (" + (int)directImage.getWidth() + "x" + (int)directImage.getHeight() + ")");
                }
                return directImage;
            }
            
            // All strategies failed - only log once per file
            if (!skippedFiles.contains(path)) {
                skippedFiles.add(path);
                if (debugMode) {
                    String hex = bytesToHex(imageData, 0, Math.min(16, imageData.length));
                    System.out.println("[AssetManager] Skipping non-image file: " + path + 
                        " (size: " + imageData.length + " bytes, first bytes: " + hex + 
                        " - tried ImageIO, format detection, deep scan/extract, and direct load)");
                }
            }
            return null;
            
        } catch (IOException e) {
            if (debugMode) {
                System.err.println("[AssetManager] IOException loading image: " + path + " - " + e.getMessage());
            }
            return null;
        } catch (Exception e) {
            if (debugMode) {
                System.err.println("[AssetManager] Exception loading image: " + path + " - " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }
    }
    
    /**
     * Load image from J2ME custom binary format.
     * Parses offset table and extracts RGB image data.
     * 
     * @param data Byte array containing J2ME format data
     * @param path Path for caching and logging
     * @return Loaded Image, or null if failed
     */
    private Image loadJ2MEFormat(byte[] data, String path) {
        if (data == null || data.length < 6) {
            return null;
        }
        
        // Check if it's J2ME format
        if (!J2MEResourceParser.isJ2MEFormat(data)) {
            if (debugMode) {
                System.out.println("[AssetManager] File does not appear to be J2ME format: " + path);
            }
            return null;
        }
        
        try {
            // Parse offset table
            int[] offsets = J2MEResourceParser.parseOffsetTable(data);
            if (offsets == null || offsets.length < 2) {
                if (debugMode) {
                    System.out.println("[AssetManager] Could not parse offset table: " + path);
                }
                return null;
            }
            
            int sectionCount = offsets.length - 1;
            if (debugMode) {
                System.out.println("[AssetManager] J2ME format detected: " + path + 
                    " (sections: " + sectionCount + ")");
            }
            
            // Try to load first section as image (most numbered files have single image)
            // If that fails, try other sections
            for (int sectionIndex = 0; sectionIndex < sectionCount; sectionIndex++) {
                byte[] sectionData = J2MEResourceParser.readDataSection(data, offsets, sectionIndex);
                if (sectionData == null || sectionData.length == 0) {
                    continue;
                }
                
                if (debugMode) {
                    System.out.println("[AssetManager] Trying section " + sectionIndex + 
                        " (size: " + sectionData.length + " bytes)");
                }
                
                // Try to parse as RGB image with auto-detected dimensions
                RGBImageParser.RGBImageData rgbData = 
                    RGBImageParser.parseRGBArrayWithAutoDimensions(sectionData, true);
                
                if (rgbData == null) {
                    // Try without alpha
                    rgbData = RGBImageParser.parseRGBArrayWithAutoDimensions(sectionData, false);
                }
                
                if (rgbData != null) {
                    // Convert to JavaFX Image
                    Image javafxImage = RGBImageParser.rgbArrayToJavaFXImage(
                        rgbData.rgb, rgbData.width, rgbData.height, true);
                    
                    if (javafxImage != null && !javafxImage.isError() && 
                        validateImage(javafxImage, path)) {
                        
                        // Cache with J2ME format indicator
                        String cacheKey = path + "[J2ME-section" + sectionIndex + "]";
                        images.put(cacheKey, javafxImage);
                        
                        if (debugMode) {
                            System.out.println("[AssetManager] Loaded J2ME image: " + path + 
                                " (section: " + sectionIndex + ", " + 
                                rgbData.width + "x" + rgbData.height + ")");
                        }
                        
                        return javafxImage;
                    }
                }
            }
            
            if (debugMode) {
                System.out.println("[AssetManager] Could not parse any section as RGB image: " + path);
            }
            return null;
            
        } catch (Exception e) {
            if (debugMode) {
                System.err.println("[AssetManager] Exception loading J2ME format: " + path + 
                    " - " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }
    }
    
    /**
     * Extract image from custom format by finding PNG/JPEG signature in file data.
     * This handles files with custom headers where image data starts at an offset.
     * 
     * @param data Byte array containing file data (may have custom header)
     * @param path Path for caching and logging
     * @return Loaded Image, or null if failed
     */
    private Image extractImageFromCustomFormat(byte[] data, String path) {
        if (data == null || data.length < 3) {
            return null;
        }
        
        // Find image signature in file
        int signatureOffset = FileTypeDetector.findImageSignatureInFile(data);
        
        if (signatureOffset < 0) {
            // No signature found
            if (debugMode) {
                System.out.println("[AssetManager] No image signature found in file: " + path);
            }
            return null;
        }
        
        // Detect format at offset
        String format = FileTypeDetector.detectFormatAtOffset(data, signatureOffset);
        if (format == null) {
            if (debugMode) {
                System.out.println("[AssetManager] Could not detect format at offset " + signatureOffset + " for: " + path);
            }
            return null;
        }
        
        if (debugMode) {
            System.out.println("[AssetManager] Found " + format + " signature at offset " + signatureOffset + " in: " + path);
        }
        
        // Extract image data from signature offset to end of file
        byte[] extractedData = Arrays.copyOfRange(data, signatureOffset, data.length);
        
        // Try to load with ImageIO
        try {
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(extractedData));
            
            if (bufferedImage == null) {
                if (debugMode) {
                    System.err.println("[AssetManager] ImageIO could not read extracted image: " + path);
                }
                return null;
            }
            
            // Validate BufferedImage
            if (!ImageConverter.isValidBufferedImage(bufferedImage)) {
                if (debugMode) {
                    System.err.println("[AssetManager] Invalid extracted BufferedImage: " + path + 
                        " (width: " + bufferedImage.getWidth() + ", height: " + bufferedImage.getHeight() + ")");
                }
                return null;
            }
            
            // Convert to JavaFX Image
            Image javafxImage = ImageConverter.bufferedImageToJavaFX(bufferedImage);
            
            if (javafxImage == null || javafxImage.isError()) {
                if (debugMode) {
                    System.err.println("[AssetManager] Failed to convert extracted image to JavaFX Image: " + path);
                }
                return null;
            }
            
            // Validate JavaFX Image
            if (!validateImage(javafxImage, path)) {
                return null;
            }
            
            // Cache with extracted format indicator
            String cacheKey = path + "[Extracted-" + format + "@" + signatureOffset + "]";
            images.put(cacheKey, javafxImage);
            
            if (debugMode) {
                System.out.println("[AssetManager] Extracted and loaded image: " + path + 
                    " (format: " + format + ", offset: " + signatureOffset + ", " + 
                    (int)javafxImage.getWidth() + "x" + (int)javafxImage.getHeight() + ")");
            }
            
            return javafxImage;
        } catch (IOException e) {
            if (debugMode) {
                System.err.println("[AssetManager] IOException extracting image: " + path + " - " + e.getMessage());
            }
            return null;
        } catch (Exception e) {
            if (debugMode) {
                System.err.println("[AssetManager] Exception extracting image: " + path + " - " + e.getMessage());
            }
            return null;
        }
    }
    
    /**
     * Convert bytes to hex string for logging.
     */
    private String bytesToHex(byte[] bytes, int offset, int length) {
        StringBuilder hex = new StringBuilder();
        int end = Math.min(offset + length, bytes.length);
        for (int i = offset; i < end; i++) {
            hex.append(String.format("%02X ", bytes[i] & 0xFF));
        }
        return hex.toString().trim();
    }
    
    /**
     * Validate loaded image (check width/height > 0).
     * 
     * @param image Image to validate
     * @param path Path for logging
     * @return true if image is valid
     */
    private boolean validateImage(Image image, String path) {
        if (image == null) {
            return false;
        }
        
        double width = image.getWidth();
        double height = image.getHeight();
        
        if (width <= 0 || height <= 0 || Double.isNaN(width) || Double.isNaN(height)) {
            if (debugMode) {
                System.err.println("[AssetManager] Invalid image: " + path + 
                    " (width: " + width + ", height: " + height + ")");
            }
            return false;
        }
        
        return true;
    }
    
    /**
     * Load an image by number (for numbered asset files like 0, 1, 2, etc.)
     * Uses ImageIO first (best compatibility), then falls back to format detection and direct load.
     * Skips non-image files with warning.
     * 
     * @param number The number of the asset file
     * @return Loaded Image, or null if failed
     */
    public Image loadImageByNumber(int number) {
        String basePath = "/images/" + number;
        
        // Check cache first (with all possible format keys)
        Image cached = getImageByNumber(number);
        if (cached != null) {
            if (debugMode) {
                System.out.println("[AssetManager] Using cached image for number: " + number);
            }
            return cached;
        }
        
        // Strategy 1: Try ImageIO first (can handle many formats including custom ones)
        try {
            InputStream is = getClass().getResourceAsStream(basePath);
            if (is != null) {
                Image imageIO = loadImageWithImageIO(is, basePath);
                if (imageIO != null) {
                    if (debugMode) {
                        System.out.println("[AssetManager] Successfully loaded image #" + number + " with ImageIO");
                    }
                    return imageIO;
                }
                is.close();
            }
        } catch (IOException e) {
            if (debugMode) {
                System.err.println("[AssetManager] IOException with ImageIO for file: " + basePath + " - " + e.getMessage());
            }
        }
        
        // Strategy 2: Try loadImageFromStream (uses format detection + deep scan + JavaFX direct load)
        try {
            InputStream is = getClass().getResourceAsStream(basePath);
            if (is != null) {
                Image image = loadImageFromStream(is, basePath);
                if (image != null) {
                    if (debugMode) {
                        System.out.println("[AssetManager] Successfully loaded image #" + number + 
                            " with format detection/deep scan/JavaFX");
                    }
                    return image;
                }
            }
        } catch (Exception e) {
            if (debugMode) {
                System.err.println("[AssetManager] Exception loading file: " + basePath + " - " + e.getMessage());
            }
        }
        
        // Strategy 3: Fallback - Try common extensions if file not found or all strategies failed
        String[] extensions = {".png", ".jpg", ".jpeg", ".gif", ".bmp"};
        boolean triedExtensions = false;
        for (String ext : extensions) {
            Image image = loadImage(basePath + ext);
            if (image != null) {
                if (debugMode) {
                    System.out.println("[AssetManager] Successfully loaded image #" + number + " with extension: " + ext);
                }
                return image;
            }
            triedExtensions = true;
        }
        
        // Only log failure once per file
        if (!skippedFiles.contains(basePath)) {
            skippedFiles.add(basePath);
            if (debugMode) {
                String triedMethods = triedExtensions ? 
                    "ImageIO, format detection/JavaFX, deep scan/extract, and extensions: .png, .jpg, .jpeg, .gif, .bmp" :
                    "ImageIO, format detection/JavaFX, and deep scan/extract";
                System.out.println("[AssetManager] Could not load image by number: " + number + 
                    " (tried: " + triedMethods + ")");
            }
        }
        
        return null;
    }
    
    /**
     * Get a cached image
     * @param path Path to image resource
     * @return Cached Image, or null if not loaded
     */
    public Image getImage(String path) {
        return images.get(path);
    }
    
    /**
     * Get a cached image by number
     * @param number The number of the asset file
     * @return Cached Image, or null if not loaded
     */
    public Image getImageByNumber(int number) {
        // Check all possible paths (including format-detected cache keys)
        String basePath = "/images/" + number;
        String[] paths = {
            basePath,
            basePath + ".png",
            basePath + ".jpg",
            basePath + ".jpeg",
            basePath + ".gif",
            basePath + ".bmp"
        };
        
        // Check ImageIO cache key
        String imageIOCacheKey = basePath + "[ImageIO]";
        Image img = images.get(imageIOCacheKey);
        if (img != null) {
            return img;
        }
        
        // Check format-detected cache keys
        String[] formats = {"PNG", "JPEG", "GIF", "BMP", "Direct"};
        for (String format : formats) {
            String cacheKey = basePath + "[" + format + "]";
            img = images.get(cacheKey);
            if (img != null) {
                return img;
            }
        }
        
        // Check regular paths
        for (String path : paths) {
            img = images.get(path);
            if (img != null) {
                return img;
            }
        }
        
        return null;
    }
    
    /**
     * Preload commonly used images
     */
    public void preloadCommonAssets() {
        if (debugMode) {
            System.out.println("[AssetManager] Preloading common assets...");
        }
        
        int loadedCount = 0;
        int skippedCount = 0;
        
        // Load icon
        Image icon = loadImage("/images/icon.png");
        if (icon != null) {
            loadedCount++;
            if (debugMode) {
                System.out.println("[AssetManager] Preloaded icon.png");
            }
        } else if (debugMode) {
            System.out.println("[AssetManager] Warning: Could not load icon.png");
            skippedCount++;
        }
        
        // Load numbered image files if they exist
        for (int i = 0; i < 20; i++) {
            Image img = loadImageByNumber(i);
            if (img != null) {
                loadedCount++;
            } else {
                skippedCount++;
            }
        }
        
        if (debugMode) {
            System.out.println("[AssetManager] Preload summary: " + loadedCount + " loaded, " + 
                              skippedCount + " skipped, " + images.size() + " total cached");
        }
    }
    
    /**
     * Clear all cached assets
     */
    public void clearCache() {
        int size = images.size();
        images.clear();
        skippedFiles.clear();
        if (debugMode) {
            System.out.println("[AssetManager] Cleared " + size + " cached images and skipped files list");
        }
    }
    
    /**
     * Get the number of cached images
     * @return Number of cached images
     */
    public int getCacheSize() {
        return images.size();
    }
    
    /**
     * Enable or disable debug logging
     * @param enabled true to enable debug mode
     */
    public void setDebugMode(boolean enabled) {
        this.debugMode = enabled;
    }
}
