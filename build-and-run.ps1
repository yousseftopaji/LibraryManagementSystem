# Build and Run Script for Library Management System
# This script helps you build and start all services

Write-Host "======================================" -ForegroundColor Cyan
Write-Host "Library Management System - Build Script" -ForegroundColor Cyan
Write-Host "======================================" -ForegroundColor Cyan
Write-Host ""

# Function to check if a port is in use
function Test-Port {
    param([int]$Port)
    $connection = Get-NetTCPConnection -LocalPort $Port -ErrorAction SilentlyContinue
    return $null -ne $connection
}

# Step 1: Build C# gRPC Server
Write-Host "[Step 1] Building C# gRPC Server..." -ForegroundColor Yellow
Set-Location "D:\SEP 3\LibraryManagementSystem\LogicServer\Server\GrpcService"

if (Test-Port 9090) {
    Write-Host "WARNING: Port 9090 is already in use. Please stop the running GrpcService first." -ForegroundColor Red
    Write-Host "You can find the process with: Get-NetTCPConnection -LocalPort 9090" -ForegroundColor Red
    exit 1
}

Write-Host "Running: dotnet clean..." -ForegroundColor Gray
dotnet clean | Out-Null

Write-Host "Running: dotnet build..." -ForegroundColor Gray
$buildResult = dotnet build 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: C# build failed!" -ForegroundColor Red
    Write-Host $buildResult -ForegroundColor Red
    exit 1
}

Write-Host "✓ C# gRPC Server build successful!" -ForegroundColor Green
Write-Host ""

# Step 2: Build Java REST API Server
Write-Host "[Step 2] Building Java REST API Server..." -ForegroundColor Yellow
Set-Location "D:\SEP 3\LibraryManagementSystem\AarhusLogicServer"

Write-Host "Running: mvnw clean compile..." -ForegroundColor Gray
$mvnResult = & .\mvnw.cmd clean compile 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Java build failed!" -ForegroundColor Red
    Write-Host "Check the output above for errors" -ForegroundColor Red
    exit 1
}

Write-Host "✓ Java REST API Server build successful!" -ForegroundColor Green
Write-Host ""

# Step 3: Instructions to run
Write-Host "======================================" -ForegroundColor Cyan
Write-Host "Build Complete! Ready to Run" -ForegroundColor Cyan
Write-Host "======================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "To start the servers, open TWO separate PowerShell windows:" -ForegroundColor White
Write-Host ""
Write-Host "[Window 1] C# gRPC Server (Port 9090):" -ForegroundColor Yellow
Write-Host "  cd 'D:\SEP 3\LibraryManagementSystem\LogicServer\Server\GrpcService'" -ForegroundColor Gray
Write-Host "  dotnet run" -ForegroundColor Gray
Write-Host ""
Write-Host "[Window 2] Java REST API Server (Port 8080):" -ForegroundColor Yellow
Write-Host "  cd 'D:\SEP 3\LibraryManagementSystem\AarhusLogicServer'" -ForegroundColor Gray
Write-Host "  .\mvnw.cmd spring-boot:run" -ForegroundColor Gray
Write-Host ""
Write-Host "After both servers are running, test with:" -ForegroundColor White
Write-Host "  GET  http://localhost:8080/books/9780132350884" -ForegroundColor Cyan
Write-Host "  POST http://localhost:8080/loans" -ForegroundColor Cyan
Write-Host ""
Write-Host "See QUICK_START_GUIDE.md for more details!" -ForegroundColor Green

