# Quick Start Guide - Testing the New Endpoints

## Prerequisites
1. PostgreSQL database is running with the `kitabkhana` schema
2. C# gRPC Server (LogicServer) is running on port 9090
3. Java REST API Server (AarhusLogicServer) is running on port 8080

## Step-by-Step Setup

### 1. Update the Database Schema

First, ensure your database has the Loan table. Run this SQL:

```sql
-- Connect to your PostgreSQL database and run:
CREATE TABLE IF NOT EXISTS kitabkhana."Loan" (
    id SERIAL PRIMARY KEY,
    borrowDate DATE NOT NULL DEFAULT CURRENT_DATE,
    dueDate DATE NOT NULL,
    isReturned BOOLEAN NOT NULL DEFAULT FALSE,
    numberOfExtensions INT NOT NULL DEFAULT 0 CHECK (numberOfExtensions >= 0),
    username VARCHAR(100) NOT NULL,
    bookId INT NOT NULL,
    FOREIGN KEY (username) REFERENCES kitabkhana."User"(username) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (bookId) REFERENCES kitabkhana."Book"(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CHECK (dueDate >= borrowDate)
);
```

### 2. Stop and Rebuild C# gRPC Server

**Stop the running GrpcService** (if it's running)

Then rebuild:
```powershell
cd "D:\SEP 3\LibraryManagementSystem\LogicServer\Server\GrpcService"
dotnet clean
dotnet build
```

If successful, run:
```powershell
dotnet run
```

You should see:
```
info: Microsoft.Hosting.Lifetime[14]
      Now listening on: http://[::]:9090
```

### 3. Build and Run Java Server

In a new terminal:
```powershell
cd "D:\SEP 3\LibraryManagementSystem\AarhusLogicServer"
.\mvnw.cmd clean compile
.\mvnw.cmd spring-boot:run
```

You should see:
```
Tomcat started on port 8080
```

### 4. Test the Endpoints

You can use the provided `test-requests.http` file or test manually:

#### Test 1: Get All Books
```http
GET http://localhost:8080/books
Accept: application/json
```

Expected response: List of all books

#### Test 2: Get Book by ISBN (New!)
```http
GET http://localhost:8080/books/9780132350884
Accept: application/json
```

Expected response:
```json
{
  "id": "1",
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "isbn": "9780132350884",
  "state": "Available"
}
```

#### Test 3: Create a Loan (New!)
```http
POST http://localhost:8080/loans
Content-Type: application/json

{
  "username": "stud.alex",
  "bookId": "1",
  "loanDurationDays": 30
}
```

Expected response:
```json
{
  "id": "1",
  "borrowDate": "2025-11-12",
  "dueDate": "2025-12-12",
  "isReturned": false,
  "numberOfExtensions": 0,
  "username": "stud.alex",
  "bookId": "1"
}
```

After creating a loan, if you check the book again:
```http
GET http://localhost:8080/books/9780132350884
```

The book with id "1" should now have state: "Borrowed"

## Troubleshooting

### Issue: gRPC proto classes not found
**Solution:** Make sure to run `mvn clean compile` which will regenerate the gRPC classes from the proto files.

### Issue: C# build fails with "file is locked"
**Solution:** Stop the running GrpcService process first, then rebuild.

### Issue: Book not found when creating loan
**Solution:** Make sure you're using the correct bookId. Check available books first:
```http
GET http://localhost:8080/books
```

### Issue: Foreign key constraint error
**Solution:** Make sure the username exists in the User table:
```sql
INSERT INTO kitabkhana."User"(username, password, name, phoneNumber, isLibrarian) 
VALUES ('stud.alex', 'password123', 'Alex Student', '+45-2222', FALSE)
ON CONFLICT DO NOTHING;
```

### Issue: Book is not available
**Solution:** The book might already be borrowed. Check the book state or use a different book with state = 'Available'.

## Database Queries for Verification

### Check all loans:
```sql
SELECT * FROM kitabkhana."Loan";
```

### Check book states:
```sql
SELECT id, title, isbn, state FROM kitabkhana."Book";
```

### Check which books are borrowed:
```sql
SELECT b.id, b.title, b.isbn, b.state, l.username, l.borrowDate, l.dueDate
FROM kitabkhana."Book" b
LEFT JOIN kitabkhana."Loan" l ON b.id = l.bookId AND l.isReturned = FALSE;
```

## What Happens Behind the Scenes

### When you GET /books/{isbn}:
1. REST Controller receives HTTP request
2. Calls Model layer
3. gRPC client sends request to C# server
4. C# server queries PostgreSQL for available book with that ISBN
5. Returns first available book found
6. Response travels back through the layers

### When you POST /loans:
1. REST Controller receives HTTP request with JSON body
2. Calls LoanService
3. gRPC client sends CreateLoan request to C# server
4. C# server starts a database transaction:
   - Checks if book is available
   - Creates new loan record
   - Updates book state to "Borrowed"
   - Commits transaction (or rolls back if any step fails)
5. Returns loan details
6. Response travels back through the layers

## Next Steps

After testing these endpoints, you can extend the functionality:
- Return a book (update loan.isReturned = true, book.state = 'Available')
- Extend a loan (increment numberOfExtensions, update dueDate)
- Reserve a book
- Get all loans for a user
- Get loan history

Happy testing! 🚀

