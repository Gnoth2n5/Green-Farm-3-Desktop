@echo off
REM Build and package script for Windows

echo Building Green Farm 3 Desktop...
call mvn clean package

if %ERRORLEVEL% NEQ 0 (
    echo Build failed!
    pause
    exit /b 1
)

echo.
echo Build successful! JAR file created in target/
echo.
echo To run the application:
echo   mvn javafx:run
echo.
echo Or create native installer with jpackage (requires JavaFX SDK):
echo   jpackage --input target --name "Green Farm 3" --main-jar greenfarm3-desktop-1.0.0.jar --main-class com.greenfarm3.Main --type msi --dest dist
echo.
pause
