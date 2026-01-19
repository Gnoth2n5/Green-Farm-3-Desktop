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
            return images.get(path);
        }
        
        try {
            InputStream is = getClass().getResourceAsStream(path);
            if (is == null) {
                System.err.println("Image not found: " + path);
                return null;
            }
            
            Image image = new Image(is);
            images.put(path, image);
            return image;
        } catch (Exception e) {
            System.err.println("Failed to load image: " + path + " - " + e.getMessage());
            return null;
        }
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
     * Preload commonly used images
     */
    public void preloadCommonAssets() {
        // Load icon
        loadImage("/images/icon.png");
        
        // Load numbered image files if they exist
        for (int i = 0; i < 20; i++) {
            loadImage("/images/" + i);
        }
    }
    
    /**
     * Clear all cached assets
     */
    public void clearCache() {
        images.clear();
    }
}
