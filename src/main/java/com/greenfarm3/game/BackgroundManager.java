package com.greenfarm3.game;

import com.greenfarm3.ui.Renderer;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages multiple background layers with parallax scrolling support.
 * Renders layers from farthest to nearest for proper depth effect.
 */
public class BackgroundManager {
    
    /**
     * Inner class to hold background layer with parallax speed
     */
    private static class BackgroundLayer {
        Background background;
        float parallaxSpeed;
        
        BackgroundLayer(Background background, float parallaxSpeed) {
            this.background = background;
            this.parallaxSpeed = parallaxSpeed;
        }
    }
    
    private final List<BackgroundLayer> layers;
    private int scrollX;
    private int scrollY;
    private int viewWidth;
    private int viewHeight;
    
    public BackgroundManager() {
        this.layers = new ArrayList<>();
        this.scrollX = 0;
        this.scrollY = 0;
        this.viewWidth = 240;
        this.viewHeight = 320;
    }
    
    /**
     * Add a background layer
     * @param background Background to add
     * @param parallaxSpeed Parallax speed (1.0 = normal, <1.0 = slower, >1.0 = faster)
     */
    public void addLayer(Background background, float parallaxSpeed) {
        if (background != null) {
            background.setViewSize(viewWidth, viewHeight);
            layers.add(new BackgroundLayer(background, parallaxSpeed));
        }
    }
    
    /**
     * Add a background layer with normal speed (1.0)
     * @param background Background to add
     */
    public void addLayer(Background background) {
        addLayer(background, 1.0f);
    }
    
    /**
     * Remove all layers
     */
    public void clearLayers() {
        layers.clear();
    }
    
    /**
     * Update background scrolling
     * @param deltaTime Time since last frame in milliseconds
     */
    public void update(double deltaTime) {
        // Update each layer's scroll based on parallax speed
        for (BackgroundLayer layer : layers) {
            int deltaX = (int)(scrollX * layer.parallaxSpeed);
            int deltaY = (int)(scrollY * layer.parallaxSpeed);
            layer.background.setOffset(deltaX, deltaY);
        }
    }
    
    /**
     * Set scroll offset (base position)
     * @param x X scroll position
     * @param y Y scroll position
     */
    public void setScrollOffset(int x, int y) {
        this.scrollX = x;
        this.scrollY = y;
        update(0); // Update layers immediately
    }
    
    /**
     * Get scroll X
     * @return X scroll position
     */
    public int getScrollX() {
        return scrollX;
    }
    
    /**
     * Get scroll Y
     * @return Y scroll position
     */
    public int getScrollY() {
        return scrollY;
    }
    
    /**
     * Update scroll offset relative to current position
     * @param deltaX Change in X
     * @param deltaY Change in Y
     */
    public void updateScroll(int deltaX, int deltaY) {
        this.scrollX += deltaX;
        this.scrollY += deltaY;
        update(0);
    }
    
    /**
     * Render all background layers
     * @param renderer Renderer to use
     */
    public void render(Renderer renderer) {
        // Render from farthest to nearest (back to front)
        for (BackgroundLayer layer : layers) {
            layer.background.render(renderer);
        }
    }
    
    /**
     * Set viewport size for all layers
     * @param width View width
     * @param height View height
     */
    public void setViewSize(int width, int height) {
        this.viewWidth = width;
        this.viewHeight = height;
        
        // Update all layers
        for (BackgroundLayer layer : layers) {
            layer.background.setViewSize(width, height);
        }
    }
    
    /**
     * Get number of layers
     * @return Number of background layers
     */
    public int getLayerCount() {
        return layers.size();
    }
    
    /**
     * Create a default background setup (sky + ground)
     * @return BackgroundManager with default layers
     */
    public static BackgroundManager createDefault() {
        BackgroundManager manager = new BackgroundManager();
        
        // Sky layer (static, no parallax)
        Background sky = new Background(Color.SKYBLUE);
        manager.addLayer(sky, 0.0f);
        
        // Ground layer (normal speed)
        Background ground = new Background(Color.GREEN);
        manager.addLayer(ground, 1.0f);
        
        return manager;
    }
    
    /**
     * Create a procedural background with gradient sky
     * @return BackgroundManager with procedural layers
     */
    public static BackgroundManager createProcedural() {
        BackgroundManager manager = new BackgroundManager();
        
        // Sky gradient (light blue to darker blue)
        Background sky = new Background(Color.LIGHTBLUE);
        manager.addLayer(sky, 0.1f); // Very slow parallax
        
        // Ground (green)
        Background ground = new Background(Color.GREEN);
        ground.setTileMode(true); // Enable tiling for ground
        manager.addLayer(ground, 1.0f);
        
        return manager;
    }
}
