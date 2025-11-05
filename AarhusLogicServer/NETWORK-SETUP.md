# AarhusLogicServer - Network Connection Fix

## Current Status
- ✓ Server IP: 10.154.216.20
- ✓ Port: 8080
- ✓ Application is running and listening on 0.0.0.0:8080
- ✗ Windows Firewall is BLOCKING external connections

## Fix: Enable Windows Firewall for Port 8080

### Option 1: Run the PowerShell Script (RECOMMENDED)
1. Right-click `enable-firewall.ps1`
2. Select "Run with PowerShell"
3. If prompted, click "Yes" to allow administrator privileges

### Option 2: Run the Batch File
1. Right-click `enable-firewall.bat`
2. Select "Run as administrator"
3. Click "Yes" when prompted

### Option 3: Manual PowerShell Command
1. Press `Win + X`
2. Select "Terminal (Admin)" or "PowerShell (Admin)"
3. Run:
```powershell
New-NetFirewallRule -DisplayName "AarhusLogicServer Port 8080" -Direction Inbound -LocalPort 8080 -Protocol TCP -Action Allow
```

### Option 4: Windows Firewall GUI
1. Press `Win + R`, type `wf.msc`, press Enter
2. Click "Inbound Rules" → "New Rule..."
3. Select "Port" → Next
4. Select "TCP" → Specific local ports: `8080` → Next
5. Select "Allow the connection" → Next
6. Check all profiles (Domain, Private, Public) → Next
7. Name: "AarhusLogicServer Port 8080" → Finish

## After Adding Firewall Rule

### Test Connection (from other computer)
Ask the other person to run in PowerShell:
```powershell
Test-NetConnection -ComputerName 10.154.216.20 -Port 8080
```

Expected output:
```
TcpTestSucceeded : True
```

### Access the API
```
http://10.154.216.20:8080/Books
```

## Current Issue with gRPC Backend

Your application is connecting to a gRPC server at `10.154.220.46:9090`, which is returning:
```
UNIMPLEMENTED: Service is unimplemented
```

This means:
1. The gRPC server is running
2. But the `getAllBooks` method is not implemented on the server side
3. Your Spring Boot REST API will return empty results until the gRPC server implements the service

### To Fix the gRPC Issue
Contact the administrator of the gRPC server at `10.154.220.46:9090` and ensure they:
1. Have implemented the `getAllBooks` RPC method
2. Have registered the BookService implementation
3. Have restarted the gRPC server

## Verify Everything Works

### Check if firewall rule was added:
```cmd
netsh advfirewall firewall show rule name="AarhusLogicServer Port 8080"
```

### Check if app is listening:
```cmd
netstat -ano | findstr :8080
```

You should see:
```
TCP    0.0.0.0:8080           0.0.0.0:0              LISTENING
```

## Support
- Your IP Address: 10.154.216.20
- Server Port: 8080
- REST Endpoint: http://10.154.216.20:8080/Books
- gRPC Backend: 10.154.220.46:9090 (currently not fully implemented)

