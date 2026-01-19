package com.greenfarm3.game.ui;

import com.greenfarm3.ui.Renderer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.function.Consumer;

/**
 * HUD (Heads-Up Display) overlay component.
 * Displays game information and buttons for quick access to UI screens.
 */
public class HUDOverlay {
    
    private Renderer renderer;
    private Consumer<String> buttonCallback;
    
    // Button definitions
    private static class Button {
        String label;
        int x, y, width, height;
        
        Button(String label, int x, int y, int width, int height) {
            this.label = label;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
        
        boolean contains(int px, int py) {
            return px >= x && px < x + width && py >= y && py < y + height;
        }
    }
    
    private Button[] buttons;
    
    // Game info (placeholders)
    private int money = 0;
    private int level = 1;
    
    // HUD dimensions
    private static final int BUTTON_WIDTH = 60;
    private static final int BUTTON_HEIGHT = 25;
    private static final int BUTTON_SPACING = 5;
    private static final int HUD_PADDING = 5;
    
    public HUDOverlay(Renderer renderer) {
        this.renderer = renderer;
        initializeButtons();
    }
    
    private void initializeButtons() {
        // Position buttons in top-right corner
        int startX = 240 - HUD_PADDING - BUTTON_WIDTH;
        int startY = HUD_PADDING;
        
        buttons = new Button[] {
            new Button("Inv", startX, startY, BUTTON_WIDTH, BUTTON_HEIGHT),
            new Button("Shop", startX, startY + (BUTTON_HEIGHT + BUTTON_SPACING), BUTTON_WIDTH, BUTTON_HEIGHT),
            new Button("Set", startX, startY + 2 * (BUTTON_HEIGHT + BUTTON_SPACING), BUTTON_WIDTH, BUTTON_HEIGHT)
        };
    }
    
    public void setButtonCallback(Consumer<String> callback) {
        this.buttonCallback = callback;
    }
    
    /**
     * Set game money (for display)
     */
    public void setMoney(int money) {
        this.money = money;
    }
    
    /**
     * Set game level (for display)
     */
    public void setLevel(int level) {
        this.level = level;
    }
    
    /**
     * Render HUD overlay
     */
    public void render() {
        // Draw semi-transparent background for info area (top-left)
        Color infoBgColor = new Color(0, 0, 0, 0.6);
        renderer.fillRect(HUD_PADDING, HUD_PADDING, 100, 50, infoBgColor);
        
        // Draw game info
        Font infoFont = Font.font("Arial", 10);
        renderer.drawText("Money: $" + money, HUD_PADDING + 5, HUD_PADDING + 15, infoFont, Color.WHITE);
        renderer.drawText("Level: " + level, HUD_PADDING + 5, HUD_PADDING + 30, infoFont, Color.WHITE);
        
        // Draw buttons (top-right)
        Font buttonFont = Font.font("Arial", 11);
        Color buttonBgColor = new Color(0.3, 0.3, 0.3, 0.8);
        Color buttonTextColor = Color.WHITE;
        
        for (Button button : buttons) {
            // Draw button background
            renderer.fillRect(button.x, button.y, button.width, button.height, buttonBgColor);
            renderer.strokeRect(button.x, button.y, button.width, button.height, Color.WHITE);
            
            // Draw button text (centered)
            int textX = button.x + (button.width / 2) - (button.label.length() * 3); // Approximate centering
            int textY = button.y + (button.height / 2) + 4; // Approximate vertical center
            renderer.drawText(button.label, textX, textY, buttonFont, buttonTextColor);
        }
    }
    
    /**
     * Handle click and check if any button was clicked
     * @param x Click X coordinate
     * @param y Click Y coordinate
     * @return true if a button was clicked
     */
    public boolean handleClick(int x, int y) {
        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i].contains(x, y)) {
                // Button clicked
                String action = getButtonAction(i);
                if (buttonCallback != null && action != null) {
                    buttonCallback.accept(action);
                }
                return true;
            }
        }
        return false;
    }
    
    /**
     * Get action string for button index
     */
    private String getButtonAction(int buttonIndex) {
        switch (buttonIndex) {
            case 0: return "inventory"; // Inv button
            case 1: return "shop";      // Shop button
            case 2: return "settings"; // Set button
            default: return null;
        }
    }
    
    /**
     * Check if a point is within any button
     */
    public boolean isButtonClicked(int x, int y) {
        for (Button button : buttons) {
            if (button.contains(x, y)) {
                return true;
            }
        }
        return false;
    }
}
