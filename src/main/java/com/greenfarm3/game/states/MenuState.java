package com.greenfarm3.game.states;

import com.greenfarm3.game.GameState;
import com.greenfarm3.ui.Renderer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import java.util.function.Consumer;

/**
 * Main menu game state.
 */
public class MenuState extends GameState {
    
    private Renderer renderer;
    private Consumer<String> stateChangeCallback;
    private int selectedOption = 0;
    private final String[] menuOptions = {
        "New Game",
        "Load Game",
        "Settings",
        "Exit"
    };
    
    public MenuState(Renderer renderer) {
        this.renderer = renderer;
    }
    
    public void setStateChangeCallback(Consumer<String> callback) {
        this.stateChangeCallback = callback;
    }
    
    @Override
    public void initialize() {
        super.initialize();
    }
    
    @Override
    public void update(double deltaTime) {
        // Menu doesn't need continuous updates
    }
    
    @Override
    public void render(GraphicsContext gc) {
        // Clear with background color
        renderer.clear(Color.LIGHTGREEN);
        
        // Draw title
        Font titleFont = Font.font("Arial", 24);
        renderer.drawText("Green Farm 3", 70, 50, titleFont, Color.DARKGREEN);
        
        // Draw menu options
        Font menuFont = Font.font("Arial", 16);
        int startY = 150;
        int spacing = 40;
        
        for (int i = 0; i < menuOptions.length; i++) {
            Color color = (i == selectedOption) ? Color.DARKBLUE : Color.BLACK;
            renderer.drawText(menuOptions[i], 80, startY + i * spacing, menuFont, color);
            
            // Draw selection indicator
            if (i == selectedOption) {
                renderer.drawText(">", 60, startY + i * spacing, menuFont, color);
            }
        }
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
        }
    }
    
    @Override
    public void handleClick(int x, int y) {
        // Convert click to menu option selection
        int startY = 150;
        int spacing = 40;
        int clickedOption = (y - startY) / spacing;
        
        if (clickedOption >= 0 && clickedOption < menuOptions.length) {
            selectedOption = clickedOption;
            selectOption();
        }
    }
    
    private void selectOption() {
        if (stateChangeCallback == null) return;
        
        switch (selectedOption) {
            case 0: // New Game
                stateChangeCallback.accept("new_game");
                break;
            case 1: // Load Game
                stateChangeCallback.accept("load_game");
                break;
            case 2: // Settings
                stateChangeCallback.accept("settings");
                break;
            case 3: // Exit
                System.exit(0);
                break;
        }
    }
}
