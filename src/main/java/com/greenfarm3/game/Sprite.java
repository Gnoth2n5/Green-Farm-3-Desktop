package com.greenfarm3.game;

import javafx.scene.image.Image;

/**
 * Wrapper class for game sprites.
 * Manages image, position, size, and provides helper methods for rendering.
 */
public class Sprite {
    
    private Image image;
    private String name;
    private int width;
    private int height;
    private int x;
    private int y;
    
    /**
     * Create a sprite from an image
     * @param image The JavaFX Image
     * @param name Name/identifier for this sprite
     */
    public Sprite(Image image, String name) {
        this.image = image;
        this.name = name;
        if (image != null) {
            this.width = (int) image.getWidth();
            this.height = (int) image.getHeight();
        } else {
            this.width = 0;
            this.height = 0;
        }
        this.x = 0;
        this.y = 0;
    }
    
    /**
     * Create a sprite from an image with position
     * @param image The JavaFX Image
     * @param name Name/identifier for this sprite
     * @param x Initial X position
     * @param y Initial Y position
     */
    public Sprite(Image image, String name, int x, int y) {
        this(image, name);
        this.x = x;
        this.y = y;
    }
    
    /**
     * Get the underlying image
     * @return The JavaFX Image
     */
    public Image getImage() {
        return image;
    }
    
    /**
     * Get sprite name
     * @return Name of the sprite
     */
    public String getName() {
        return name;
    }
    
    /**
     * Get sprite width
     * @return Width in pixels
     */
    public int getWidth() {
        return width;
    }
    
    /**
     * Get sprite height
     * @return Height in pixels
     */
    public int getHeight() {
        return height;
    }
    
    /**
     * Get X position
     * @return X coordinate
     */
    public int getX() {
        return x;
    }
    
    /**
     * Get Y position
     * @return Y coordinate
     */
    public int getY() {
        return y;
    }
    
    /**
     * Set position
     * @param x X coordinate
     * @param y Y coordinate
     */
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Set X position
     * @param x X coordinate
     */
    public void setX(int x) {
        this.x = x;
    }
    
    /**
     * Set Y position
     * @param y Y coordinate
     */
    public void setY(int y) {
        this.y = y;
    }
    
    /**
     * Check if sprite has a valid image
     * @return true if image is not null and not in error state
     */
    public boolean isValid() {
        return image != null && !image.isError() && width > 0 && height > 0;
    }
    
    /**
     * Get center X position
     * @return Center X coordinate
     */
    public int getCenterX() {
        return x + width / 2;
    }
    
    /**
     * Get center Y position
     * @return Center Y coordinate
     */
    public int getCenterY() {
        return y + height / 2;
    }
    
    /**
     * Check if a point is within sprite bounds
     * @param px Point X coordinate
     * @param py Point Y coordinate
     * @return true if point is within sprite
     */
    public boolean contains(int px, int py) {
        return px >= x && px < x + width && py >= y && py < y + height;
    }
    
    /**
     * Check if sprite intersects with another sprite
     * @param other Other sprite
     * @return true if sprites intersect
     */
    public boolean intersects(Sprite other) {
        if (other == null) return false;
        return x < other.x + other.width &&
               x + width > other.x &&
               y < other.y + other.height &&
               y + height > other.y;
    }
    
    @Override
    public String toString() {
        return "Sprite{" +
                "name='" + name + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", x=" + x +
                ", y=" + y +
                ", valid=" + isValid() +
                '}';
    }
}
