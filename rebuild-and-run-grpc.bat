@echo off
echo =====================================
echo   Rebuilding and Starting gRPC Server
echo =====================================
echo.

REM Navigate to GrpcService directory
cd /d "D:\SEP 3\LibraryManagementSystem\LogicServer\Server\GrpcService"

REM Stop any running instances
echo [1/4] Stopping any running GrpcService...
taskkill /F /IM GrpcService.exe >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo       Success: Process stopped
) else (
    echo       Info: No running process found
)
timeout /t 2 /nobreak >nul

REM Clean old build files
echo.
echo [2/4] Cleaning old build files...
dotnet clean >nul 2>&1
if exist bin rmdir /s /q bin
if exist obj rmdir /s /q obj
echo       Success: Clean complete

REM Build the project
echo.
echo [3/4] Building GrpcService (regenerating proto files)...
dotnet build --no-incremental
if %ERRORLEVEL% NEQ 0 (
    echo       Error: Build failed!
    echo.
    echo Press any key to exit...
    pause >nul
    exit /b 1
)
echo       Success: Build complete
echo       Success: Proto files regenerated

REM Run the server
echo.
echo [4/4] Starting GrpcService...
echo.
echo =====================================
echo   gRPC Server Starting on Port 9090
echo   GetBook and CreateLoan are NOW available!
echo =====================================
echo.
echo Server is running. Press Ctrl+C to stop.
echo.

dotnet run --no-build

pause

