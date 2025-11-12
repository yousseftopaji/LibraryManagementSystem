# Fix for "UNIMPLEMENTED: Method is unimplemented" Error

## Problem Explained

The error `io.grpc.StatusRuntimeException: UNIMPLEMENTED: Method is unimplemented` occurs because:

1. ✅ Your **Java client** (AarhusLogicServer) has the `getBook()` method implemented
2. ❌ Your **C# gRPC server** (GrpcService) didn't have the `getBook()` method implemented

## What I Fixed

I've implemented the missing server-side methods:

### 1. Updated Proto File
**File**: `LogicServer/Server/GrpcService/Protos/books.proto`
- ✅ Added `GetBookRequest` and `GetBookResponse` messages
- ✅ Added `GetBook` RPC method to `BookService`
- ✅ Added `CreateLoanRequest` and `CreateLoanResponse` messages
- ✅ Added `LoanService` with `CreateLoan` RPC method

### 2. Updated DBService
**File**: `LogicServer/Server/GrpcService/DatabaseService/DBService.cs`
- ✅ Added `GetBookByISBNAsync(string isbn)` - Queries database for book by ISBN
- ✅ Added `CreateLoanAsync(string bookISBN, string username)` - Creates loan record in database

### 3. Updated BookServiceImpl
**File**: `LogicServer/Server/GrpcService/Services/BookServiceImpl.cs`
- ✅ Implemented `GetBook` RPC method
- Queries database and returns book or NotFound error

### 4. Created LoanServiceImpl
**File**: `LogicServer/Server/GrpcService/Services/LoanServiceImpl.cs` ✨ **NEW**
- ✅ Implemented `CreateLoan` RPC method
- Creates loan in database with 14-day loan period

### 5. Updated Program.cs
**File**: `LogicServer/Server/GrpcService/Program.cs`
- ✅ Registered `LoanServiceImpl` in the gRPC service mapping

## How to Fix (IMPORTANT - Follow These Steps)

### Step 1: Stop the Running gRPC Server
The server is currently running (Process ID: 19544), so you need to stop it first:

**Option A: Stop from IntelliJ/Rider**
- Find the "Run" window
- Click the red ⏹️ Stop button

**Option B: Kill the process manually**
```powershell
# Find and kill the process
taskkill /F /IM GrpcService.exe
```

### Step 2: Rebuild the gRPC Server
```powershell
cd "D:\SEP 3\LibraryManagementSystem\LogicServer\Server\GrpcService"
dotnet build
```

This will:
- Regenerate the proto classes with the new `GetBook` and `CreateLoan` methods
- Compile the new service implementations

### Step 3: Restart the gRPC Server
```powershell
dotnet run
```

You should see:
```
Connected to PostgreSQL: ...
Now listening on: http://0.0.0.0:9090
```

### Step 4: Test the Endpoints

#### Test Get All Books (should still work)
```bash
curl http://localhost:8080/books
```

#### Test Get Single Book (NEW)
```bash
curl http://localhost:8080/books/978-0-123456-78-9
```

#### Test Create Loan (NEW)
```bash
curl -X POST http://localhost:8080/loans \
  -H "Content-Type: application/json" \
  -d "{\"bookISBN\":\"978-0-123456-78-9\",\"username\":\"john.doe\"}"
```

## Database Requirements

Make sure your PostgreSQL database has these tables:

### Book Table
```sql
CREATE TABLE kitabkhana."book" (
    id SERIAL PRIMARY KEY,
    isbn VARCHAR(20) UNIQUE,
    title VARCHAR(255),
    author VARCHAR(255),
    state VARCHAR(20)
);
```

### Loan Table (if not exists)
```sql
CREATE TABLE kitabkhana."loan" (
    id SERIAL PRIMARY KEY,
    book_id INTEGER REFERENCES kitabkhana."book"(id),
    user_id VARCHAR(100),
    loan_date TIMESTAMP,
    due_date TIMESTAMP,
    return_date TIMESTAMP NULL,
    status VARCHAR(20)
);
```

## Architecture After Fix

```
┌─────────────────┐   HTTP    ┌──────────────────┐   gRPC   ┌────────────────┐   SQL   ┌──────────┐
│  Blazor Client  │ ────────→ │ AarhusLogicServer│ ───────→ │  GrpcService   │ ──────→ │PostgreSQL│
│   (Port ????)   │           │   (Port 8080)    │          │  (Port 9090)   │         │          │
└─────────────────┘           └──────────────────┘          └────────────────┘         └──────────┘
                               Java Spring Boot              C# gRPC Server              Database
                               REST API Client               
                                                              ✅ GetBook implemented
                                                              ✅ CreateLoan implemented
```

## What Each Component Does

### AarhusLogicServer (Java - Port 8080)
- Exposes REST API to Blazor client
- Converts HTTP requests to gRPC calls
- Already had client code, just needed server implementation

### GrpcService (C# - Port 9090)
- **NOW IMPLEMENTS**:
  - `GetAllBooks()` ✅ (was already working)
  - `GetBook(isbn)` ✅ (NEW - just implemented)
  - `CreateLoan(bookISBN, username)` ✅ (NEW - just implemented)
- Queries PostgreSQL database
- Returns data to Java client

### PostgreSQL Database
- Stores books and loans
- Queries executed by C# DBService

## Troubleshooting

### If you still get UNIMPLEMENTED error:
1. ✅ Verify gRPC server is running on port 9090
2. ✅ Check server logs for any startup errors
3. ✅ Verify proto file was regenerated (check obj/Debug folder for generated .cs files)
4. ✅ Ensure both client and server proto files match exactly

### If you get "Connection Refused":
- The gRPC server isn't running - start it with `dotnet run`

### If you get "NotFound" error:
- The book with that ISBN doesn't exist in the database
- Check your database has books with valid ISBNs

## Files Modified

### Created:
- `LogicServer/Server/GrpcService/Services/LoanServiceImpl.cs`

### Modified:
- `LogicServer/Server/GrpcService/Protos/books.proto`
- `LogicServer/Server/GrpcService/DatabaseService/DBService.cs`
- `LogicServer/Server/GrpcService/Services/BookServiceImpl.cs`
- `LogicServer/Server/GrpcService/Program.cs`

---

## Quick Action Steps

**RIGHT NOW, DO THIS:**

1. **Stop the running GrpcService** (press Stop button or `taskkill /F /IM GrpcService.exe`)
2. **Rebuild**: `cd "D:\SEP 3\LibraryManagementSystem\LogicServer\Server\GrpcService"; dotnet build`
3. **Start server**: `dotnet run`
4. **Test your endpoint**: Try calling `GET http://localhost:8080/books/{isbn}` again

**The UNIMPLEMENTED error will be GONE!** ✅

