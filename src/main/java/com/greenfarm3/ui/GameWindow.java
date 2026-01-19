package com.greenfarm3.ui;

import com.greenfarm3.game.GameEngine;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Main game window. Replaces J2ME Display and Canvas.
 * Manages the JavaFX Stage and Scene.
 */
public class GameWindow {
    
    private final Stage stage;
    private Scene scene;
    private Canvas canvas;
    private GraphicsContext gc;
    private GameEngine gameEngine;
    
    // Default game dimensions (can be scaled for desktop)
    private static final int BASE_WIDTH = 240;  // Typical J2ME width
    private static final int BASE_HEIGHT = 320; // Typical J2ME height
    private static final int SCALE = 2; // Scale factor for desktop
    
    public GameWindow(Stage stage) {
        this.stage = stage;
    }
    
    public void initialize() {
        // Create canvas
        canvas = new Canvas(BASE_WIDTH * SCALE, BASE_HEIGHT * SCALE);
        gc = canvas.getGraphicsContext2D();
        
        // Create menu bar
        MenuBar menuBar = createMenuBar();
        
        // Setup scene with menu bar
        StackPane canvasPane = new StackPane();
        canvasPane.getChildren().add(canvas);
        
        BorderPane root = new BorderPane();
        root.setTop(menuBar);
        root.setCenter(canvasPane);
        
        scene = new Scene(root, BASE_WIDTH * SCALE, BASE_HEIGHT * SCALE + 25); // +25 for menu bar
        
        // Setup stage
        stage.setTitle("Green Farm 3");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.setOnCloseRequest(e -> cleanup());
        
        // Initialize game engine
        gameEngine = new GameEngine(canvas, gc);
        gameEngine.initialize();
        
        // Setup input handlers
        setupInputHandlers();
    }
    
    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        
        // File menu
        Menu fileMenu = new Menu("File");
        MenuItem newGameItem = new MenuItem("New Game");
        newGameItem.setOnAction(e -> gameEngine.newGame());
        MenuItem loadGameItem = new MenuItem("Load Game");
        loadGameItem.setOnAction(e -> gameEngine.loadGame());
        MenuItem saveGameItem = new MenuItem("Save Game");
        saveGameItem.setOnAction(e -> gameEngine.saveGame());
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> cleanup());
        fileMenu.getItems().addAll(newGameItem, loadGameItem, saveGameItem, exitItem);
        
        // Settings menu
        Menu settingsMenu = new Menu("Settings");
        MenuItem graphicsItem = new MenuItem("Graphics...");
        graphicsItem.setOnAction(e -> showGraphicsSettings());
        MenuItem controlsItem = new MenuItem("Controls...");
        controlsItem.setOnAction(e -> showControlsSettings());
        settingsMenu.getItems().addAll(graphicsItem, controlsItem);
        
        // Help menu
        Menu helpMenu = new Menu("Help");
        MenuItem aboutItem = new MenuItem("About");
        aboutItem.setOnAction(e -> showAbout());
        helpMenu.getItems().add(aboutItem);
        
        menuBar.getMenus().addAll(fileMenu, settingsMenu, helpMenu);
        return menuBar;
    }
    
    private void showGraphicsSettings() {
        // TODO: Implement graphics settings dialog
        System.out.println("Graphics settings");
    }
    
    private void showControlsSettings() {
        // TODO: Implement controls settings dialog
        System.out.println("Controls settings");
    }
    
    private void showAbout() {
        // TODO: Implement about dialog
        System.out.println("Green Farm 3 Desktop v1.0.0");
    }
    
    private void setupInputHandlers() {
        // Mouse input (replaces J2ME keypad)
        canvas.setOnMouseClicked(e -> {
            int x = (int)(e.getX() / SCALE);
            int y = (int)(e.getY() / SCALE);
            gameEngine.handleClick(x, y);
        });
        
        // Keyboard input
        scene.setOnKeyPressed(e -> {
            gameEngine.handleKeyPress(e.getCode());
        });
        
        scene.setOnKeyReleased(e -> {
            gameEngine.handleKeyRelease(e.getCode());
        });
    }
    
    public void show() {
        stage.show();
        gameEngine.start();
    }
    
    public void cleanup() {
        if (gameEngine != null) {
            gameEngine.stop();
            gameEngine.saveGame();
        }
    }
}
