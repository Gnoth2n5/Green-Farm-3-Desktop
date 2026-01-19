# Build Instructions

## Prerequisites

1. **Java JDK 17+**: Download from [Oracle](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://adoptium.net/)
2. **Maven 3.6+**: Download from [Apache Maven](https://maven.apache.org/download.cgi)

## Building the Project

### Step 1: Verify Java Installation

```bash
java -version
javac -version
```

Should show Java 17 or higher.

### Step 2: Verify Maven Installation

```bash
mvn -version
```

### Step 3: Build the Project

```bash
# Clean and compile
mvn clean compile

# Package into JAR
mvn package
```

The JAR file will be created in `target/greenfarm3-desktop-1.0.0.jar`

### Step 4: Run the Application

```bash
# Using Maven JavaFX plugin
mvn javafx:run

# Or run JAR directly (requires JavaFX on module path)
java -jar target/greenfarm3-desktop-1.0.0.jar
```

## Creating Native Installer (Windows)

### Using jpackage (Java 14+)

```bash
# Build JAR first
mvn clean package

# Create Windows installer
jpackage --input target \
  --name "Green Farm 3" \
  --main-jar greenfarm3-desktop-1.0.0.jar \
  --main-class com.greenfarm3.Main \
  --type msi \
  --dest dist \
  --java-options "--module-path <javafx-path> --add-modules javafx.controls,javafx.fxml"
```

Note: jpackage requires JavaFX to be available. You may need to download JavaFX SDK separately for Java 17+.

## Troubleshooting

### JavaFX Module Not Found

If you get "module javafx.controls not found", you need to:

1. Download JavaFX SDK from [OpenJFX](https://openjfx.io/)
2. Extract it to a directory (e.g., `C:\javafx-sdk-17`)
3. Run with:
```bash
java --module-path C:\javafx-sdk-17\lib --add-modules javafx.controls,javafx.fxml -jar target/greenfarm3-desktop-1.0.0.jar
```

### Maven Not Found

If Maven is not in your PATH:
- Windows: Add Maven bin directory to PATH environment variable
- Or use full path: `C:\path\to\maven\bin\mvn.cmd`
