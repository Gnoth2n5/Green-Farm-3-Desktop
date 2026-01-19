@echo off
REM Simple launcher script for Windows
REM Logs output to log/game_YYYYMMDD_HHMMSS.txt

REM Create log directory if it doesn't exist
if not exist "log" mkdir log

REM Generate timestamp for log file using PowerShell (more reliable)
for /f %%i in ('powershell -Command "Get-Date -Format 'yyyyMMdd_HHmmss'"') do set timestamp=%%i

REM Set log file path
set LOGFILE=log\game_%timestamp%.txt

echo Starting Green Farm 3 Desktop...
echo Log file: %LOGFILE%
echo.

REM Run application and redirect all output to log file (append mode)
call mvn javafx:run >> "%LOGFILE%" 2>&1

REM Capture exit code
set EXITCODE=%ERRORLEVEL%

REM Display result
if %EXITCODE% NEQ 0 (
    echo.
    echo Failed to start application!
    echo Check log file for details: %LOGFILE%
    echo Make sure Maven and Java are installed and configured correctly.
    pause
    exit /b %EXITCODE%
) else (
    echo.
    echo Application closed.
    echo Log saved to: %LOGFILE%
)
