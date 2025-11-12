# 🎯 QUICK START - See Books with Loans

## In 3 Simple Steps:

### 1️⃣ Rebuild gRPC Server
```powershell
D:\SEP 3\LibraryManagementSystem\rebuild-and-run-grpc.bat
```
Wait for: `Now listening on: http://0.0.0.0:9090`

### 2️⃣ Run This Request
In your HTTP file, click the ▶️ button next to:
```http
GET http://localhost:8080/books
```

### 3️⃣ Look at Console!
Check the **gRPC server console window** - you'll see:

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
```

---

## What Shows in Console:

✅ All books with their details  
✅ Loan status (Available / On Loan)  
✅ Who borrowed it  
✅ When borrowed & when due  
✅ Days remaining  
✅ ⚠️ Warnings for overdue books

---

## Your HTTP File is Ready!

I updated it with actual ISBNs from your database:
- `9780132350884` - Clean Code (Available)
- `9780201616224` - The Pragmatic Programmer (On Loan to Alex)

Just run the requests and watch the console!

---

## Files to Reference:

📖 **VIEW_LOANS_GUIDE.md** - Complete guide  
📊 **view_books_with_loans.sql** - SQL queries  
🚀 **rebuild-and-run-grpc.bat** - Rebuild script

---

**That's it! Rebuild → Request → Watch Console!** 🎉

