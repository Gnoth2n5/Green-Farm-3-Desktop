package com.greenfarm3.game;

import com.greenfarm3.ui.Renderer;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * Manages background rendering with support for tiling, scrolling, and offsets.
 * Can render from sprite/image or create procedural backgrounds.
 */
public class Background {
    
    private Sprite sprite;
    private Color solidColor;
    private int offsetX;
    private int offsetY;
    private boolean tiled;
    private float scrollSpeed;
    private int viewWidth;
    private int viewHeight;
    
    /**
     * Create a background from a sprite
     * @param sprite Background sprite
     */
    public Background(Sprite sprite) {
        this.sprite = sprite;
        this.solidColor = null;
        this.offsetX = 0;
        this.offsetY = 0;
        this.tiled = false;
        this.scrollSpeed = 1.0f;
        this.viewWidth = 240; // Default J2ME width
        this.viewHeight = 320; // Default J2ME height
    }
    
    /**
     * Create a solid color background
     * @param color Background color
     */
    public Background(Color color) {
        this.sprite = null;
        this.solidColor = color;
        this.offsetX = 0;
        this.offsetY = 0;
        this.tiled = false;
        this.scrollSpeed = 1.0f;
        this.viewWidth = 240;
        this.viewHeight = 320;
    }
    
    /**
     * Render the background
     * @param renderer Renderer to use
     */
    public void render(Renderer renderer) {
        if (solidColor != null) {
            // Render solid color background
            renderer.clear(solidColor);
            return;
        }
        
        if (sprite == null || !sprite.isValid()) {
            // Fallback to default color
            renderer.clear(Color.SKYBLUE);
            return;
        }
        
        Image image = sprite.getImage();
        int spriteWidth = sprite.getWidth();
        int spriteHeight = sprite.getHeight();
        
        if (tiled) {
            // Render tiled background
            renderTiled(renderer, image, spriteWidth, spriteHeight);
        } else {
            // Render single image with offset
            int x = offsetX % spriteWidth;
            int y = offsetY % spriteHeight;
            renderer.drawImage(image, x, y);
        }
    }
    
    /**
     * Render tiled background
     * @param renderer Renderer to use
     * @param image Image to tile
     * @param tileWidth Tile width
     * @param tileHeight Tile height
     */
    private void renderTiled(Renderer renderer, Image image, int tileWidth, int tileHeight) {
        if (tileWidth <= 0 || tileHeight <= 0) return;
        
        // Calculate starting position with offset
        int startX = (offsetX % tileWidth) - tileWidth;
        int startY = (offsetY % tileHeight) - tileHeight;
        
        // Calculate how many tiles we need
        int tilesX = (viewWidth / tileWidth) + 3; // +3 for offset and edges
        int tilesY = (viewHeight / tileHeight) + 3;
        
        // Render tiles
        for (int y = 0; y < tilesY; y++) {
            for (int x = 0; x < tilesX; x++) {
                int tileX = startX + (x * tileWidth);
                int tileY = startY + (y * tileHeight);
                renderer.drawImage(image, tileX, tileY);
            }
        }
    }
    
    /**
     * Set scroll offset
     * @param x X offset
     * @param y Y offset
     */
    public void setOffset(int x, int y) {
        this.offsetX = x;
        this.offsetY = y;
    }
    
    /**
     * Set X offset
     * @param x X offset
     */
    public void setOffsetX(int x) {
        this.offsetX = x;
    }
    
    /**
     * Set Y offset
     * @param y Y offset
     */
    public void setOffsetY(int y) {
        this.offsetY = y;
    }
    
    /**
     * Get X offset
     * @return X offset
     */
    public int getOffsetX() {
        return offsetX;
    }
    
    /**
     * Get Y offset
     * @return Y offset
     */
    public int getOffsetY() {
        return offsetY;
    }
    
    /**
     * Enable or disable tiled mode
     * @param enabled true to enable tiling
     */
    public void setTileMode(boolean enabled) {
        this.tiled = enabled;
    }
    
    /**
     * Check if tiled mode is enabled
     * @return true if tiled
     */
    public boolean isTiled() {
        return tiled;
    }
    
    /**
     * Set scroll speed (for parallax)
     * @param speed Scroll speed multiplier
     */
    public void setScrollSpeed(float speed) {
        this.scrollSpeed = speed;
    }
    
    /**
     * Get scroll speed
     * @return Scroll speed multiplier
     */
    public float getScrollSpeed() {
        return scrollSpeed;
    }
    
    /**
     * Set viewport dimensions
     * @param width View width
     * @param height View height
     */
    public void setViewSize(int width, int height) {
        this.viewWidth = width;
        this.viewHeight = height;
    }
    
    /**
     * Update offset based on scroll speed
     * @param deltaX Base delta X
     * @param deltaY Base delta Y
     */
    public void updateScroll(int deltaX, int deltaY) {
        this.offsetX += (int)(deltaX * scrollSpeed);
        this.offsetY += (int)(deltaY * scrollSpeed);
    }
    
    /**
     * Get the sprite
     * @return Background sprite
     */
    public Sprite getSprite() {
        return sprite;
    }
    
    /**
     * Check if background is valid
     * @return true if has sprite or solid color
     */
    public boolean isValid() {
        return solidColor != null || (sprite != null && sprite.isValid());
    }
}
