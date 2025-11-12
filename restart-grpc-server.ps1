Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Restarting gRPC Server" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Step 1: Stop the running process
Write-Host "[1/3] Stopping GrpcService process..." -ForegroundColor Yellow
$process = Get-Process -Name "GrpcService" -ErrorAction SilentlyContinue
if ($process) {
    Stop-Process -Name "GrpcService" -Force
    Start-Sleep -Seconds 2
    Write-Host "      ✓ Process stopped" -ForegroundColor Green
} else {
    Write-Host "      ℹ No running process found" -ForegroundColor Gray
}

# Step 2: Build the project
Write-Host ""
Write-Host "[2/3] Building GrpcService..." -ForegroundColor Yellow
Set-Location "D:\SEP 3\LibraryManagementSystem\LogicServer\Server\GrpcService"
dotnet build --no-incremental

if ($LASTEXITCODE -eq 0) {
    Write-Host "      ✓ Build successful" -ForegroundColor Green
} else {
    Write-Host "      ✗ Build failed" -ForegroundColor Red
    exit 1
}

# Step 3: Run the server
Write-Host ""
Write-Host "[3/3] Starting GrpcService..." -ForegroundColor Yellow
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Server is starting on port 9090" -ForegroundColor Green
Write-Host "  Press Ctrl+C to stop" -ForegroundColor Gray
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

dotnet run

