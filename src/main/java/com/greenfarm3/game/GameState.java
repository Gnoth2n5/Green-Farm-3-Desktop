package com.greenfarm3.game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;

/**
 * Base class for game states (menu, gameplay, pause, etc.)
 * Implements state pattern for game flow management.
 */
public abstract class GameState {
    
    protected boolean initialized = false;
    
    /**
     * Initialize the game state
     */
    public void initialize() {
        initialized = true;
    }
    
    /**
     * Update game state
     * @param deltaTime Time since last frame in milliseconds
     */
    public abstract void update(double deltaTime);
    
    /**
     * Render the game state
     * @param gc GraphicsContext for drawing
     */
    public abstract void render(GraphicsContext gc);
    
    /**
     * Handle mouse/touch click
     * @param x X coordinate
     * @param y Y coordinate
     */
    public void handleClick(int x, int y) {
        // Override in subclasses
    }
    
    /**
     * Handle key press
     * @param keyCode Key code
     */
    public void handleKeyPress(KeyCode keyCode) {
        // Override in subclasses
    }
    
    /**
     * Handle key release
     * @param keyCode Key code
     */
    public void handleKeyRelease(KeyCode keyCode) {
        // Override in subclasses
    }
    
    /**
     * Cleanup when leaving this state
     */
    public void cleanup() {
        initialized = false;
    }
    
    public boolean isInitialized() {
        return initialized;
    }
}
