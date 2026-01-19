package com.greenfarm3.game.states;

import com.greenfarm3.game.GameState;
import com.greenfarm3.ui.Renderer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Main gameplay state.
 * This is where the actual farming game logic will be implemented.
 */
public class PlayState extends GameState {
    
    private Renderer renderer;
    private int playerX = 120;
    private int playerY = 160;
    
    public PlayState(Renderer renderer) {
        this.renderer = renderer;
    }
    
    @Override
    public void initialize() {
        super.initialize();
        // Initialize gameplay
    }
    
    @Override
    public void update(double deltaTime) {
        // Update game logic
        // This will be expanded with actual game mechanics
    }
    
    @Override
    public void render(GraphicsContext gc) {
        // Clear with sky color
        renderer.clear(Color.SKYBLUE);
        
        // Draw ground
        renderer.fillRect(0, 200, 240, 120, Color.GREEN);
        
        // Draw player (placeholder)
        renderer.fillRect(playerX - 10, playerY - 10, 20, 20, Color.BLUE);
        
        // Draw UI overlay
        Font uiFont = Font.font("Arial", 12);
        renderer.drawText("Green Farm 3", 10, 20, uiFont, Color.WHITE);
    }
    
    @Override
    public void handleKeyPress(KeyCode keyCode) {
        int moveSpeed = 2;
        switch (keyCode) {
            case UP:
            case W:
                playerY = Math.max(10, playerY - moveSpeed);
                break;
            case DOWN:
            case S:
                playerY = Math.min(300, playerY + moveSpeed);
                break;
            case LEFT:
            case A:
                playerX = Math.max(10, playerX - moveSpeed);
                break;
            case RIGHT:
            case D:
                playerX = Math.min(230, playerX + moveSpeed);
                break;
            case ESCAPE:
                // TODO: Pause menu
                break;
        }
    }
    
    @Override
    public void handleClick(int x, int y) {
        // Handle click interactions
        // TODO: Implement farming interactions
    }
}
