# 📊 View Books with Loan Information

I've added functionality to show loan information in your console when you request books!

## What I Added

### 1. ✅ Enhanced Console Output
When you call the API endpoints, you'll now see detailed loan information in the **gRPC server console**:

- **Book details** (ID, ISBN, Title, Author, State)
- **Loan status** (Available / On Loan)
- **Borrower information** (who borrowed it)
- **Loan dates** (when borrowed, when due)
- **Days until due** (countdown)
- **Warnings** for overdue or due-soon loans

### 2. ✅ SQL Queries
Created `view_books_with_loans.sql` with 7 useful queries:

1. All books with loan status
2. Only books currently on loan
3. Available books (not on loan)
4. Summary statistics
5. Loans by user
6. Overdue loans (IMPORTANT!)
7. Detailed view with ALL loan history

### 3. ✅ Updated Database Schema
Fixed the Loan table in `kitabkhana_Tables.sql`

---

## How to See Loan Information

### Method 1: Console Output (Automatic)

When you make API requests, the **gRPC server console** will display formatted output:

**Example when calling GET /books:**
```
╔════════════════════════════════════════════════════════════════════════════════════════╗
║                          BOOKS WITH LOAN STATUS                                        ║
╠════════════════════════════════════════════════════════════════════════════════════════╣
║ ID: 1   │ ISBN: 9780132350884      │ Title: Clean Code                     
║ Author: Robert C. Martin  │ State: Available   │ Status: AVAILABLE      
╠────────────────────────────────────────────────────────────────────────────────────────╣
║ ID: 2   │ ISBN: 9780201616224      │ Title: The Pragmatic Programmer       
║ Author: Andrew Hunt       │ State: Borrowed    │ Status: ON LOAN        
║ ➜ Borrowed by: Alex Student (stud.alex)
║ ➜ Loan Date: 2025-11-12 │ Due Date: 2025-11-26 │ Days Until Due: 14
║ ✓ STATUS: ACTIVE
╠────────────────────────────────────────────────────────────────────────────────────────╣
╚════════════════════════════════════════════════════════════════════════════════════════╝
Total Books: 2
```

**Example when calling GET /books/{isbn}:**
```
╔════════════════════════════════════════════════════════════════╗
║                    BOOK DETAILS                                ║
╠════════════════════════════════════════════════════════════════╣
║ ID: 2
║ ISBN: 9780201616224
║ Title: The Pragmatic Programmer
║ Author: Andrew Hunt
║ State: Borrowed
╠════════════════════════════════════════════════════════════════╣
║ LOAN STATUS: ON LOAN                                           ║
║ Borrowed by: Alex Student (stud.alex)
║ Loan Date: 2025-11-12 16:30
║ Due Date: 2025-11-26 16:30
║ Days Until Due: 14
║ ✓ STATUS: ACTIVE                                               ║
╚════════════════════════════════════════════════════════════════╝
```

**Example when creating a loan:**
```
╔════════════════════════════════════════════════════════════════╗
║                    LOAN CREATED                                ║
╠════════════════════════════════════════════════════════════════╣
║ Loan ID: 3
║ Book ID: 1
║ ISBN: 9780132350884
║ User: john.doe
║ Loan Date: 2025-11-12 16:45
║ Due Date: 2025-11-26 16:45
║ Loan Period: 14 days
╚════════════════════════════════════════════════════════════════╝
```

---

### Method 2: Run SQL Queries Directly

Connect to your PostgreSQL database and run the queries in `view_books_with_loans.sql`:

```bash
# Connect to database
psql -U postgres -d your_database_name

# Run the queries file
\i D:/SEP 3/LibraryManagementSystem/KitabKhana_DB/view_books_with_loans.sql
```

**Or run individual queries:**

```sql
-- See all books with loan status
SELECT 
    b.id AS book_id,
    b.ISBN,
    b.title,
    b.author,
    b.state,
    CASE 
        WHEN l.id IS NOT NULL THEN 'ON LOAN'
        ELSE 'AVAILABLE'
    END AS loan_status,
    l.user_id AS borrowed_by,
    u.name AS borrower_name,
    l.loan_date,
    l.due_date
FROM kitabkhana."Book" b
LEFT JOIN kitabkhana."Loan" l ON b.id = l.book_id AND l.status = 'ACTIVE'
LEFT JOIN kitabkhana."User" u ON l.user_id = u.username
ORDER BY b.title;
```

