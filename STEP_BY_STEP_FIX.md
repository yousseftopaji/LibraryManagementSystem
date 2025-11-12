# STEP-BY-STEP: Fix the UNIMPLEMENTED Error

## The Problem
You're still getting the UNIMPLEMENTED error because the gRPC server needs to be **completely rebuilt and restarted** with the new code.

## DO THESE STEPS EXACTLY (In Order)

### Step 1: Stop the Running gRPC Server

**Open PowerShell or Command Prompt and run:**
```powershell
taskkill /F /IM GrpcService.exe
```

**OR** if you started it in IntelliJ/Rider:
- Find the Run window (usually at the bottom)
- Click the red STOP button ⏹️

**Verify it's stopped:**
```powershell
netstat -ano | findstr :9090
```
This should show NOTHING. If it shows something, the server is still running.

---

### Step 2: Navigate to the GrpcService Folder

```powershell
cd "D:\SEP 3\LibraryManagementSystem\LogicServer\Server\GrpcService"
```

---

### Step 3: Clean Old Build Files

```powershell
dotnet clean
Remove-Item -Path bin -Recurse -Force -ErrorAction SilentlyContinue
Remove-Item -Path obj -Recurse -Force -ErrorAction SilentlyContinue
```

---

### Step 4: Rebuild the Project (THIS REGENERATES PROTO FILES)

```powershell
dotnet build
```

**WAIT** for this to complete. You should see:
```
Build succeeded.
    0 Warning(s)
    0 Error(s)
```

**If you see errors**, copy them and let me know.

---

### Step 5: Run the Server

```powershell
dotnet run
```

You should see output like:
```
Connected to PostgreSQL: ...
Now listening on: http://0.0.0.0:9090
Application started. Press Ctrl+C to shut down.
```

**LEAVE THIS WINDOW OPEN** - the server is now running!

---

### Step 6: Test the Endpoint

**Open a NEW PowerShell/Terminal window** (don't close the server window!)

**Test 1: Get All Books**
```powershell
curl http://localhost:8080/books
```

**Test 2: Get Single Book** (use an actual ISBN from your database)
```powershell
curl http://localhost:8080/books/YOUR-ACTUAL-ISBN-HERE
```

**Test 3: Create Loan** (use an actual ISBN from your database)
```powershell
curl -X POST http://localhost:8080/loans -H "Content-Type: application/json" -d '{\"bookISBN\":\"YOUR-ACTUAL-ISBN-HERE\",\"username\":\"testuser\"}'
```

---

## What To Look For

### In the gRPC Server Window (dotnet run):
When you call the endpoints, you should see logs like:
```
Received request to get book with ISBN: 978-0-123456-78-9
Received request to create loan for ISBN: 978-0-123456-78-9, Username: testuser
```

### In the AarhusLogicServer Window:
You should see logs like:
```
Sending gRPC request to get book with ISBN: 978-0-123456-78-9
Received gRPC response: ...
```

---

## If You STILL Get UNIMPLEMENTED Error

### Check 1: Is the gRPC server actually running?
```powershell
netstat -ano | findstr :9090
```
Should show something like:
```
TCP    0.0.0.0:9090    0.0.0.0:0    LISTENING    12345
```

### Check 2: Did the build actually regenerate the proto files?
```powershell
cd "D:\SEP 3\LibraryManagementSystem\LogicServer\Server\GrpcService"
dir obj\Debug\net10.0\Protos
```
You should see files like:
- Books.cs
- BooksGrpc.cs
- GetBookRequest.cs
- CreateLoanRequest.cs
- etc.

### Check 3: Is Program.cs mapping the services?
```powershell
Get-Content Program.cs | Select-String "MapGrpcService"
```
Should show BOTH:
```csharp
app.MapGrpcService<BookServiceImpl>();
app.MapGrpcService<LoanServiceImpl>();
```

---

## Alternative: Use IntelliJ/Rider

If you have the gRPC project open in IntelliJ IDEA or Rider:

1. Right-click on `GrpcService` project
2. Select "Build" → "Rebuild Project"
3. Wait for build to complete
4. Right-click on `Program.cs`
5. Select "Run 'GrpcService'"
6. Check the Run window at the bottom for server logs

---

## Quick Copy-Paste Commands

**Copy and paste this entire block into PowerShell:**

```powershell
# Stop server
taskkill /F /IM GrpcService.exe 2>$null

# Navigate
cd "D:\SEP 3\LibraryManagementSystem\LogicServer\Server\GrpcService"

# Clean
dotnet clean
Remove-Item -Path bin,obj -Recurse -Force -ErrorAction SilentlyContinue

# Build (this regenerates proto files)
Write-Host "Building... This will take a moment..." -ForegroundColor Yellow
dotnet build

if ($LASTEXITCODE -eq 0) {
    Write-Host "Build successful! Starting server..." -ForegroundColor Green
    Write-Host ""
    Write-Host "=====================" -ForegroundColor Cyan
    Write-Host "Server starting on port 9090" -ForegroundColor Cyan
    Write-Host "=====================" -ForegroundColor Cyan
    Write-Host ""
    dotnet run
} else {
    Write-Host "Build failed! Check errors above." -ForegroundColor Red
}
```

---

## After Server Starts

**Test it immediately:**

```powershell
# In a NEW terminal window:
curl http://localhost:8080/books
```

If this works, the server is running correctly.

Then try:
```powershell
# Get one book (replace ISBN with actual from your DB)
curl http://localhost:8080/books/978-0-123456-78-9
```

**If you STILL get UNIMPLEMENTED error after following ALL these steps**, then:
1. Copy the EXACT error message
2. Copy the output from the gRPC server window
3. Copy the output from the AarhusLogicServer window
4. Let me know - there might be a different issue

---

## The Nuclear Option (If Nothing Else Works)

1. Close ALL terminal windows
2. Restart your computer
3. Start the gRPC server: `cd "D:\SEP 3\LibraryManagementSystem\LogicServer\Server\GrpcService"; dotnet run`
4. Start the AarhusLogicServer
5. Test again

Sometimes processes don't fully release ports, and a restart fixes it.

