package com.greenfarm3.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.greenfarm3.game.GameState;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Manages save/load functionality.
 * Replaces J2ME RecordStore (RMS) with file-based storage.
 */
public class SaveManager {
    
    private static final String SAVE_DIR = "saves";
    private static final String SAVE_FILE = "savegame.json";
    private final Gson gson;
    private final Path savePath;
    
    public SaveManager() {
        this.gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
        
        // Create saves directory if it doesn't exist
        this.savePath = Paths.get(SAVE_DIR);
        try {
            if (!Files.exists(savePath)) {
                Files.createDirectories(savePath);
            }
        } catch (IOException e) {
            System.err.println("Failed to create save directory: " + e.getMessage());
        }
    }
    
    /**
     * Save game state to file
     * @param gameState Game state to save
     */
    public void saveGame(GameState gameState) {
        try {
            // Convert game state to save data
            SaveData saveData = new SaveData();
            saveData.fromGameState(gameState);
            
            // Serialize to JSON
            String json = gson.toJson(saveData);
            
            // Write to file
            Path filePath = savePath.resolve(SAVE_FILE);
            Files.write(filePath, json.getBytes());
            
            System.out.println("Game saved successfully");
        } catch (IOException e) {
            System.err.println("Failed to save game: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Load game state from file
     * @return Loaded game state, or null if load failed
     */
    public GameState loadGame() {
        try {
            Path filePath = savePath.resolve(SAVE_FILE);
            
            if (!Files.exists(filePath)) {
                System.out.println("No save file found");
                return null;
            }
            
            // Read from file
            String json = new String(Files.readAllBytes(filePath));
            
            // Deserialize from JSON
            SaveData saveData = gson.fromJson(json, SaveData.class);
            
            // Convert to game state
            GameState gameState = saveData.toGameState();
            
            System.out.println("Game loaded successfully");
            return gameState;
        } catch (IOException e) {
            System.err.println("Failed to load game: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Check if a save file exists
     * @return true if save file exists
     */
    public boolean hasSaveFile() {
        Path filePath = savePath.resolve(SAVE_FILE);
        return Files.exists(filePath);
    }
    
    /**
     * Delete save file
     */
    public void deleteSave() {
        try {
            Path filePath = savePath.resolve(SAVE_FILE);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                System.out.println("Save file deleted");
            }
        } catch (IOException e) {
            System.err.println("Failed to delete save: " + e.getMessage());
        }
    }
}
