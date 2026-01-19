package com.greenfarm3;

import com.greenfarm3.ui.GameWindow;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main entry point for Green Farm 3 Desktop application.
 * Replaces the J2ME MIDlet class.
 */
public class Main extends Application {
    
    private static GameWindow gameWindow;
    
    @Override
    public void start(Stage primaryStage) {
        try {
            gameWindow = new GameWindow(primaryStage);
            gameWindow.initialize();
            gameWindow.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    @Override
    public void stop() {
        // Cleanup when application closes
        if (gameWindow != null) {
            gameWindow.cleanup();
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
