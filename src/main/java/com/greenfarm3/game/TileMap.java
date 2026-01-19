package com.greenfarm3.game;

/**
 * Represents a tile-based map.
 * Manages tile data and provides methods to get/set tiles.
 * Based on the original J2ME tileset system (class k).
 */
public class TileMap {
    
    // Tile type constants
    public static final int TILE_GRASS = 0;
    public static final int TILE_DIRT = 1;
    public static final int TILE_WATER = 2;
    public static final int TILE_PATH = 3;
    public static final int TILE_FARMLAND = 4;
    public static final int TILE_STONE = 5;
    
    // Tile properties
    public static final int PROPERTY_WALKABLE = 1;
    public static final int PROPERTY_PLANTABLE = 2;
    
    private int[][] tiles;  // tileId tại mỗi vị trí [y][x]
    private int tileWidth;
    private int tileHeight;
    private int mapWidth;   // Số tiles theo chiều ngang
    private int mapHeight;  // Số tiles theo chiều dọc
    
    /**
     * Create a new tile map
     * @param mapWidth Map width in tiles
     * @param mapHeight Map height in tiles
     * @param tileWidth Tile width in pixels
     * @param tileHeight Tile height in pixels
     */
    public TileMap(int mapWidth, int mapHeight, int tileWidth, int tileHeight) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.tiles = new int[mapHeight][mapWidth];
        
        // Initialize with grass tiles
        fill(TILE_GRASS);
    }
    
    /**
     * Create a tile map with default tile size (32x32)
     * @param mapWidth Map width in tiles
     * @param mapHeight Map height in tiles
     */
    public TileMap(int mapWidth, int mapHeight) {
        this(mapWidth, mapHeight, 32, 32);
    }
    
    /**
     * Get tile ID at position
     * @param x Tile X coordinate
     * @param y Tile Y coordinate
     * @return Tile ID, or -1 if invalid position
     */
    public int getTile(int x, int y) {
        if (!isValidPosition(x, y)) {
            return -1;
        }
        return tiles[y][x];
    }
    
    /**
     * Set tile at position
     * @param x Tile X coordinate
     * @param y Tile Y coordinate
     * @param tileId Tile ID to set
     * @return true if successful, false if invalid position
     */
    public boolean setTile(int x, int y, int tileId) {
        if (!isValidPosition(x, y)) {
            return false;
        }
        tiles[y][x] = tileId;
        return true;
    }
    
    /**
     * Check if position is valid
     * @param x Tile X coordinate
     * @param y Tile Y coordinate
     * @return true if position is within map bounds
     */
    public boolean isValidPosition(int x, int y) {
        return x >= 0 && x < mapWidth && y >= 0 && y < mapHeight;
    }
    
    /**
     * Fill entire map with a tile type
     * @param tileId Tile ID to fill with
     */
    public void fill(int tileId) {
        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                tiles[y][x] = tileId;
            }
        }
    }
    
    /**
     * Fill a rectangular area with a tile type
     * @param x Start X coordinate
     * @param y Start Y coordinate
     * @param width Width in tiles
     * @param height Height in tiles
     * @param tileId Tile ID to fill with
     */
    public void fillRect(int x, int y, int width, int height, int tileId) {
        for (int ty = y; ty < y + height && ty < mapHeight; ty++) {
            if (ty < 0) continue;
            for (int tx = x; tx < x + width && tx < mapWidth; tx++) {
                if (tx < 0) continue;
                tiles[ty][tx] = tileId;
            }
        }
    }
    
    /**
     * Generate a default map (grass with some dirt patches)
     */
    public void generateDefaultMap() {
        // Fill with grass
        fill(TILE_GRASS);
        
        // Add some dirt patches
        fillRect(5, 5, 3, 3, TILE_DIRT);
        fillRect(10, 8, 4, 2, TILE_DIRT);
        fillRect(3, 12, 5, 4, TILE_FARMLAND);
        
        // Add a path
        for (int x = 0; x < mapWidth; x++) {
            setTile(x, mapHeight / 2, TILE_PATH);
        }
        for (int y = 0; y < mapHeight; y++) {
            setTile(mapWidth / 2, y, TILE_PATH);
        }
    }
    
    /**
     * Check if a tile type is walkable
     * @param tileId Tile ID
     * @return true if walkable
     */
    public static boolean isWalkable(int tileId) {
        return tileId != TILE_WATER && tileId != TILE_STONE;
    }
    
    /**
     * Check if a tile type is plantable
     * @param tileId Tile ID
     * @return true if plantable
     */
    public static boolean isPlantable(int tileId) {
        return tileId == TILE_DIRT || tileId == TILE_FARMLAND;
    }
    
    /**
     * Get map width in tiles
     * @return Map width
     */
    public int getWidth() {
        return mapWidth;
    }
    
    /**
     * Get map height in tiles
     * @return Map height
     */
    public int getHeight() {
        return mapHeight;
    }
    
    /**
     * Get tile width in pixels
     * @return Tile width
     */
    public int getTileWidth() {
        return tileWidth;
    }
    
    /**
     * Get tile height in pixels
     * @return Tile height
     */
    public int getTileHeight() {
        return tileHeight;
    }
    
    /**
     * Get map width in pixels
     * @return Map width in pixels
     */
    public int getPixelWidth() {
        return mapWidth * tileWidth;
    }
    
    /**
     * Get map height in pixels
     * @return Map height in pixels
     */
    public int getPixelHeight() {
        return mapHeight * tileHeight;
    }
    
    /**
     * Convert world pixel coordinates to tile coordinates
     * @param pixelX Pixel X coordinate
     * @param pixelY Pixel Y coordinate
     * @return Array with [tileX, tileY], or null if out of bounds
     */
    public int[] pixelToTile(int pixelX, int pixelY) {
        int tileX = pixelX / tileWidth;
        int tileY = pixelY / tileHeight;
        
        if (isValidPosition(tileX, tileY)) {
            return new int[]{tileX, tileY};
        }
        return null;
    }
    
    /**
     * Convert tile coordinates to world pixel coordinates (top-left of tile)
     * @param tileX Tile X coordinate
     * @param tileY Tile Y coordinate
     * @return Array with [pixelX, pixelY], or null if invalid
     */
    public int[] tileToPixel(int tileX, int tileY) {
        if (!isValidPosition(tileX, tileY)) {
            return null;
        }
        return new int[]{tileX * tileWidth, tileY * tileHeight};
    }
}
