package com.greenfarm3.game.states;

import com.greenfarm3.assets.AssetManager;
import com.greenfarm3.game.GameState;
import com.greenfarm3.game.Sprite;
import com.greenfarm3.ui.Renderer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Main gameplay state.
 * This is where the actual farming game logic will be implemented.
 */
public class PlayState extends GameState {
    
    private Renderer renderer;
    private AssetManager assetManager;
    private int playerX = 120;
    private int playerY = 160;
    
    // Sprites
    private Sprite iconSprite;
    private Sprite backgroundSprite;
    private Sprite playerSprite;
    private Sprite[] testSprites;
    
    public PlayState(Renderer renderer) {
        this.renderer = renderer;
        this.assetManager = AssetManager.getInstance();
    }
    
    @Override
    public void initialize() {
        super.initialize();
        
        // Load sprites
        loadSprites();
    }
    
    private void loadSprites() {
        // Load icon sprite
        Image iconImage = assetManager.loadImage("/images/icon.png");
        if (iconImage != null) {
            iconSprite = new Sprite(iconImage, "icon");
            System.out.println("[PlayState] Loaded icon sprite: " + iconSprite);
        } else {
            System.out.println("[PlayState] Warning: Could not load icon.png");
        }
        
        // Try to load numbered sprites (test with first few)
        testSprites = new Sprite[10];
        int loadedCount = 0;
        for (int i = 0; i < 10; i++) {
            Image img = assetManager.loadImageByNumber(i);
            if (img != null) {
                testSprites[i] = new Sprite(img, "sprite_" + i);
                loadedCount++;
                System.out.println("[PlayState] Loaded sprite " + i + ": " + testSprites[i]);
            }
        }
        System.out.println("[PlayState] Loaded " + loadedCount + " numbered sprites");
        
        // Try to use first loaded sprite as player sprite
        for (Sprite sprite : testSprites) {
            if (sprite != null && sprite.isValid()) {
                playerSprite = sprite;
                System.out.println("[PlayState] Using sprite as player: " + playerSprite.getName());
                break;
            }
        }
        
        // If no numbered sprites, try to use icon as player
        if (playerSprite == null && iconSprite != null && iconSprite.isValid()) {
            playerSprite = iconSprite;
            System.out.println("[PlayState] Using icon as player sprite");
        }
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
        
        // Draw background/ground
        if (backgroundSprite != null && backgroundSprite.isValid()) {
            renderer.drawSprite(backgroundSprite, 0, 200);
        } else {
            // Fallback to colored rectangle
            renderer.fillRect(0, 200, 240, 120, Color.GREEN);
        }
        
        // Draw test sprites in a row (if loaded)
        if (testSprites != null) {
            int x = 10;
            int y = 220;
            for (Sprite sprite : testSprites) {
                if (sprite != null && sprite.isValid()) {
                    renderer.drawSprite(sprite, x, y);
                    x += sprite.getWidth() + 5;
                    if (x > 220) {
                        x = 10;
                        y += sprite.getHeight() + 5;
                    }
                }
            }
        }
        
        // Draw player sprite
        if (playerSprite != null && playerSprite.isValid()) {
            int spriteX = playerX - playerSprite.getWidth() / 2;
            int spriteY = playerY - playerSprite.getHeight() / 2;
            renderer.drawSprite(playerSprite, spriteX, spriteY);
        } else {
            // Fallback to placeholder rectangle
            renderer.fillRect(playerX - 10, playerY - 10, 20, 20, Color.BLUE);
        }
        
        // Draw icon sprite in corner (if loaded)
        if (iconSprite != null && iconSprite.isValid()) {
            renderer.drawSprite(iconSprite, 10, 10);
        }
        
        // Draw UI overlay
        Font uiFont = Font.font("Arial", 12);
        renderer.drawText("Green Farm 3", 10, 20, uiFont, Color.WHITE);
        
        // Draw debug info
        if (testSprites != null) {
            int spriteCount = 0;
            for (Sprite sprite : testSprites) {
                if (sprite != null && sprite.isValid()) spriteCount++;
            }
            renderer.drawText("Sprites loaded: " + spriteCount, 10, 35, uiFont, Color.WHITE);
        }
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
