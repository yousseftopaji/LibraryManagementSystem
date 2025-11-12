using Npgsql;
using DTOs;
using DTOs.Loan;

namespace GrpcService.DatabaseService;

public class DBService
{
    private readonly string? connectionString;

    public DBService(IConfiguration configuration)
    {
        connectionString = configuration.GetConnectionString("LibraryDb");
    }

    public async Task<string> TestConnectionAsync()
    {
        await using var connection = new NpgsqlConnection(connectionString);
        await connection.OpenAsync();

        await using var cmd = new NpgsqlCommand("Select Version()", connection);
        var version = await cmd.ExecuteScalarAsync();
        return version?.ToString() ?? "Unknown Postgres Version";
    }

    public async Task<List<BookDTO>> GetAllBooksAsync()
    {
        var books = new List<BookDTO>();

        await using var conn = new NpgsqlConnection(connectionString);
        await conn.OpenAsync();

        // Query books with loan information
        const string query = @"
            SELECT
                b.id,
                b.isbn,
                b.author,
                b.title,
                b.state,
                l.id AS loan_id,
                l.user_id,
                u.name AS borrower_name,
                l.loan_date,
                l.due_date,
                l.status
            FROM kitabkhana.""Book"" b
            LEFT JOIN kitabkhana.""Loan"" l ON b.id = l.book_id AND l.status = 'ACTIVE'
            LEFT JOIN kitabkhana.""User"" u ON l.user_id = u.username
            ORDER BY b.title";

        await using var cmd = new NpgsqlCommand(query, conn);
        await using var reader = await cmd.ExecuteReaderAsync();

        Console.WriteLine("\n╔════════════════════════════════════════════════════════════════════════════════════════╗");
        Console.WriteLine("║                          BOOKS WITH LOAN STATUS                                        ║");
        Console.WriteLine("╠════════════════════════════════════════════════════════════════════════════════════════╣");

        while (await reader.ReadAsync())
        {
            var bookId = reader.GetInt32(0).ToString();
            var isbn = reader.IsDBNull(1) ? null : reader.GetString(1);
            var author = reader.IsDBNull(2) ? null : reader.GetString(2);
            var title = reader.IsDBNull(3) ? null : reader.GetString(3);
            var state = reader.GetString(4);

            var hasLoan = !reader.IsDBNull(5);
            var loanStatus = hasLoan ? "ON LOAN" : "AVAILABLE";

            Console.WriteLine($"║ ID: {bookId,-3} │ ISBN: {isbn,-18} │ Title: {title,-30}");
            Console.WriteLine($"║ Author: {author,-20} │ State: {state,-10} │ Status: {loanStatus,-12}");

            if (hasLoan)
            {
                var borrowerId = reader.GetString(6);
                var borrowerName = reader.IsDBNull(7) ? "Unknown" : reader.GetString(7);
                var loanDate = reader.GetDateTime(8);
                var dueDate = reader.GetDateTime(9);
                var daysUntilDue = (dueDate - DateTime.UtcNow).Days;

                Console.WriteLine($"║ ➜ Borrowed by: {borrowerName} ({borrowerId})");
                Console.WriteLine($"║ ➜ Loan Date: {loanDate:yyyy-MM-dd} │ Due Date: {dueDate:yyyy-MM-dd} │ Days Until Due: {daysUntilDue}");

                if (daysUntilDue < 0)
                {
                    Console.WriteLine($"║ ⚠️  WARNING: OVERDUE BY {Math.Abs(daysUntilDue)} DAYS!");
                }
                else if (daysUntilDue <= 3)
                {
                    Console.WriteLine($"║ ⚠️  DUE SOON: Only {daysUntilDue} days remaining!");
                }
            }

            Console.WriteLine("╠────────────────────────────────────────────────────────────────────────────────────────╣");

            books.Add(new BookDTO
            {
                BookId = bookId,
                ISBN = isbn,
                Author = author,
                Title = title,
                State = state
            });
        }

        Console.WriteLine("╚════════════════════════════════════════════════════════════════════════════════════════╝");
        Console.WriteLine($"Total Books: {books.Count}\n");

        return books;
    }

