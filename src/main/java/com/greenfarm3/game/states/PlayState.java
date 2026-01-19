package com.greenfarm3.game.states;

import com.greenfarm3.assets.AssetManager;
import com.greenfarm3.game.Camera;
import com.greenfarm3.game.GameState;
import com.greenfarm3.game.Sprite;
import com.greenfarm3.game.TileMap;
import com.greenfarm3.game.TileRenderer;
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
    
    // Player position in world coordinates
    private int playerX = 240;  // Start at center of a 15x20 tile map (15*32/2 = 240)
    private int playerY = 320;  // Start at center (20*32/2 = 320)
    private int playerSpeed = 2;
    
    // Sprites
    private Sprite iconSprite;
    private Sprite playerSprite;
    private Sprite[] testSprites;
    
    // Tile map system
    private TileMap tileMap;
    private Camera camera;
    private TileRenderer tileRenderer;
    
    // Viewport size (matches GameWindow base dimensions)
    private static final int VIEW_WIDTH = 240;
    private static final int VIEW_HEIGHT = 320;
    
    public PlayState(Renderer renderer) {
        this.renderer = renderer;
        this.assetManager = AssetManager.getInstance();
    }
    
    @Override
    public void initialize() {
        super.initialize();
        
        // Initialize tile map system
        initializeTileMap();
        
        // Initialize camera
        initializeCamera();
        
        // Initialize tile renderer
        tileRenderer = new TileRenderer();
        
        // Load sprites
        loadSprites();
    }
    
    private void initializeTileMap() {
        // Create a tile map (15x20 tiles, 32x32 pixels per tile)
        // This gives us 480x640 pixel map
        tileMap = new TileMap(15, 20, 32, 32);
        
        // Generate default map
        tileMap.generateDefaultMap();
        
        System.out.println("[PlayState] Tile map initialized: " + 
                          tileMap.getWidth() + "x" + tileMap.getHeight() + 
                          " tiles (" + tileMap.getPixelWidth() + "x" + tileMap.getPixelHeight() + " pixels)");
    }
    
    private void initializeCamera() {
        // Create camera with viewport size
        camera = new Camera(VIEW_WIDTH, VIEW_HEIGHT);
        
        // Set map boundaries
        camera.setMapBounds(tileMap.getPixelWidth(), tileMap.getPixelHeight());
        
        // Center camera on player initially
        camera.setCenter(playerX, playerY);
        
        System.out.println("[PlayState] Camera initialized: " + 
                          VIEW_WIDTH + "x" + VIEW_HEIGHT + " viewport");
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
        // Update camera to follow player
        if (camera != null) {
            camera.follow(playerX, playerY);
            camera.update();
        }
    }
    
    @Override
    public void render(GraphicsContext gc) {
        // Clear screen
        renderer.clear(Color.SKYBLUE);
        
        // Render tile map
        if (tileMap != null && tileRenderer != null && camera != null) {
            tileRenderer.render(renderer, tileMap, camera);
        }
        
        // Draw player sprite (convert world coords to screen coords)
        if (playerSprite != null && playerSprite.isValid() && camera != null) {
            int[] screenCoords = camera.worldToScreen(playerX, playerY);
            int screenX = screenCoords[0] - playerSprite.getWidth() / 2;
            int screenY = screenCoords[1] - playerSprite.getHeight() / 2;
            renderer.drawSprite(playerSprite, screenX, screenY);
        } else if (camera != null) {
            // Fallback to placeholder rectangle
            int[] screenCoords = camera.worldToScreen(playerX, playerY);
            int screenX = screenCoords[0] - 10;
            int screenY = screenCoords[1] - 10;
            renderer.fillRect(screenX, screenY, 20, 20, Color.BLUE);
        }
        
        // Draw test sprites in a row (if loaded) - fixed screen position
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
        
        // Draw icon sprite in corner (if loaded) - fixed screen position
        if (iconSprite != null && iconSprite.isValid()) {
            renderer.drawSprite(iconSprite, 10, 10);
        }
        
        // Draw UI overlay
        Font uiFont = Font.font("Arial", 12);
        renderer.drawText("Green Farm 3", 10, 20, uiFont, Color.WHITE);
        
        // Draw debug info
        if (tileMap != null && camera != null) {
            int[] playerTile = tileMap.pixelToTile(playerX, playerY);
            String tileInfo = playerTile != null ? 
                "Tile: " + playerTile[0] + "," + playerTile[1] : "Tile: out of bounds";
            renderer.drawText("Player: " + playerX + "," + playerY, 10, 35, uiFont, Color.WHITE);
            renderer.drawText(tileInfo, 10, 50, uiFont, Color.WHITE);
            renderer.drawText("Camera: " + camera.getViewX() + "," + camera.getViewY(), 10, 65, uiFont, Color.WHITE);
        }
        
        if (tileRenderer != null) {
            renderer.drawText("Tiles loaded: " + tileRenderer.getLoadedSpriteCount(), 10, 80, uiFont, Color.WHITE);
        }
    }
    
    @Override
    public void handleKeyPress(KeyCode keyCode) {
        int newX = playerX;
        int newY = playerY;
        
        switch (keyCode) {
            case UP:
            case W:
                newY = playerY - playerSpeed;
                break;
            case DOWN:
            case S:
                newY = playerY + playerSpeed;
                break;
            case LEFT:
            case A:
                newX = playerX - playerSpeed;
                break;
            case RIGHT:
            case D:
                newX = playerX + playerSpeed;
                break;
            case ESCAPE:
                // TODO: Pause menu
                break;
        }
        
        // Clamp player to map boundaries
        if (tileMap != null) {
            newX = Math.max(0, Math.min(newX, tileMap.getPixelWidth() - 1));
            newY = Math.max(0, Math.min(newY, tileMap.getPixelHeight() - 1));
        } else {
            // Fallback boundaries
            newX = Math.max(10, Math.min(newX, VIEW_WIDTH - 10));
            newY = Math.max(10, Math.min(newY, VIEW_HEIGHT - 10));
        }
        
        // Check if tile is walkable (if we have a map)
        if (tileMap != null) {
            int[] tile = tileMap.pixelToTile(newX, newY);
            if (tile != null) {
                int tileId = tileMap.getTile(tile[0], tile[1]);
                if (TileMap.isWalkable(tileId)) {
                    playerX = newX;
                    playerY = newY;
                }
            } else {
                // Out of bounds, don't move
            }
        } else {
            playerX = newX;
            playerY = newY;
        }
    }
    
    @Override
    public void handleClick(int x, int y) {
        // Handle click interactions
        // TODO: Implement farming interactions
    }
}