---

## Testing Steps

### Step 1: Rebuild and Restart gRPC Server

**Important!** You need to rebuild for the changes to take effect:

```powershell
cd "D:\SEP 3\LibraryManagementSystem\LogicServer\Server\GrpcService"
dotnet clean
dotnet build
dotnet run
```

Or use the batch file:
```powershell
D:\SEP 3\LibraryManagementSystem\rebuild-and-run-grpc.bat
```

### Step 2: Make API Requests

**Request 1: Get All Books**
```http
GET http://localhost:8080/books
```

**Look at the gRPC server console** - you'll see formatted output with loan information!

**Request 2: Get Single Book**
```http
GET http://localhost:8080/books/9780201616224
```

**Request 3: Create a Loan**
```http
POST http://localhost:8080/loans
Content-Type: application/json

{
  "bookISBN": "9780132350884",
  "username": "john.doe"
}
```

Each request will display formatted loan information in the console!

---

## Warning Indicators

The console output includes visual warnings:

### ⚠️ OVERDUE
Shows when a loan is past its due date:
```
║ ⚠️  WARNING: OVERDUE BY 5 DAYS!
```

### ⚠️ DUE SOON
Shows when a loan is due within 3 days:
```
║ ⚠️  DUE SOON: Only 2 days remaining!
```

### ✓ ACTIVE
Normal active loan:
```
║ ✓ STATUS: ACTIVE
```

---

## SQL Queries Reference

Open `view_books_with_loans.sql` to find:

### Query 1: All Books with Loan Status
Shows every book and whether it's on loan

### Query 2: Only Books Currently on Loan
Filters to show only borrowed books

### Query 3: Books Available for Loan
Shows books NOT currently borrowed

### Query 4: Summary Statistics
Shows totals:
- Total books
- Books on loan
- Books available
- Overdue loans

### Query 5: Loans by User
Shows which users have active loans

### Query 6: Overdue Loans (IMPORTANT!)
Shows all overdue loans with days overdue

### Query 7: Detailed View
Complete history of all loans (active and returned)

---

## Files Created/Modified

### Created:
- **`view_books_with_loans.sql`** - SQL queries to view loan information

### Modified:
- **`DBService.cs`** - Added console logging with loan details
- **`kitabkhana_Tables.sql`** - Fixed Loan table schema
- **`BookServiceImpl.cs`** - Added logging

---

## What You'll See in Console

### Before (Old):
```
Received request to get all books.
```

### After (New):
```
Received request to get all books.
╔════════════════════════════════════════════════════════════════════════════════════════╗
║                          BOOKS WITH LOAN STATUS                                        ║
╠════════════════════════════════════════════════════════════════════════════════════════╣
║ ID: 1   │ ISBN: 9780132350884      │ Title: Clean Code                     
║ Author: Robert C. Martin  │ State: Available   │ Status: AVAILABLE      
╠────────────────────────────────────────────────────────────────────────────────────────╣
║ ID: 2   │ ISBN: 9780201616224      │ Title: The Pragmatic Programmer       
║ Author: Andrew Hunt       │ State: Borrowed    │ Status: ON LOAN        
║ ➜ Borrowed by: Alex Student (stud.alex)
║ ➜ Loan Date: 2025-11-12 │ Due Date: 2025-11-26 │ Days Until Due: 14
╚════════════════════════════════════════════════════════════════════════════════════════╝
Total Books: 2
```

---

## Next Steps

1. ✅ **Rebuild the gRPC server** (use the batch file)
2. ✅ **Start both servers** (gRPC + AarhusLogicServer)
3. ✅ **Make API requests** and watch the console!
4. ✅ **Run SQL queries** to explore loan data in the database

---

**Now when you request books, you'll see complete loan information in your console!** 📊🎉

