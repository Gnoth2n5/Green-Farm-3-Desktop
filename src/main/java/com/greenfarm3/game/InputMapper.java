package com.greenfarm3.game;

import javafx.scene.input.KeyCode;
import java.util.HashMap;
import java.util.Map;

/**
 * Maps desktop keyboard/mouse input to J2ME keypad equivalents.
 * Helps maintain compatibility with original game input handling.
 */
public class InputMapper {
    
    // J2ME key codes (from original game)
    public static final int KEY_NUM0 = 48;
    public static final int KEY_NUM1 = 49;
    public static final int KEY_NUM2 = 50;
    public static final int KEY_NUM3 = 51;
    public static final int KEY_NUM4 = 52;
    public static final int KEY_NUM5 = 53;
    public static final int KEY_NUM6 = 54;
    public static final int KEY_NUM7 = 55;
    public static final int KEY_NUM8 = 56;
    public static final int KEY_NUM9 = 57;
    public static final int KEY_STAR = 42;
    public static final int KEY_POUND = 35;
    public static final int KEY_UP = -1;
    public static final int KEY_DOWN = -2;
    public static final int KEY_LEFT = -3;
    public static final int KEY_RIGHT = -4;
    public static final int KEY_FIRE = -5;
    public static final int KEY_SOFT_LEFT = -6;
    public static final int KEY_SOFT_RIGHT = -7;
    public static final int KEY_SEND = -8;
    
    private static final Map<KeyCode, Integer> keyMap = new HashMap<>();
    
    static {
        // Map arrow keys
        keyMap.put(KeyCode.UP, KEY_UP);
        keyMap.put(KeyCode.DOWN, KEY_DOWN);
        keyMap.put(KeyCode.LEFT, KEY_LEFT);
        keyMap.put(KeyCode.RIGHT, KEY_RIGHT);
        
        // Map number keys
        keyMap.put(KeyCode.DIGIT0, KEY_NUM0);
        keyMap.put(KeyCode.DIGIT1, KEY_NUM1);
        keyMap.put(KeyCode.DIGIT2, KEY_NUM2);
        keyMap.put(KeyCode.DIGIT3, KEY_NUM3);
        keyMap.put(KeyCode.DIGIT4, KEY_NUM4);
        keyMap.put(KeyCode.DIGIT5, KEY_NUM5);
        keyMap.put(KeyCode.DIGIT6, KEY_NUM6);
        keyMap.put(KeyCode.DIGIT7, KEY_NUM7);
        keyMap.put(KeyCode.DIGIT8, KEY_NUM8);
        keyMap.put(KeyCode.DIGIT9, KEY_NUM9);
        
        // Map special keys
        keyMap.put(KeyCode.ENTER, KEY_FIRE);
        keyMap.put(KeyCode.SPACE, KEY_FIRE);
        keyMap.put(KeyCode.ESCAPE, KEY_SOFT_LEFT);
        
        // Map WASD for movement
        keyMap.put(KeyCode.W, KEY_UP);
        keyMap.put(KeyCode.S, KEY_DOWN);
        keyMap.put(KeyCode.A, KEY_LEFT);
        keyMap.put(KeyCode.D, KEY_RIGHT);
    }
    
    /**
     * Convert JavaFX KeyCode to J2ME key code
     * @param keyCode JavaFX KeyCode
     * @return J2ME key code, or 0 if not mapped
     */
    public static int mapKey(KeyCode keyCode) {
        return keyMap.getOrDefault(keyCode, 0);
    }
    
    /**
     * Check if a key code is a directional key
     * @param keyCode J2ME key code
     * @return true if directional
     */
    public static boolean isDirectional(int keyCode) {
        return keyCode == KEY_UP || keyCode == KEY_DOWN || 
               keyCode == KEY_LEFT || keyCode == KEY_RIGHT;
    }
    
    /**
     * Check if a key code is a number key
     * @param keyCode J2ME key code
     * @return true if number key
     */
    public static boolean isNumberKey(int keyCode) {
        return keyCode >= KEY_NUM0 && keyCode <= KEY_NUM9;
    }
}
