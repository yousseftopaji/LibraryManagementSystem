@echo off
REM Run this as Administrator to enable port 8080 for incoming connections
REM Right-click -> Run as Administrator

echo Adding Windows Firewall rule for port 8080...
echo.

netsh advfirewall firewall add rule name="AarhusLogicServer Port 8080" dir=in action=allow protocol=TCP localport=8080

if %errorlevel% equ 0 (
    echo.
    echo [SUCCESS] Firewall rule successfully added!
    echo.
    echo Your server is now accessible at: http://10.154.216.20:8080
    echo.
    echo Others can test the connection with:
    echo   Test-NetConnection -ComputerName 10.154.216.20 -Port 8080
    echo.
    echo Make sure your Spring Boot application is running!
) else (
    echo.
    echo [ERROR] Failed to add firewall rule.
    echo.
    echo Make sure you're running this batch file as Administrator!
    echo Right-click the file -^> Run as Administrator
)

echo.
pause

