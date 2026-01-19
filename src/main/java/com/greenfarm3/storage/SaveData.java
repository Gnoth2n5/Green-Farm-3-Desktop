package com.greenfarm3.storage;

import com.greenfarm3.game.GameState;

/**
 * Data class for serializing/deserializing game state.
 * Contains all necessary data to restore game state.
 */
public class SaveData {
    
    // Add game-specific save data fields here
    // Example:
    // private int playerLevel;
    // private int coins;
    // private int[] inventory;
    // private long playTime;
    
    private long timestamp;
    private String version;
    
    public SaveData() {
        this.timestamp = System.currentTimeMillis();
        this.version = "1.0.0";
    }
    
    /**
     * Populate save data from game state
     * @param gameState Current game state
     */
    public void fromGameState(GameState gameState) {
        // Extract data from game state
        // This will be implemented based on actual game state structure
    }
    
    /**
     * Create game state from save data
     * @return Restored game state
     */
    public GameState toGameState() {
        // Create and populate game state from save data
        // This will be implemented based on actual game state structure
        return null; // Placeholder
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
}
