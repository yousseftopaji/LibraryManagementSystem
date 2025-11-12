# How to Test Your API Endpoints

## Step 1: First, Get All Books to See What's Available

```http
GET http://localhost:8080/books
```

**Expected Response (200 OK):**
```json
[
  {
    "id": "1",
    "isbn": "978-0-134685991",
    "title": "Effective Java",
    "author": "Joshua Bloch",
    "state": "AVAILABLE"
  },
  {
    "id": "2",
    "isbn": "978-0-596009205",
    "title": "Head First Design Patterns",
    "author": "Eric Freeman",
    "state": "AVAILABLE"
  }
]
```

**Copy one of the ISBNs** from the response to use in the next tests.

---

## Step 2: Get a Single Book by ISBN

Replace `YOUR-ACTUAL-ISBN` with an ISBN from Step 1:

```http
GET http://localhost:8080/books/YOUR-ACTUAL-ISBN
```

**Example:**
```http
GET http://localhost:8080/books/978-0-134685991
```

**Expected Response (200 OK):**
```json
{
  "id": "1",
  "isbn": "978-0-134685991",
  "title": "Effective Java",
  "author": "Joshua Bloch",
  "state": "AVAILABLE"
}
```

**If you get 404 Not Found:**
- The ISBN doesn't exist in the database
- Check spelling - ISBNs are case-sensitive
- Make sure you're using an ISBN from Step 1

---

## Step 3: Create a Loan

Use an ISBN from Step 1:

```http
POST http://localhost:8080/loans
Content-Type: application/json

{
  "bookISBN": "978-0-134685991",
  "username": "john.doe"
}
```

**Expected Response (201 Created):**
```json
{
  "loanId": "1",
  "bookId": "1",
  "isbn": "978-0-134685991",
  "userId": "john.doe",
  "loanDate": "2025-11-12T16:00:00Z",
  "dueDate": "2025-11-26T16:00:00Z"
}
```

---

## Using IntelliJ HTTP Client

### Method 1: Use Variables

1. Open `test-requests.http` (I created this for you)
2. Run the first request "Get All Books"
3. Copy an actual ISBN from the response
4. Update the `@isbn` variable at the top:
   ```http
   @isbn = 978-0-134685991
   ```
5. Now you can run the other requests using `{{isbn}}`

### Method 2: Run Requests Directly

1. Click the green ▶️ play button next to each request
2. Or use `Ctrl+Enter` while cursor is in the request
3. The response will appear in a panel on the right

### Method 3: Use the Services Tool Window

1. Open Services tool window (View → Tool Windows → Services)
2. Find "HTTP Client"
3. See history of all requests and responses

---

## Common Errors and Solutions

### Error: 405 Method Not Allowed

**Problem:** Using wrong HTTP method

**Solution:**
- For `/books` → Use **GET**
- For `/books/{isbn}` → Use **GET**
- For `/loans` → Use **POST** with JSON body

```http
# WRONG ❌
GET http://localhost:8080/loans

# CORRECT ✅
POST http://localhost:8080/loans
Content-Type: application/json

{...}
```

---

### Error: 404 Not Found

**Problem:** Book doesn't exist or wrong ISBN

**Solution:**
1. First run `GET /books` to see what books exist
2. Copy an exact ISBN from the response
3. Use that ISBN in your request

---

### Error: 400 Bad Request

**Problem:** Invalid JSON or missing required fields

**Solution:** Check your JSON format:
```json
{
  "bookISBN": "978-0-134685991",  // Required
  "username": "john.doe"           // Required
}
```

**Common mistakes:**
- Missing quotes around values
- Missing comma between fields
- Typo in field names (case-sensitive!)

---

### Error: UNIMPLEMENTED

**Problem:** gRPC server not rebuilt/restarted

**Solution:**
1. Run `rebuild-and-run-grpc.bat` from project root
2. Or manually rebuild gRPC server:
   ```powershell
   cd "D:\SEP 3\LibraryManagementSystem\LogicServer\Server\GrpcService"
   dotnet clean
   dotnet build
   dotnet run
   ```

---

### Error: Connection Refused

**Problem:** Server not running

**Solution:**
1. Check AarhusLogicServer is running on port 8080
2. Check GrpcService is running on port 9090

```powershell
# Check ports
netstat -ano | findstr :8080
netstat -ano | findstr :9090
```

---

## Testing Workflow

### Full Test Sequence:

1. **Start gRPC Server** (if not running):
   ```powershell
   cd "D:\SEP 3\LibraryManagementSystem\LogicServer\Server\GrpcService"
   dotnet run
   ```

2. **Start AarhusLogicServer** (if not running):
   - Run from IntelliJ: Click Run button
   - Or: `./mvnw spring-boot:run`

3. **Test in this order**:
   ```
   ✅ GET /books (should always work if servers are running)
   ✅ GET /books/{isbn} (use ISBN from step above)
   ✅ POST /loans (use ISBN from step above)
   ```

---

## Sample Test Data (If Your DB is Empty)

If you don't have books in your database, add some:

```sql
-- Connect to PostgreSQL
psql -U postgres -d your_database

-- Add test books
INSERT INTO kitabkhana."book" (isbn, title, author, state) VALUES
('978-0-134685991', 'Effective Java', 'Joshua Bloch', 'AVAILABLE'),
('978-0-596009205', 'Head First Design Patterns', 'Eric Freeman', 'AVAILABLE'),
('978-0-132350884', 'Clean Code', 'Robert Martin', 'AVAILABLE');

-- Verify
SELECT * FROM kitabkhana."book";
```

---

## Quick Test Commands (PowerShell)

```powershell
# Test 1: Get all books
Invoke-WebRequest -Uri "http://localhost:8080/books" -Method GET

# Test 2: Get single book
Invoke-WebRequest -Uri "http://localhost:8080/books/978-0-134685991" -Method GET

# Test 3: Create loan
$body = @{
    bookISBN = "978-0-134685991"
    username = "john.doe"
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:8080/loans" -Method POST `
    -ContentType "application/json" -Body $body
```

---

## Files for Testing

I created these files to help you:

1. **`test-requests.http`** - IntelliJ HTTP Client file with all requests
2. **`generated-requests.http`** - Your scratch file (now fixed with variables)

**Use `test-requests.http`** - it has better examples and documentation!

---

## Next Steps

1. ✅ Make sure both servers are running
2. ✅ Run `GET /books` to see what's in the database
3. ✅ Copy an actual ISBN from the response
4. ✅ Update the `@isbn` variable in the HTTP file
5. ✅ Test `GET /books/{isbn}` with the real ISBN
6. ✅ Test `POST /loans` with the real ISBN

Good luck! 🚀

