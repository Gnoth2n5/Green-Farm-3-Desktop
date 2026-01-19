@echo off
REM Simple launcher script for Windows

echo Starting Green Farm 3 Desktop...
call mvn javafx:run

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Failed to start application!
    echo Make sure Maven and Java are installed and configured correctly.
    pause
    exit /b 1
)
