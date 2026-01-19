# Green Farm 3 J2ME - Code Analysis

## Decompilation Summary

The game has been decompiled using CFR 0.152. The code is heavily obfuscated with single-letter class names.

## Main Classes

### Entry Point
- **GloftGF2M** extends `MIDlet` - Main application entry point
  - `startApp()` - Initializes display and game canvas
  - `pauseApp()` - Handles pause
  - `destroyApp()` - Handles cleanup and save

### Core Game Classes
- **a** extends **g** implements **b** - Main game canvas (very large, 29k+ lines)
  - Contains most game logic, rendering, and state management
  
- **g** extends `Canvas` implements `Runnable` - Base canvas class
  - Handles graphics, display, input
  - Manages RecordStore for save data
  - Contains image loading and resource management

### Other Classes (Obfuscated)
- **b, c, d, e, f, h, i, j, k, l, m, n, o, p** - Various utility and game logic classes
  - Likely include: sprites, game objects, UI elements, data structures

## J2ME Dependencies to Replace

1. **javax.microedition.midlet.MIDlet** → JavaFX Application
2. **javax.microedition.lcdui.Canvas** → JavaFX Canvas or Pane
3. **javax.microedition.lcdui.Graphics** → JavaFX GraphicsContext
4. **javax.microedition.lcdui.Display** → JavaFX Stage/Scene
5. **javax.microedition.rms.RecordStore** → File I/O (JSON/Properties)
6. **javax.microedition.lcdui.Command** → JavaFX Menu/Button

## Assets Structure

From extracted JAR:
- `icon.png` - Application icon
- Numbered files (0, 1, 2, ...) - Likely image/sprite data
- Text files: `EN`, `dataIGP`, `IAP_profiles`, `IAP_texts`
- Class files for all game classes

## Key Observations

1. **Obfuscation**: Code uses single-letter class names, making analysis difficult
2. **Large Main Class**: Class `a` is extremely large (29k+ lines) - needs refactoring
3. **Canvas-based**: Game uses Canvas for rendering (pixel-level control)
4. **RMS Storage**: Uses RecordStore for save data
5. **Thread-based**: Implements Runnable for game loop

## Migration Strategy

1. Create new clean architecture with proper class names
2. Port game logic incrementally
3. Replace J2ME APIs with JavaFX equivalents
4. Extract and organize assets properly
5. Implement new save system
6. Refactor large classes into smaller, manageable components