    public async Task<BookDTO?> GetBookByISBNAsync(string isbn)
    {
        await using var conn = new NpgsqlConnection(connectionString);
        await conn.OpenAsync();

        const string query = @"
            SELECT
                b.id,
                b.isbn,
                b.author,
                b.title,
                b.state,
                l.id AS loan_id,
                l.user_id,
                u.name AS borrower_name,
                l.loan_date,
                l.due_date,
                l.status
            FROM kitabkhana.""Book"" b
            LEFT JOIN kitabkhana.""Loan"" l ON b.id = l.book_id AND l.status = 'ACTIVE'
            LEFT JOIN kitabkhana.""User"" u ON l.user_id = u.username
            WHERE b.isbn = @isbn
            LIMIT 1";

        await using var cmd = new NpgsqlCommand(query, conn);
        cmd.Parameters.AddWithValue("@isbn", isbn);

        await using var reader = await cmd.ExecuteReaderAsync();

        if (await reader.ReadAsync())
        {
            var bookId = reader.GetInt32(0).ToString();
            var bookIsbn = reader.IsDBNull(1) ? null : reader.GetString(1);
            var author = reader.IsDBNull(2) ? null : reader.GetString(2);
            var title = reader.IsDBNull(3) ? null : reader.GetString(3);
            var state = reader.GetString(4);

            var hasLoan = !reader.IsDBNull(5);

            Console.WriteLine("\n╔════════════════════════════════════════════════════════════════╗");
            Console.WriteLine("║                    BOOK DETAILS                                ║");
            Console.WriteLine("╠════════════════════════════════════════════════════════════════╣");
            Console.WriteLine($"║ ID: {bookId}");
            Console.WriteLine($"║ ISBN: {bookIsbn}");
            Console.WriteLine($"║ Title: {title}");
            Console.WriteLine($"║ Author: {author}");
            Console.WriteLine($"║ State: {state}");
            Console.WriteLine("╠════════════════════════════════════════════════════════════════╣");

            if (hasLoan)
            {
                var borrowerId = reader.GetString(6);
                var borrowerName = reader.IsDBNull(7) ? "Unknown" : reader.GetString(7);
                var loanDate = reader.GetDateTime(8);
                var dueDate = reader.GetDateTime(9);
                var daysUntilDue = (dueDate - DateTime.UtcNow).Days;

                Console.WriteLine("║ LOAN STATUS: ON LOAN                                           ║");
                Console.WriteLine($"║ Borrowed by: {borrowerName} ({borrowerId})");
                Console.WriteLine($"║ Loan Date: {loanDate:yyyy-MM-dd HH:mm}");
                Console.WriteLine($"║ Due Date: {dueDate:yyyy-MM-dd HH:mm}");
                Console.WriteLine($"║ Days Until Due: {daysUntilDue}");

                if (daysUntilDue < 0)
                {
                    Console.WriteLine($"║ ⚠️  STATUS: OVERDUE BY {Math.Abs(daysUntilDue)} DAYS!               ║");
                }
                else if (daysUntilDue <= 3)
                {
                    Console.WriteLine($"║ ⚠️  STATUS: DUE SOON ({daysUntilDue} days remaining)                  ║");
                }
                else
                {
                    Console.WriteLine("║ ✓ STATUS: ACTIVE                                               ║");
                }
            }
            else
            {
                Console.WriteLine("║ LOAN STATUS: AVAILABLE (Not currently on loan)                 ║");
            }

            Console.WriteLine("╚════════════════════════════════════════════════════════════════╝\n");

            return new BookDTO
            {
                BookId = bookId,
                ISBN = bookIsbn,
                Author = author,
                Title = title,
                State = state
            };
        }

        return null;
    }

    public async Task<LoanResponseDTO> CreateLoanAsync(string bookISBN, string username)
    {
        await using var conn = new NpgsqlConnection(connectionString);
        await conn.OpenAsync();

        // First, get the book by ISBN
        const string bookQuery = "SELECT id FROM kitabkhana.\"book\" WHERE isbn = @isbn LIMIT 1";
        await using var bookCmd = new NpgsqlCommand(bookQuery, conn);
        bookCmd.Parameters.AddWithValue("@isbn", bookISBN);
        var bookId = await bookCmd.ExecuteScalarAsync();

        if (bookId == null)
        {
            throw new Exception($"Book with ISBN {bookISBN} not found");
        }

        // Get user ID (assuming username is the user ID for now, or query user table if needed)
        // TODO: Replace with actual user lookup if you have a user table
        string userId = username; // Simplified for now

        // Create the loan
        DateTime loanDate = DateTime.UtcNow;
        DateTime dueDate = loanDate.AddDays(14); // 14 days loan period

        const string insertQuery = @"
            INSERT INTO kitabkhana.""loan"" (book_id, user_id, loan_date, due_date, return_date, status)
            VALUES (@bookId, @userId, @loanDate, @dueDate, NULL, 'ACTIVE')
            RETURNING id";

        await using var insertCmd = new NpgsqlCommand(insertQuery, conn);
        insertCmd.Parameters.AddWithValue("@bookId", Convert.ToInt32(bookId));
        insertCmd.Parameters.AddWithValue("@userId", userId);
        insertCmd.Parameters.AddWithValue("@loanDate", loanDate);
        insertCmd.Parameters.AddWithValue("@dueDate", dueDate);

        var loanId = await insertCmd.ExecuteScalarAsync();

        Console.WriteLine("\n╔════════════════════════════════════════════════════════════════╗");
        Console.WriteLine("║                    LOAN CREATED                                ║");
        Console.WriteLine("╠════════════════════════════════════════════════════════════════╣");
        Console.WriteLine($"║ Loan ID: {loanId}");
        Console.WriteLine($"║ Book ID: {bookId}");
        Console.WriteLine($"║ ISBN: {bookISBN}");
        Console.WriteLine($"║ User: {userId}");
        Console.WriteLine($"║ Loan Date: {loanDate:yyyy-MM-dd HH:mm}");
        Console.WriteLine($"║ Due Date: {dueDate:yyyy-MM-dd HH:mm}");
        Console.WriteLine($"║ Loan Period: 14 days");
        Console.WriteLine("╚════════════════════════════════════════════════════════════════╝\n");

        return new LoanResponseDTO
        {
            LoanId = loanId?.ToString(),
            BookId = bookId.ToString(),
            ISBN = bookISBN,
            UserId = userId,
            LoanDate = loanDate,
            DueDate = dueDate
        };
    }
}


// Step 1: Install PostgreSQL client
//         dotnet add package Npgsql
//         dotnet add package Dapper 

// Step 2: Add connection string
// In appsettings.json:

// Step 3: Create a DTO

// Step 4: Create Database Service
// DBService.cs — handles all PostgreSQL operations:

// Step 5: Register DBService in Program.cs

// Step 6: Inject DBService into gRPC Service
// Example: BookServiceImpl.cs