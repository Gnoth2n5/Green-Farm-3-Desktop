package com.greenfarm3.game;

import com.greenfarm3.assets.AssetManager;
import com.greenfarm3.ui.Renderer;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;

/**
 * Renders tiles from a TileMap.
 * Handles culling (only renders visible tiles) and tile sprite loading.
 */
public class TileRenderer {
    
    private AssetManager assetManager;
    private Map<Integer, Sprite> tileSprites;  // Map tileId -> Sprite
    private Map<Integer, Color> tileColors;    // Map tileId -> Color (fallback)
    
    /**
     * Create a new tile renderer
     */
    public TileRenderer() {
        this.assetManager = AssetManager.getInstance();
        this.tileSprites = new HashMap<>();
        this.tileColors = new HashMap<>();
        
        // Initialize default tile colors (fallback if sprites not found)
        initializeTileColors();
        
        // Try to load tile sprites
        loadTileSprites();
    }
    
    /**
     * Initialize default colors for tiles (fallback rendering)
     */
    private void initializeTileColors() {
        tileColors.put(TileMap.TILE_GRASS, Color.GREEN);
        tileColors.put(TileMap.TILE_DIRT, Color.SADDLEBROWN);
        tileColors.put(TileMap.TILE_WATER, Color.BLUE);
        tileColors.put(TileMap.TILE_PATH, Color.GRAY);
        tileColors.put(TileMap.TILE_FARMLAND, Color.DARKGREEN);
        tileColors.put(TileMap.TILE_STONE, Color.DARKGRAY);
    }
    
    /**
     * Load tile sprites from assets
     * Tries to load numbered sprites that might be tiles
     */
    public void loadTileSprites() {
        // Try to load sprites for common tile types
        // We'll try numbered assets that might be tiles
        int[] tileNumbers = {0, 1, 2, 3, 4, 5, 10, 20, 30, 50};
        
        for (int num : tileNumbers) {
            Image img = assetManager.loadImageByNumber(num);
            if (img != null) {
                // Try to map to tile types based on number or order
                // This is a heuristic - actual mapping might need adjustment
                int tileId = mapNumberToTileId(num);
                if (tileId >= 0) {
                    Sprite sprite = new Sprite(img, "tile_" + tileId);
                    tileSprites.put(tileId, sprite);
                    System.out.println("[TileRenderer] Loaded tile sprite " + tileId + " from asset " + num);
                }
            }
        }
        
        System.out.println("[TileRenderer] Loaded " + tileSprites.size() + " tile sprites");
    }
    
    /**
     * Map asset number to tile ID (heuristic)
     * @param number Asset number
     * @return Tile ID, or -1 if not mapped
     */
    private int mapNumberToTileId(int number) {
        // Simple mapping - can be adjusted based on actual game assets
        switch (number) {
            case 0: return TileMap.TILE_GRASS;
            case 1: return TileMap.TILE_DIRT;
            case 2: return TileMap.TILE_WATER;
            case 3: return TileMap.TILE_PATH;
            case 4: return TileMap.TILE_FARMLAND;
            case 5: return TileMap.TILE_STONE;
            default:
                // For other numbers, try to map sequentially
                if (number < 10) {
                    return number;  // Use number as tile ID if < 10
                }
                return -1;
        }
    }
    
    /**
     * Get sprite for a tile ID
     * @param tileId Tile ID
     * @return Sprite for tile, or null if not found
     */
    public Sprite getTileSprite(int tileId) {
        return tileSprites.get(tileId);
    }
    
    /**
     * Register a tile sprite
     * @param tileId Tile ID
     * @param sprite Sprite to use for this tile
     */
    public void registerTileSprite(int tileId, Sprite sprite) {
        tileSprites.put(tileId, sprite);
    }
    
    /**
     * Register a tile sprite from an image
     * @param tileId Tile ID
     * @param image Image to use
     */
    public void registerTileSprite(int tileId, Image image) {
        if (image != null) {
            Sprite sprite = new Sprite(image, "tile_" + tileId);
            tileSprites.put(tileId, sprite);
        }
    }
    
    /**
     * Render the tile map
     * @param renderer Renderer to use
     * @param map TileMap to render
     * @param camera Camera for viewport culling
     */
    public void render(Renderer renderer, TileMap map, Camera camera) {
        if (map == null || camera == null) {
            return;
        }
        
        int tileWidth = map.getTileWidth();
        int tileHeight = map.getTileHeight();
        
        // Calculate which tiles are visible (culling)
        int startTileX = camera.getViewX() / tileWidth;
        int startTileY = camera.getViewY() / tileHeight;
        int endTileX = (camera.getViewX() + camera.getViewWidth()) / tileWidth + 1;
        int endTileY = (camera.getViewY() + camera.getViewHeight()) / tileHeight + 1;
        
        // Clamp to map bounds
        startTileX = Math.max(0, startTileX);
        startTileY = Math.max(0, startTileY);
        endTileX = Math.min(map.getWidth(), endTileX);
        endTileY = Math.min(map.getHeight(), endTileY);
        
        // Render visible tiles
        for (int ty = startTileY; ty < endTileY; ty++) {
            for (int tx = startTileX; tx < endTileX; tx++) {
                int tileId = map.getTile(tx, ty);
                if (tileId < 0) continue;
                
                // Calculate world position
                int worldX = tx * tileWidth;
                int worldY = ty * tileHeight;
                
                // Convert to screen coordinates
                int[] screenCoords = camera.worldToScreen(worldX, worldY);
                int screenX = screenCoords[0];
                int screenY = screenCoords[1];
                
                // Render tile
                renderTile(renderer, tileId, screenX, screenY, tileWidth, tileHeight);
            }
        }
    }
    
    /**
     * Render a single tile
     * @param renderer Renderer to use
     * @param tileId Tile ID
     * @param x Screen X coordinate
     * @param y Screen Y coordinate
     * @param width Tile width
     * @param height Tile height
     */
    private void renderTile(Renderer renderer, int tileId, int x, int y, int width, int height) {
        // Try to render with sprite first
        Sprite sprite = tileSprites.get(tileId);
        if (sprite != null && sprite.isValid()) {
            renderer.drawSprite(sprite, x, y, width, height);
            return;
        }
        
        // Fallback to colored rectangle
        Color color = tileColors.get(tileId);
        if (color != null) {
            renderer.fillRect(x, y, width, height, color);
        } else {
            // Default color if tile type not found
            renderer.fillRect(x, y, width, height, Color.MAGENTA);
        }
    }
    
    /**
     * Get the number of loaded tile sprites
     * @return Number of tile sprites
     */
    public int getLoadedSpriteCount() {
        return tileSprites.size();
    }
}
