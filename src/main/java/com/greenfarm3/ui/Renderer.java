package com.greenfarm3.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Utility class for rendering operations.
 * Provides helper methods for common drawing operations.
 */
public class Renderer {
    
    private final GraphicsContext gc;
    private final int scale;
    
    public Renderer(GraphicsContext gc, int scale) {
        this.gc = gc;
        this.scale = scale;
    }
    
    /**
     * Draw an image with scaling
     * @param image Image to draw
     * @param x X coordinate (in game coordinates)
     * @param y Y coordinate (in game coordinates)
     */
    public void drawImage(Image image, int x, int y) {
        if (image != null) {
            gc.drawImage(image, x * scale, y * scale, 
                        image.getWidth() * scale, image.getHeight() * scale);
        }
    }
    
    /**
     * Draw an image with scaling and size
     * @param image Image to draw
     * @param x X coordinate (in game coordinates)
     * @param y Y coordinate (in game coordinates)
     * @param width Width (in game coordinates)
     * @param height Height (in game coordinates)
     */
    public void drawImage(Image image, int x, int y, int width, int height) {
        if (image != null) {
            gc.drawImage(image, x * scale, y * scale, 
                        width * scale, height * scale);
        }
    }
    
    /**
     * Draw filled rectangle
     * @param x X coordinate
     * @param y Y coordinate
     * @param width Width
     * @param height Height
     * @param color Fill color
     */
    public void fillRect(int x, int y, int width, int height, Color color) {
        gc.setFill(color);
        gc.fillRect(x * scale, y * scale, width * scale, height * scale);
    }
    
    /**
     * Draw rectangle outline
     * @param x X coordinate
     * @param y Y coordinate
     * @param width Width
     * @param height Height
     * @param color Stroke color
     */
    public void strokeRect(int x, int y, int width, int height, Color color) {
        gc.setStroke(color);
        gc.strokeRect(x * scale, y * scale, width * scale, height * scale);
    }
    
    /**
     * Draw text
     * @param text Text to draw
     * @param x X coordinate
     * @param y Y coordinate
     * @param color Text color
     */
    public void drawText(String text, int x, int y, Color color) {
        gc.setFill(color);
        gc.fillText(text, x * scale, y * scale);
    }
    
    /**
     * Draw text with font
     * @param text Text to draw
     * @param x X coordinate
     * @param y Y coordinate
     * @param font Font to use
     * @param color Text color
     */
    public void drawText(String text, int x, int y, Font font, Color color) {
        gc.setFont(font);
        gc.setFill(color);
        gc.fillText(text, x * scale, y * scale);
    }
    
    /**
     * Clear the canvas
     * @param color Background color
     */
    public void clear(Color color) {
        gc.setFill(color);
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
    }
    
    public GraphicsContext getGraphicsContext() {
        return gc;
    }
    
    public int getScale() {
        return scale;
    }
}
