# Fix for 405 Method Not Allowed Error

## What I Fixed

Added **CORS (Cross-Origin Resource Sharing)** configuration to allow your Blazor client to access the API endpoints.

### Changes Made:

1. ✅ **BooksController** - Added `@CrossOrigin` annotation
2. ✅ **LoansController** - Added `@CrossOrigin` annotation  
3. ✅ **WebConfig.java** (NEW) - Global CORS configuration

## Most Common Causes of 405 Error

### 1. Wrong HTTP Method
**Problem**: Using GET when endpoint expects POST (or vice versa)

**Check your request:**
- ✅ `GET /books` - Correct
- ✅ `GET /books/{isbn}` - Correct
- ✅ `POST /loans` - Correct (with JSON body)
- ❌ `GET /loans` - WRONG (should be POST)

**Example of correct requests:**

```bash
# Correct - GET all books
curl http://localhost:8080/books

# Correct - GET single book
curl http://localhost:8080/books/978-0-123456-78-9

# Correct - POST create loan
curl -X POST http://localhost:8080/loans \
  -H "Content-Type: application/json" \
  -d '{"bookISBN":"978-0-123456-78-9","username":"john"}'

# WRONG - This will give 405 error
curl http://localhost:8080/loans  # Missing -X POST
```

### 2. Missing Content-Type Header for POST
**Problem**: POST request without proper headers

**Wrong:**
```bash
curl -X POST http://localhost:8080/loans -d '{"bookISBN":"123"}'
```

**Correct:**
```bash
curl -X POST http://localhost:8080/loans \
  -H "Content-Type: application/json" \
  -d '{"bookISBN":"978-0-123456-78-9","username":"john"}'
```

### 3. Browser Sending OPTIONS Request (CORS Preflight)
**Problem**: Browser sends OPTIONS request first, server rejects it

**Solution**: I've added CORS configuration to allow all origins and methods.

### 4. Trailing Slash Issue
**Problem**: Some frameworks treat `/loans` and `/loans/` differently

**Try both:**
```bash
# Without trailing slash
curl -X POST http://localhost:8080/loans -H "Content-Type: application/json" -d '...'

# With trailing slash
curl -X POST http://localhost:8080/loans/ -H "Content-Type: application/json" -d '...'
```

## How to Test & Debug

### Step 1: Restart the AarhusLogicServer

**Option A: From IntelliJ**
1. Stop the current running server (red stop button)
2. Rebuild the project (Ctrl+F9 or Build → Build Project)
3. Run it again (Shift+F10)

**Option B: From Command Line**
```powershell
# Navigate to the project
cd "D:\SEP 3\LibraryManagementSystem\AarhusLogicServer"

# Stop any running instance (if running as Java process)
# Then rebuild and run
.\mvnw.cmd clean package -DskipTests
java -jar target/AarhusLogicServer-0.0.1-SNAPSHOT.jar
```

### Step 2: Test Each Endpoint

**Test 1: GET all books**
```bash
curl -v http://localhost:8080/books
```

Expected response: `200 OK` with JSON array of books

**Test 2: GET single book**
```bash
curl -v http://localhost:8080/books/978-0-123456-78-9
```

Expected response: `200 OK` with book JSON, or `404 Not Found`

**Test 3: POST create loan**
```bash
curl -v -X POST http://localhost:8080/loans \
  -H "Content-Type: application/json" \
  -d '{"bookISBN":"978-0-123456-78-9","username":"testuser"}'
```

Expected response: `201 Created` with loan details

### Step 3: Check Server Logs

Look for these in the console:
```
Received create loan request for ISBN: 978-0-123456-78-9, Username: testuser
Sending gRPC request to create loan for ISBN: 978-0-123456-78-9
```

## If You're Using Blazor Client

Make sure your Blazor HTTP client is configured correctly:

### Check appsettings.json (or appsettings.Development.json)

```json
{
  "BaseUrl": "http://localhost:8080"
}
```

### Check HttpClient Configuration in Program.cs

```csharp
builder.Services.AddScoped(sp => new HttpClient { 
    BaseAddress = new Uri("http://localhost:8080") 
});
```

### Check Your Service Calls

**Correct way to call getBook:**
```csharp
// In HttpBookService.cs
public async Task<BookDTO> GetBookAsync(string isbn)
{
    HttpResponseMessage httpResponse = await client.GetAsync($"books/{isbn}");
    // ...
}
```

**Correct way to call createLoan:**
```csharp
// In HttpLoanService.cs
public async Task<LoanResponseDTO> CreateLoanAsync(CreateLoanDTO createLoanDTO)
{
    StringContent content = new StringContent(
        JsonSerializer.Serialize(createLoanDTO), 
        System.Text.Encoding.UTF8, 
        "application/json"
    );
    HttpResponseMessage httpResponse = await client.PostAsync("loans", content);
    // ...
}
```

## Common Mistakes & Solutions

| Mistake | Error | Solution |
|---------|-------|----------|
| Using `GET` to create loan | 405 | Use `POST` |
| Missing `Content-Type` header | 405 or 415 | Add `-H "Content-Type: application/json"` |
| Forgetting `-X POST` in curl | 405 | Add `-X POST` |
| Wrong endpoint path | 404 | Check URL spelling |
| Server not running | Connection refused | Start the server |
| gRPC server not running | UNAVAILABLE | Start GrpcService on port 9090 |

## Quick Diagnostic Commands

```powershell
# Check if AarhusLogicServer is running
netstat -ano | findstr :8080

# Check if GrpcService is running  
netstat -ano | findstr :9090

# Test with verbose curl
curl -v -X POST http://localhost:8080/loans \
  -H "Content-Type: application/json" \
  -d '{"bookISBN":"978-0-123456-78-9","username":"test"}'
```

## What to Check in Order

1. ✅ Is AarhusLogicServer running on port 8080?
2. ✅ Is GrpcService running on port 9090?
3. ✅ Are you using the correct HTTP method (GET vs POST)?
4. ✅ For POST requests, do you have `Content-Type: application/json` header?
5. ✅ Is your JSON body properly formatted?
6. ✅ Does the book ISBN exist in the database?

## Files Modified to Fix CORS

- `src/main/java/dk/via/sep3/controller/BooksController.java` - Added `@CrossOrigin`
- `src/main/java/dk/via/sep3/controller/LoansController.java` - Added `@CrossOrigin`
- `src/main/java/dk/via/sep3/controller/WebConfig.java` - NEW global CORS config

## After Making These Changes

**You MUST restart the AarhusLogicServer** for the changes to take effect!

```powershell
# Stop the server
# Then rebuild
cd "D:\SEP 3\LibraryManagementSystem\AarhusLogicServer"
.\mvnw.cmd clean package -DskipTests

# Run it
java -jar target/AarhusLogicServer-0.0.1-SNAPSHOT.jar
```

---

**Most Likely Issue**: You're using the wrong HTTP method. Make sure:
- ✅ Use **GET** for `/books` and `/books/{isbn}`
- ✅ Use **POST** for `/loans` with JSON body

