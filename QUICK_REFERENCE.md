# 🚀 Quick Reference - Testing Your API

## Before Testing - Make Sure:

```powershell
# 1. gRPC Server running on 9090
cd "D:\SEP 3\LibraryManagementSystem\LogicServer\Server\GrpcService"
dotnet run

# 2. AarhusLogicServer running on 8080
# (Run from IntelliJ or use Maven)
```

---

## Test Order (Do This!)

### 1️⃣ Get All Books
```http
GET http://localhost:8080/books
```
**Purpose:** See what books exist  
**Expected:** 200 OK with JSON array

### 2️⃣ Update Variables
Copy an ISBN from step 1 and update your HTTP file:
```http
@isbn = COPY-ACTUAL-ISBN-HERE
@username = testuser
```

### 3️⃣ Get Single Book
```http
GET http://localhost:8080/books/{{isbn}}
```
**Expected:** 200 OK with single book JSON

### 4️⃣ Create Loan
```http
POST http://localhost:8080/loans
Content-Type: application/json

{
  "bookISBN": "{{isbn}}",
  "username": "{{username}}"
}
```
**Expected:** 201 Created with loan details

---

## Common Errors & Fixes

| Error | Cause | Fix |
|-------|-------|-----|
| `UNIMPLEMENTED` | Old gRPC server | Run `rebuild-and-run-grpc.bat` |
| `405 Method Not Allowed` | Wrong HTTP method | GET for books, POST for loans |
| `404 Not Found` | ISBN doesn't exist | Use actual ISBN from GET /books |
| `Connection Refused` | Server not running | Start the server |
| `Variable not substituted` | Variable not defined | Add `@isbn = ...` at top |

---

## Files I Created For You

1. **`generated-requests.http`** (your scratch file) - FIXED ✅
2. **`test-requests.http`** - Better version with more examples
3. **`TESTING_GUIDE.md`** - Complete testing guide
4. **`rebuild-and-run-grpc.bat`** - Rebuild gRPC server script

---

## Keyboard Shortcuts (IntelliJ)

- `Ctrl+Enter` - Run request at cursor
- `Alt+Enter` - Run request with environment
- Right-click → "Run All" - Run all requests

---

## Quick Diagnostics

```powershell
# Check if servers are running
netstat -ano | findstr "8080 9090"

# Should show BOTH ports
```

---

## Need More Help?

📖 Read: `TESTING_GUIDE.md` - Complete instructions  
🔧 Run: `rebuild-and-run-grpc.bat` - If getting UNIMPLEMENTED  
📝 Use: `test-requests.http` - Better example file

---

**TL;DR:**
1. Run GET /books
2. Copy an ISBN
3. Update @isbn variable
4. Run other requests
5. Done! ✅

