package com.greenfarm3.game;

import com.greenfarm3.game.states.MenuState;
import com.greenfarm3.game.states.PlayState;
import com.greenfarm3.storage.SaveManager;
import com.greenfarm3.ui.Renderer;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;

/**
 * Main game engine. Manages game loop, state, and rendering.
 * Replaces the J2ME Canvas game loop.
 */
public class GameEngine {
    
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final Renderer renderer;
    private AnimationTimer gameLoop;
    private GameState currentState;
    private SaveManager saveManager;
    
    private boolean running = false;
    private long lastFrameTime = 0;
    private static final long TARGET_FPS = 60;
    private static final long FRAME_TIME_NS = 1_000_000_000 / TARGET_FPS;
    private static final int SCALE = 2;
    
    public GameEngine(Canvas canvas, GraphicsContext gc) {
        this.canvas = canvas;
        this.gc = gc;
        this.renderer = new Renderer(gc, SCALE);
        this.saveManager = new SaveManager();
    }
    
    public void initialize() {
        // Initialize with menu state
        MenuState menuState = new MenuState(renderer);
        menuState.setStateChangeCallback(this::handleStateChange);
        currentState = menuState;
        currentState.initialize();
        
        // Setup game loop
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastFrameTime == 0) {
                    lastFrameTime = now;
                    return;
                }
                
                long deltaTime = now - lastFrameTime;
                
                // Cap frame time to prevent large jumps
                if (deltaTime > FRAME_TIME_NS * 2) {
                    deltaTime = FRAME_TIME_NS * 2;
                }
                
                update(deltaTime / 1_000_000.0); // Convert to milliseconds
                render();
                
                lastFrameTime = now;
            }
        };
    }
    
    public void start() {
        if (!running) {
            running = true;
            lastFrameTime = 0;
            gameLoop.start();
        }
    }
    
    public void stop() {
        if (running) {
            running = false;
            gameLoop.stop();
        }
    }
    
    private void update(double deltaTime) {
        if (currentState != null) {
            currentState.update(deltaTime);
        }
    }
    
    private void render() {
        // Clear canvas
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
        // Render current state
        if (currentState != null) {
            currentState.render(gc);
        }
    }
    
    public void handleClick(int x, int y) {
        if (currentState != null) {
            currentState.handleClick(x, y);
        }
    }
    
    public void handleKeyPress(KeyCode keyCode) {
        if (currentState != null) {
            currentState.handleKeyPress(keyCode);
        }
    }
    
    public void handleKeyRelease(KeyCode keyCode) {
        if (currentState != null) {
            currentState.handleKeyRelease(keyCode);
        }
    }
    
    public void saveGame() {
        if (currentState != null && saveManager != null) {
            saveManager.saveGame(currentState);
        }
    }
    
    public void loadGame() {
        if (saveManager != null) {
            GameState loaded = saveManager.loadGame();
            if (loaded != null) {
                setState(loaded);
            }
        }
    }
    
    public void newGame() {
        PlayState playState = new PlayState(renderer);
        setState(playState);
    }
    
    private void handleStateChange(String action) {
        switch (action) {
            case "new_game":
                newGame();
                break;
            case "load_game":
                loadGame();
                break;
            case "settings":
                // TODO: Open settings dialog
                System.out.println("Settings");
                break;
        }
    }
    
    public void setState(GameState newState) {
        if (currentState != null) {
            currentState.cleanup();
        }
        currentState = newState;
        if (currentState != null) {
            currentState.initialize();
        }
    }
    
    public GameState getCurrentState() {
        return currentState;
    }
}
