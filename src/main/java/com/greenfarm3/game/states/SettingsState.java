package com.greenfarm3.game.states;

import com.greenfarm3.game.GameState;
import com.greenfarm3.ui.Renderer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.function.Consumer;

/**
 * Settings screen state.
 * Displays game settings (graphics, controls, audio) - skeleton implementation.
 */
public class SettingsState extends GameState {
    
    private Renderer renderer;
    private Consumer<String> actionCallback;
    
    public SettingsState(Renderer renderer) {
        this.renderer = renderer;
    }
    
    public void setActionCallback(Consumer<String> callback) {
        this.actionCallback = callback;
    }
    
    @Override
    public void initialize() {
        super.initialize();
    }
    
    @Override
    public void update(double deltaTime) {
        // Settings screen doesn't need continuous updates
    }
    
    @Override
    public void render(GraphicsContext gc) {
        // Clear with background color
        renderer.clear(Color.DARKGRAY);
        
        // Draw title
        Font titleFont = Font.font("Arial", 24);
        renderer.drawText("SETTINGS", 60, 50, titleFont, Color.WHITE);
        
        // Draw placeholder content
        Font contentFont = Font.font("Arial", 14);
        renderer.drawText("Settings options will be displayed here.", 20, 100, contentFont, Color.LIGHTGRAY);
        renderer.drawText("(Placeholder - To be implemented)", 20, 120, contentFont, Color.LIGHTGRAY);
        renderer.drawText("- Graphics settings", 20, 150, contentFont, Color.LIGHTGRAY);
        renderer.drawText("- Controls settings", 20, 170, contentFont, Color.LIGHTGRAY);
        renderer.drawText("- Audio settings", 20, 190, contentFont, Color.LIGHTGRAY);
        
        // Draw back button
        Font buttonFont = Font.font("Arial", 16);
        renderer.fillRect(20, 280, 80, 30, Color.DARKBLUE);
        renderer.strokeRect(20, 280, 80, 30, Color.WHITE);
        renderer.drawText("Back", 35, 300, buttonFont, Color.WHITE);
        
        // Draw instructions
        Font hintFont = Font.font("Arial", 10);
        renderer.drawText("Press ESC or click Back to return", 20, 250, hintFont, Color.LIGHTGRAY);
    }
    
    @Override
    public void handleKeyPress(KeyCode keyCode) {
        if (keyCode == KeyCode.ESCAPE || keyCode == KeyCode.B) {
            // Return to gameplay
            if (actionCallback != null) {
                actionCallback.accept("back");
            }
        }
    }
    
    @Override
    public void handleClick(int x, int y) {
        // Check if back button was clicked
        if (x >= 20 && x <= 100 && y >= 280 && y <= 310) {
            if (actionCallback != null) {
                actionCallback.accept("back");
            }
        }
    }
}
