# LibraryManagementSystem

A distributed library management system built with .NET, Java Spring Boot, gRPC, and PostgreSQL.

## 🏗️ Architecture

```
┌─────────────────┐      ┌──────────────────┐      ┌─────────────────┐
│   Blazor App    │      │  Java REST API   │      │  C# gRPC Server │
│  (Client UI)    │─────▶│  (Port 8080)     │─────▶│  (Port 9090)    │
└─────────────────┘      └──────────────────┘      └─────────────────┘
                                                             │
                                                             ▼
                                                    ┌─────────────────┐
                                                    │   PostgreSQL    │
                                                    │    Database     │
                                                    └─────────────────┘
```

## 📚 Features

### Books Management
- ✅ Get all books
- ✅ Get book by ISBN (filters for available books)
- Create/Update/Delete books

### Loans Management
- ✅ Create a loan (borrows a book)
  - Validates book availability
  - Updates book state to "Borrowed"
  - Creates loan record with due date
  - Atomic transaction support
- View active loans
- Return books
- Extend loan period

### Users Management
- User authentication
- User registration
- Role-based access (Librarian/Student)

## 🚀 Quick Start

### Prerequisites
- .NET 10.0 SDK
- Java 17 or higher
- PostgreSQL 13+
- Maven or use included Maven wrapper

### 1. Database Setup

Create the database and run the schema:
```sql
-- Connect to PostgreSQL
psql -U postgres

-- Run the schema
\i 'D:/SEP 3/LibraryManagementSystem/KitabKhana_DB/kitabkhana_Tables.sql'
```

### 2. Configure Connection String

Update `appsettings.json` in the GrpcService project:
```json
{
  "ConnectionStrings": {
    "LibraryDb": "Host=localhost;Database=your_db;Username=postgres;Password=your_password"
  }
}
```

### 3. Build and Run

#### Option A: Use the Build Script (Recommended)
```powershell
& "D:\SEP 3\LibraryManagementSystem\build-and-run.ps1"
```

#### Option B: Manual Build

**Terminal 1 - Start C# gRPC Server:**
```powershell
cd "D:\SEP 3\LibraryManagementSystem\LogicServer\Server\GrpcService"
dotnet run
```

**Terminal 2 - Start Java REST API Server:**
```powershell
cd "D:\SEP 3\LibraryManagementSystem\AarhusLogicServer"
.\mvnw.cmd spring-boot:run
```

## 📖 API Documentation

### REST API Endpoints

Base URL: `http://localhost:8080`

#### Books

**Get All Books**
```http
GET /books
```

**Get Book by ISBN** ✨ NEW
```http
GET /books/{isbn}

Example:
GET /books/9780132350884
```

Response:
```json
{
  "id": "1",
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "isbn": "9780132350884",
  "state": "Available"
}
```

#### Loans

**Create a Loan** ✨ NEW
```http
POST /loans
Content-Type: application/json

{
  "username": "stud.alex",
  "bookId": "1",
  "loanDurationDays": 30
}
```

Response:
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

## 🧪 Testing

Use the included HTTP request file:
```
D:\SEP 3\LibraryManagementSystem\AarhusLogicServer\test-requests.http
```

Or use tools like:
- IntelliJ HTTP Client
- Postman
- curl
- Thunder Client (VS Code)

Example with curl:
```bash
# Get book by ISBN
curl http://localhost:8080/books/9780132350884

# Create a loan
curl -X POST http://localhost:8080/loans \
  -H "Content-Type: application/json" \
  -d '{"username":"stud.alex","bookId":"1","loanDurationDays":30}'
```

## 📁 Project Structure

```
LibraryManagementSystem/
├── AarhusLogicServer/          # Java Spring Boot REST API
│   ├── src/main/
│   │   ├── java/
│   │   │   └── dk/via/sep3/
│   │   │       ├── controller/    # REST Controllers
│   │   │       ├── model/         # Business Logic
│   │   │       └── grpcConnection/ # gRPC Client
│   │   └── proto/                 # Proto definitions
│   └── test-requests.http
├── LogicServer/                # C# gRPC Server
│   └── Server/
│       └── GrpcService/
│           ├── Services/       # gRPC Service Implementations
│           ├── DatabaseService/# Database Layer
│           └── Protos/         # Proto definitions
├── Client/                     # Blazor Frontend
│   └── BlazorApp/
├── KitabKhana_DB/             # Database Schema
│   └── kitabkhana_Tables.sql
├── IMPLEMENTATION_SUMMARY.md   # Detailed implementation notes
├── QUICK_START_GUIDE.md       # Step-by-step guide
└── build-and-run.ps1          # Build automation script
```

## 🔧 Technologies Used

- **Backend (C#)**: .NET 10.0, gRPC, Npgsql
- **Backend (Java)**: Spring Boot 3.5, gRPC, Maven
- **Frontend**: Blazor WebAssembly
- **Database**: PostgreSQL 13+
- **Communication**: gRPC (HTTP/2)
- **API**: REST (JSON)

## 📝 Development Notes

### Recent Changes (Nov 12, 2025)

✨ **New Features:**
1. **Get Book by ISBN** - Retrieves an available book by ISBN
2. **Create Loan** - Borrows a book and updates its state

🔧 **Technical Implementation:**
- Added proto definitions for `GetBookByIsbn` and `CreateLoan` RPCs
- Implemented database transaction support for loan creation
- Created new DTOs: `LoanDTO`, `CreateLoanRequest`
- Added REST endpoints: `GET /books/{isbn}`, `POST /loans`
- Updated database schema with `Loan` table

See [IMPLEMENTATION_SUMMARY.md](IMPLEMENTATION_SUMMARY.md) for full details.

## 🐛 Troubleshooting

See [QUICK_START_GUIDE.md](QUICK_START_GUIDE.md) for common issues and solutions.

### Common Issues:

**gRPC connection refused:**
- Ensure C# gRPC server is running on port 9090
- Check firewall settings

**Proto classes not found:**
- Run `mvn clean compile` to regenerate gRPC classes

**Database connection failed:**
- Verify PostgreSQL is running
- Check connection string in appsettings.json

## 📄 License

This project is for educational purposes (SEP3 Course).

## 👥 Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for contribution guidelines.

---

For detailed testing instructions, see [QUICK_START_GUIDE.md](QUICK_START_GUIDE.md)

