package com.greenfarm3.assets;

import javafx.scene.image.Image;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages loading and caching of game assets (images, sounds).
 * Replaces J2ME resource loading.
 */
public class AssetManager {
    
    private static AssetManager instance;
    private final Map<String, Image> images;
    private boolean debugMode = true;
    
    private AssetManager() {
        this.images = new HashMap<>();
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
            
            Image image = new Image(is);
            if (image.isError()) {
                if (debugMode) {
                    System.err.println("[AssetManager] Failed to load image: " + path + " - Image error");
                }
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
     * Load an image by number (for numbered asset files like 0, 1, 2, etc.)
     * Tries multiple formats: as-is, .png, .jpg
     * @param number The number of the asset file
     * @return Loaded Image, or null if failed
     */
    public Image loadImageByNumber(int number) {
        String basePath = "/images/" + number;
        
        // Try as-is first (no extension)
        Image image = loadImage(basePath);
        if (image != null) {
            return image;
        }
        
        // Try PNG
        image = loadImage(basePath + ".png");
        if (image != null) {
            return image;
        }
        
        // Try JPG
        image = loadImage(basePath + ".jpg");
        if (image != null) {
            return image;
        }
        
        // Try JPEG
        image = loadImage(basePath + ".jpeg");
        if (image != null) {
            return image;
        }
        
        if (debugMode) {
            System.out.println("[AssetManager] Could not load image by number: " + number + 
                " (tried: " + basePath + ", .png, .jpg, .jpeg)");
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
        // Check all possible paths
        String[] paths = {
            "/images/" + number,
            "/images/" + number + ".png",
            "/images/" + number + ".jpg",
            "/images/" + number + ".jpeg"
        };
        
        for (String path : paths) {
            Image img = images.get(path);
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
        
        // Load icon
        Image icon = loadImage("/images/icon.png");
        if (icon == null && debugMode) {
            System.out.println("[AssetManager] Warning: Could not load icon.png");
        }
        
        // Load numbered image files if they exist
        int loadedCount = 0;
        for (int i = 0; i < 20; i++) {
            Image img = loadImageByNumber(i);
            if (img != null) {
                loadedCount++;
            }
        }
        
        if (debugMode) {
            System.out.println("[AssetManager] Preloaded " + loadedCount + " numbered images");
        }
    }
    
    /**
     * Clear all cached assets
     */
    public void clearCache() {
        int size = images.size();
        images.clear();
        if (debugMode) {
            System.out.println("[AssetManager] Cleared " + size + " cached images");
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
