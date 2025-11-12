Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "  Rebuilding and Starting gRPC Server" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""

# Navigate to the GrpcService directory
$grpcPath = "D:\SEP 3\LibraryManagementSystem\LogicServer\Server\GrpcService"
Set-Location $grpcPath

# Step 1: Kill any running GrpcService processes
Write-Host "[1/4] Stopping any running GrpcService..." -ForegroundColor Yellow
$processes = Get-Process -Name "GrpcService" -ErrorAction SilentlyContinue
if ($processes) {
    foreach ($proc in $processes) {
        Write-Host "      Killing process ID: $($proc.Id)" -ForegroundColor Gray
        Stop-Process -Id $proc.Id -Force
    }
    Start-Sleep -Seconds 2
    Write-Host "      Success: Processes stopped" -ForegroundColor Green
} else {
    Write-Host "      Info: No running GrpcService found" -ForegroundColor Gray
}

# Step 2: Clean the project
Write-Host ""
Write-Host "[2/4] Cleaning old build files..." -ForegroundColor Yellow
dotnet clean | Out-Null
Remove-Item -Path "bin" -Recurse -Force -ErrorAction SilentlyContinue
Remove-Item -Path "obj" -Recurse -Force -ErrorAction SilentlyContinue
Write-Host "      Success: Clean complete" -ForegroundColor Green

# Step 3: Build the project (this regenerates proto files)
Write-Host ""
Write-Host "[3/4] Building GrpcService (regenerating proto files)..." -ForegroundColor Yellow
$buildOutput = dotnet build --no-incremental 2>&1

if ($LASTEXITCODE -eq 0) {
    Write-Host "      Success: Build successful" -ForegroundColor Green
    Write-Host "      Success: Proto files regenerated" -ForegroundColor Green
} else {
    Write-Host "      Error: Build failed!" -ForegroundColor Red
    Write-Host ""
    Write-Host "Build output:" -ForegroundColor Red
    $buildOutput | Write-Host
    exit 1
}

# Step 4: Run the server
Write-Host ""
Write-Host "[4/4] Starting GrpcService..." -ForegroundColor Yellow
Write-Host ""
Write-Host "=====================================" -ForegroundColor Green
Write-Host "  gRPC Server Starting on Port 9090" -ForegroundColor Green
Write-Host "  GetBook and CreateLoan are NOW available!" -ForegroundColor Green
Write-Host "=====================================" -ForegroundColor Green
Write-Host ""
Write-Host "Server is running. Check the output below." -ForegroundColor Gray
Write-Host ""

# Run the server
dotnet run --no-build

