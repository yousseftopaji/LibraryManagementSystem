# Implementation Checklist ✓

Use this checklist to verify that all components are properly implemented and working.

## 📋 Pre-Implementation Checklist

- [x] Proto files updated with new message types
- [x] Proto files updated with new RPC methods
- [x] Database schema includes Loan table
- [x] C# DTOs created for Loan
- [x] Java DTOs created for Loan
- [x] gRPC service implementations created
- [x] REST controllers created
- [x] Spring beans registered
- [x] Documentation created

## 🔨 Build Checklist

### C# gRPC Server Build
- [ ] Navigate to GrpcService directory
- [ ] Run `dotnet clean`
- [ ] Run `dotnet build`
- [ ] Build completes without errors
- [ ] Proto files are regenerated (check bin/Debug/net10.0/)

### Java REST API Build
- [ ] Navigate to AarhusLogicServer directory
- [ ] Run `.\mvnw.cmd clean compile`
- [ ] Build completes without errors
- [ ] gRPC classes generated (check target/generated-sources/protobuf/)
- [ ] Verify these classes exist:
  - [ ] `GetBookByIsbnRequest.java`
  - [ ] `GetBookByIsbnResponse.java`
  - [ ] `CreateLoanRequest.java`
  - [ ] `CreateLoanResponse.java`
  - [ ] `DTOLoan.java`
  - [ ] `LoanServiceGrpc.java`

## 🗄️ Database Checklist

- [ ] PostgreSQL is running
- [ ] Database `kitabkhana` schema exists
- [ ] `Book` table exists with data
- [ ] `User` table exists with test user
- [ ] `Loan` table created (NEW!)
- [ ] Run this SQL to verify:
  ```sql
  SELECT table_name FROM information_schema.tables 
  WHERE table_schema = 'kitabkhana' 
  ORDER BY table_name;
  ```
  Expected: Book, BookGenre, Genre, Loan, User

### Sample Data Check
- [ ] At least one book with state 'Available'
- [ ] Test user exists (e.g., 'stud.alex')
- [ ] Run:
  ```sql
  SELECT id, isbn, title, state FROM kitabkhana."Book" WHERE state = 'Available';
  SELECT username FROM kitabkhana."User" WHERE username = 'stud.alex';
  ```

## 🚀 Runtime Checklist

### Start C# gRPC Server
- [ ] Open Terminal/PowerShell window
- [ ] `cd "D:\SEP 3\LibraryManagementSystem\LogicServer\Server\GrpcService"`
- [ ] `dotnet run`
- [ ] Server starts successfully
- [ ] See message: "Now listening on: http://[::]:9090"
- [ ] No errors in console

### Start Java REST API Server
- [ ] Open NEW Terminal/PowerShell window
- [ ] `cd "D:\SEP 3\LibraryManagementSystem\AarhusLogicServer"`
- [ ] `.\mvnw.cmd spring-boot:run`
- [ ] Server starts successfully
- [ ] See message: "Tomcat started on port 8080"
- [ ] See message: "Sending gRPC request..." (connection test)
- [ ] No gRPC connection errors

## 🧪 Testing Checklist

### Test 1: Get All Books (Existing Feature)
- [ ] Request: `GET http://localhost:8080/books`
- [ ] Response status: 200 OK
- [ ] Response contains array of books
- [ ] Each book has: id, title, author, isbn, state

### Test 2: Get Book by ISBN (NEW FEATURE) ✨
- [ ] Request: `GET http://localhost:8080/books/9780132350884`
- [ ] Response status: 200 OK
- [ ] Response contains single book object
- [ ] Book has state "Available"
- [ ] Book details are correct

#### Expected Response:
```json
{
  "id": "1",
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "isbn": "9780132350884",
  "state": "Available"
}
```

### Test 3: Get Book by ISBN - Not Found
- [ ] Request: `GET http://localhost:8080/books/9999999999999`
- [ ] Response status: 404 Not Found
- [ ] This is expected behavior

### Test 4: Create Loan (NEW FEATURE) ✨
- [ ] Request: `POST http://localhost:8080/loans`
- [ ] Headers: `Content-Type: application/json`
- [ ] Body:
  ```json
  {
    "username": "stud.alex",
    "bookId": "1",
    "loanDurationDays": 30
  }
  ```
- [ ] Response status: 201 Created
- [ ] Response contains loan object
- [ ] Loan has: id, borrowDate, dueDate, isReturned, numberOfExtensions, username, bookId
- [ ] borrowDate is today's date
- [ ] dueDate is 30 days from now
- [ ] isReturned is false
- [ ] numberOfExtensions is 0

#### Expected Response:
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

### Test 5: Verify Book State Changed
After creating the loan:
- [ ] Request: `GET http://localhost:8080/books/9780132350884`
- [ ] Book with id "1" now has state "Borrowed"
- [ ] This confirms the transaction worked!

### Test 6: Create Loan - Book Not Available
Try to borrow the same book again:
- [ ] Request: `POST http://localhost:8080/loans` (same body as Test 4)
- [ ] Response status: 400 Bad Request
- [ ] This is expected - book is already borrowed!

### Test 7: Database Verification
- [ ] Open PostgreSQL client
- [ ] Run: `SELECT * FROM kitabkhana."Loan";`
- [ ] See the newly created loan record
- [ ] Run: `SELECT id, title, state FROM kitabkhana."Book" WHERE id = 1;`
- [ ] See state is "Borrowed"

## 📊 Integration Test Checklist

Complete flow test:
1. [ ] Get all books - find available book
2. [ ] Note the book id and isbn
3. [ ] Get book by ISBN - verify it's available
4. [ ] Create loan for that book
5. [ ] Get book by ISBN again - should be borrowed
6. [ ] Verify in database - loan exists, book state updated

## 🔍 Console Logs Checklist

### C# gRPC Server Console Should Show:
- [ ] "Received request to get all books"
- [ ] "Received request to get book by ISBN: ..."
- [ ] "Received request to create loan for user: ..."

### Java REST API Console Should Show:
- [ ] "Sending gRPC request to get all books..."
- [ ] "Sending gRPC request to get book by ISBN: ..."
- [ ] "Sending gRPC request to create loan for user: ..."
- [ ] "Loan created successfully: ..."

## ✅ Final Verification

- [ ] Both servers are running without errors
- [ ] All 7 tests pass successfully
- [ ] Database has correct data
- [ ] No exceptions in console logs
- [ ] Book state transitions work (Available → Borrowed)
- [ ] Loans are created with correct data

## 🎯 Success Criteria

Your implementation is complete when:
1. ✅ All build steps complete without errors
2. ✅ Both servers start and run successfully
3. ✅ All API tests return expected responses
4. ✅ Database transactions work correctly
5. ✅ Book states update properly
6. ✅ No errors in console logs

## 📸 Screenshots (Optional)

Consider taking screenshots of:
- [ ] Successful GET /books/{isbn} response
- [ ] Successful POST /loans response
- [ ] Database showing loan record
- [ ] Book state changed to "Borrowed"

---

## 🆘 If Something Doesn't Work

1. Check the [QUICK_START_GUIDE.md](QUICK_START_GUIDE.md) troubleshooting section
2. Verify all prerequisites are met
3. Check console logs for error messages
4. Ensure database connection is working
5. Confirm both servers are running on correct ports

## 📝 Notes

Write any issues or observations here:

```
[Your notes here]
```

---

**Date Completed:** _______________

**Tested By:** _______________

**All Tests Passed:** [ ] Yes [ ] No

If no, which tests failed: _______________

