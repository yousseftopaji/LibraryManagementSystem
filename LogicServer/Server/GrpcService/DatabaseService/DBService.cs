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

        const string query = "SELECT id, isbn, author, title, state FROM kitabkhana.\"book\"";

        await using var cmd = new NpgsqlCommand(query, conn);
        await using var reader = await cmd.ExecuteReaderAsync();

        while (await reader.ReadAsync())
        {
            books.Add(new BookDTO
            {
                BookId = reader.GetInt32(0).ToString(),
                ISBN = reader.IsDBNull(1) ? null : reader.GetString(1),
                Author = reader.IsDBNull(2) ? null : reader.GetString(2),
                Title = reader.IsDBNull(3) ? null : reader.GetString(3),
                State = reader.GetString(4)
            });
        }

        return books;
    }

    public async Task<BookDTO?> GetBookByIsbnAsync(string isbn)
    {
        await using var conn = new NpgsqlConnection(connectionString);
        await conn.OpenAsync();

        const string query = @"SELECT id, isbn, author, title, state
                               FROM kitabkhana.""book""
                               WHERE isbn = $1 AND state = 'Available'
                               LIMIT 1";

        await using var cmd = new NpgsqlCommand(query, conn);
        cmd.Parameters.AddWithValue(isbn);

        await using var reader = await cmd.ExecuteReaderAsync();

        if (await reader.ReadAsync())
        {
            return new BookDTO
            {
                BookId = reader.GetInt32(0).ToString(),
                ISBN = reader.IsDBNull(1) ? null : reader.GetString(1),
                Author = reader.IsDBNull(2) ? null : reader.GetString(2),
                Title = reader.IsDBNull(3) ? null : reader.GetString(3),
                State = reader.GetString(4)
            };
        }

        return null;
    }

    public async Task<List<BookDTO>> GetBooksByIsbnAsync(string isbn)
    {
        var books = new List<BookDTO>();

        await using var conn = new NpgsqlConnection(connectionString);
        await conn.OpenAsync();

        const string query = @"SELECT id, isbn, author, title, state
                               FROM kitabkhana.""book""
                               WHERE isbn = $1";

        await using var cmd = new NpgsqlCommand(query, conn);
        cmd.Parameters.AddWithValue(isbn);

        await using var reader = await cmd.ExecuteReaderAsync();

        while (await reader.ReadAsync())
        {
            books.Add(new BookDTO
            {
                BookId = reader.GetInt32(0).ToString(),
                ISBN = reader.IsDBNull(1) ? null : reader.GetString(1),
                Author = reader.IsDBNull(2) ? null : reader.GetString(2),
                Title = reader.IsDBNull(3) ? null : reader.GetString(3),
                State = reader.GetString(4)
            });
        }

        return books;
    }

    public async Task<LoanDTO?> CreateLoanAsync(string username, string bookId, int loanDurationDays)
    {
        await using var conn = new NpgsqlConnection(connectionString);
        await conn.OpenAsync();

        await using var transaction = await conn.BeginTransactionAsync();

        try
        {
            // Check if book is available
            const string checkBookQuery = @"SELECT state FROM kitabkhana.""book"" WHERE id = $1";
            await using var checkCmd = new NpgsqlCommand(checkBookQuery, conn, transaction);
            checkCmd.Parameters.AddWithValue(int.Parse(bookId));

            var bookState = await checkCmd.ExecuteScalarAsync();
            if (bookState == null || bookState.ToString() != "Available")
            {
                await transaction.RollbackAsync();
                return null;
            }

            // Create loan
            var borrowDate = DateTime.UtcNow.Date;
            var dueDate = borrowDate.AddDays(loanDurationDays);

            const string insertLoanQuery = @"INSERT INTO kitabkhana.""Loan""
                                            (borrowDate, dueDate, isReturned, numberOfExtensions, username, bookId)
                                            VALUES ($1, $2, $3, $4, $5, $6)
                                            RETURNING id";

            await using var insertCmd = new NpgsqlCommand(insertLoanQuery, conn, transaction);
            insertCmd.Parameters.AddWithValue(borrowDate);
            insertCmd.Parameters.AddWithValue(dueDate);
            insertCmd.Parameters.AddWithValue(false);
            insertCmd.Parameters.AddWithValue(0);
            insertCmd.Parameters.AddWithValue(username);
            insertCmd.Parameters.AddWithValue(int.Parse(bookId));

            var loanId = await insertCmd.ExecuteScalarAsync();

            // Update book state to Borrowed
            const string updateBookQuery = @"UPDATE kitabkhana.""book"" SET state = 'Borrowed' WHERE id = $1";
            await using var updateCmd = new NpgsqlCommand(updateBookQuery, conn, transaction);
            updateCmd.Parameters.AddWithValue(int.Parse(bookId));
            await updateCmd.ExecuteNonQueryAsync();

            await transaction.CommitAsync();

            return new LoanDTO
            {
                LoanId = loanId?.ToString(),
                BorrowDate = borrowDate,
                DueDate = dueDate,
                IsReturned = false,
                NumberOfExtensions = 0,
                Username = username,
                BookId = bookId
            };
        }
        catch (Exception)
        {
            await transaction.RollbackAsync();
            throw;
        }
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