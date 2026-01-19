package com.greenfarm3.game.states;

import com.greenfarm3.game.GameState;
import com.greenfarm3.ui.Renderer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.function.Consumer;

/**
 * Pause menu overlay state.
 * Displays a menu overlay on top of gameplay when game is paused.
 */
public class PauseState extends GameState {
    
    private Renderer renderer;
    private Consumer<String> actionCallback;
    private int selectedOption = 0;
    private final String[] menuOptions = {
        "Inventory",
        "Shop",
        "Settings",
        "Resume",
        "Quit to Menu"
    };
    
    // Menu dimensions
    private static final int MENU_WIDTH = 200;
    private static final int MENU_HEIGHT = 300;
    private static final int MENU_X = 20;  // Center: (240 - 200) / 2
    private static final int MENU_Y = 10;  // Center: (320 - 300) / 2
    
    public PauseState(Renderer renderer) {
        this.renderer = renderer;
    }
    
    public void setActionCallback(Consumer<String> callback) {
        this.actionCallback = callback;
    }
    
    @Override
    public void initialize() {
        super.initialize();
        selectedOption = 0; // Reset selection when initialized
    }
    
    @Override
    public void update(double deltaTime) {
        // Pause menu doesn't need continuous updates
    }
    
    @Override
    public void render(GraphicsContext gc) {
        // Draw semi-transparent background overlay
        Color overlayColor = new Color(0, 0, 0, 0.8);
        renderer.fillRect(0, 0, 240, 320, overlayColor);
        
        // Draw menu background
        renderer.fillRect(MENU_X, MENU_Y, MENU_WIDTH, MENU_HEIGHT, Color.DARKGRAY);
        renderer.strokeRect(MENU_X, MENU_Y, MENU_WIDTH, MENU_HEIGHT, Color.WHITE);
        
        // Draw title
        Font titleFont = Font.font("Arial", 20);
        renderer.drawText("PAUSED", MENU_X + 60, MENU_Y + 30, titleFont, Color.WHITE);
        
        // Draw menu options
        Font menuFont = Font.font("Arial", 16);
        int startY = MENU_Y + 60;
        int spacing = 40;
        
        for (int i = 0; i < menuOptions.length; i++) {
            Color textColor = (i == selectedOption) ? Color.YELLOW : Color.WHITE;
            
            // Draw selection indicator
            if (i == selectedOption) {
                renderer.drawText(">", MENU_X + 20, startY + i * spacing, menuFont, textColor);
            }
            
            // Draw option text
            renderer.drawText(menuOptions[i], MENU_X + 40, startY + i * spacing, menuFont, textColor);
        }
        
        // Draw instructions
        Font hintFont = Font.font("Arial", 10);
        renderer.drawText("Arrow Keys: Navigate | Enter: Select | ESC: Resume", 
                         MENU_X + 10, MENU_Y + MENU_HEIGHT - 20, hintFont, Color.LIGHTGRAY);
    }
    
    @Override
    public void handleKeyPress(KeyCode keyCode) {
        switch (keyCode) {
            case UP:
            case W:
                selectedOption = Math.max(0, selectedOption - 1);
                break;
            case DOWN:
            case S:
                selectedOption = Math.min(menuOptions.length - 1, selectedOption + 1);
                break;
            case ENTER:
            case SPACE:
                selectOption();
                break;
            case ESCAPE:
                // Resume game
                if (actionCallback != null) {
                    actionCallback.accept("resume");
                }
                break;
        }
    }
    
    @Override
    public void handleClick(int x, int y) {
        // Check if click is within menu bounds
        if (x >= MENU_X && x <= MENU_X + MENU_WIDTH &&
            y >= MENU_Y + 60 && y <= MENU_Y + MENU_HEIGHT - 40) {
            
            int startY = MENU_Y + 60;
            int spacing = 40;
            int clickedOption = (y - startY) / spacing;
            
            if (clickedOption >= 0 && clickedOption < menuOptions.length) {
                selectedOption = clickedOption;
                selectOption();
            }
        }
    }
    
    private void selectOption() {
        if (actionCallback == null) return;
        
        switch (selectedOption) {
            case 0: // Inventory
                actionCallback.accept("inventory");
                break;
            case 1: // Shop
                actionCallback.accept("shop");
                break;
            case 2: // Settings
                actionCallback.accept("settings");
                break;
            case 3: // Resume
                actionCallback.accept("resume");
                break;
            case 4: // Quit to Menu
                actionCallback.accept("quit_to_menu");
                break;
        }
    }
}
