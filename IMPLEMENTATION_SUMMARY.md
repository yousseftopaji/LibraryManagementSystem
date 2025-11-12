# Implementation Summary: Get Book by ISBN & Create Loan

## What Has Been Implemented

### 1. Proto Files Updated (Both Java & C#)
- Added `DTOLoan` message with fields: id, borrowDate, dueDate, isReturned, numberOfExtensions, username, bookId
- Added `GetBookByIsbnRequest` and `GetBookByIsbnResponse` messages
- Added `CreateLoanRequest` and `CreateLoanResponse` messages
- Added `getBookByIsbn` RPC method to BookService
- Created new `LoanService` with `createLoan` RPC method

**Files:**
- `D:\SEP 3\LibraryManagementSystem\AarhusLogicServer\src\main\proto\books.proto`
- `D:\SEP 3\LibraryManagementSystem\LogicServer\Server\GrpcService\Protos\books.proto`

### 2. Database Layer (C#)
- Created `LoanDTO.cs` for database operations
- Updated `DBService.cs` with:
  - `GetBookByIsbnAsync(string isbn)` - Gets an available book by ISBN
  - `CreateLoanAsync(string username, string bookId, int loanDurationDays)` - Creates a loan with transaction support

**Files:**
- `D:\SEP 3\LibraryManagementSystem\LogicServer\Shared\DTOs\Loan\LoanDTO.cs`
- `D:\SEP 3\LibraryManagementSystem\LogicServer\Server\GrpcService\DatabaseService\DBService.cs`

### 3. gRPC Service Layer (C#)
- Updated `BookServiceImpl.cs` with `GetBookByIsbn` implementation
- Created `LoanServiceImpl.cs` with `CreateLoan` implementation
- Registered `LoanServiceImpl` in `Program.cs`

**Files:**
- `D:\SEP 3\LibraryManagementSystem\LogicServer\Server\GrpcService\Services\BookServiceImpl.cs`
- `D:\SEP 3\LibraryManagementSystem\LogicServer\Server\GrpcService\Services\LoanServiceImpl.cs`
- `D:\SEP 3\LibraryManagementSystem\LogicServer\Server\GrpcService\Program.cs`

### 4. Java Client Layer
- Updated `GrpcConnectionInterface` with new methods
- Updated `GrpcConnection` implementation with:
  - `getBookByIsbn(String isbn)`
  - `createLoan(String username, String bookId, int loanDurationDays)`
- Created `LoanService` interface and `LoanServiceImpl`
- Created `LoanDTO.java` and `CreateLoanRequest.java`
- Updated `Model.java` to implement `getBookByIsbn`

**Files:**
- `D:\SEP 3\LibraryManagementSystem\AarhusLogicServer\src\main\java\dk\via\sep3\grpcConnection\GrpcConnection.java`
- `D:\SEP 3\LibraryManagementSystem\AarhusLogicServer\src\main\java\dk\via\sep3\grpcConnection\GrpcConnectionInterface.java`
- `D:\SEP 3\LibraryManagementSystem\AarhusLogicServer\src\main\java\dk\via\sep3\model\LoanService.java`
- `D:\SEP 3\LibraryManagementSystem\AarhusLogicServer\src\main\java\dk\via\sep3\model\LoanServiceImpl.java`
- `D:\SEP 3\LibraryManagementSystem\AarhusLogicServer\src\main\java\dk\via\sep3\model\entities\LoanDTO.java`
- `D:\SEP 3\LibraryManagementSystem\AarhusLogicServer\src\main\java\dk\via\sep3\model\entities\CreateLoanRequest.java`

### 5. REST API Controllers (Java Spring Boot)
- Updated `BooksController` with `GET /books/{isbn}` endpoint
- Created `LoansController` with `POST /loans` endpoint
- Registered beans in `AarhusLogicServer.java`

**Files:**
- `D:\SEP 3\LibraryManagementSystem\AarhusLogicServer\src\main\java\dk\via\sep3\controller\BooksController.java`
- `D:\SEP 3\LibraryManagementSystem\AarhusLogicServer\src\main\java\dk\via\sep3\controller\LoansController.java`
- `D:\SEP 3\LibraryManagementSystem\AarhusLogicServer\src\main\java\dk\via\sep3\controller\AarhusLogicServer.java`

### 6. Database Schema
- Uncommented and updated `Loan` table in SQL script
- Changed foreign key from `bookCopyId` to `bookId` to match your current schema

**File:**
- `D:\SEP 3\LibraryManagementSystem\KitabKhana_DB\kitabkhana_Tables.sql`

### 7. Test HTTP Requests
Created test requests file with examples for:
- GET all books
- GET book by ISBN
- POST create loan

**File:**
- `D:\SEP 3\LibraryManagementSystem\AarhusLogicServer\test-requests.http`

## Next Steps to Complete

### Step 1: Build the C# gRPC Server
Stop the running GrpcService if it's running, then:
```powershell
cd "D:\SEP 3\LibraryManagementSystem\LogicServer\Server\GrpcService"
dotnet build
dotnet run
```

### Step 2: Build the Java Server
```powershell
cd "D:\SEP 3\LibraryManagementSystem\AarhusLogicServer"
.\mvnw.cmd clean compile
.\mvnw.cmd spring-boot:run
```

### Step 3: Update Database
Run the updated SQL script to create the Loan table:
```sql
-- Execute the script in your PostgreSQL database
D:\SEP 3\LibraryManagementSystem\KitabKhana_DB\kitabkhana_Tables.sql
```

### Step 4: Test the APIs

**Get Book by ISBN:**
```
GET http://localhost:8080/books/9780132350884
```

**Create a Loan:**
```
POST http://localhost:8080/loans
Content-Type: application/json

{
  "username": "stud.alex",
  "bookId": "1",
  "loanDurationDays": 30
}
```

## Key Features Implemented

1. **Get Book by ISBN** - Returns an available book matching the ISBN
2. **Create Loan** - Creates a new loan and updates book state to "Borrowed"
3. **Transaction Support** - Database transaction ensures atomicity
4. **Error Handling** - Proper error responses when book is not available
5. **Complete End-to-End** - From database → gRPC → REST API

## Architecture Flow

```
HTTP Request (GET /books/{isbn})
  ↓
BooksController
  ↓
BookList (Model)
  ↓
GrpcConnection
  ↓
gRPC (BookService.GetBookByIsbn)
  ↓
BookServiceImpl (C#)
  ↓
DBService.GetBookByIsbnAsync
  ↓
PostgreSQL Database
```

```
HTTP Request (POST /loans)
  ↓
LoansController
  ↓
LoanService (LoanServiceImpl)
  ↓
GrpcConnection
  ↓
gRPC (LoanService.CreateLoan)
  ↓
LoanServiceImpl (C#)
  ↓
DBService.CreateLoanAsync (with transaction)
  ↓
PostgreSQL Database (Insert Loan + Update Book State)
```

