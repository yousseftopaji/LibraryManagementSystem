using Npgsql;
using DTOs;

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

        const string query = "SELECT isbn, author, title, state FROM kitabkhana.\"book\"";

        await using var cmd = new NpgsqlCommand(query, conn);
        await using var reader = await cmd.ExecuteReaderAsync();

        while (await reader.ReadAsync())
        {
            books.Add(new BookDTO
            {
                ISBN = reader.GetString(0),
                Author = reader.GetString(1),
                Title = reader.GetString(2),
                State = reader.GetString(3)
            });
        }

        return books;
    }

    public async Task<List<BookDTO>> GetBooksByIsbnAsync(string isbn)
    {
        var books = new List<BookDTO>();

        await using var conn = new NpgsqlConnection(connectionString);
        await conn.OpenAsync();

        var query = "SELECT isbn, author, title, genre, state FROM kitabkhana.book WHERE isbn = @isbn";

        await using var cmd = new NpgsqlCommand(query, conn);
        cmd.Parameters.AddWithValue("isbn", isbn);

        await using var reader = await cmd.ExecuteReaderAsync();

        while (await reader.ReadAsync())
        {
            books.Add(new BookDTO
            {
                ISBN = reader.GetString(0),
                Author = reader.GetString(1),
                Title = reader.GetString(2),
                State = reader.GetString(3)
            });
        }

        return books;
    }

    public async Task CreateLoanAsync(int bookId)
    {
        await using var conn = new NpgsqlConnection(connectionString);
        await conn.OpenAsync();

        // Transaction ensuring both book update and loan creation succeed together
        await using var transaction = await conn.BeginTransactionAsync();

        try
        {
            // 1️ Inserting a new loan
            var insertLoanQuery = @"
                    INSERT INTO kitabkhana.loan (book_id, borrow_date, return_date, isReturned, numberOfExtensions, username)
                    VALUES (@book_id, NOW(), NOW() + INTERVAL '30 days', FALSE, 0, null);";

            await using (var insertCmd = new NpgsqlCommand(insertLoanQuery, conn, transaction))
            {
                insertCmd.Parameters.AddWithValue("@book_id", bookId);
                await insertCmd.ExecuteNonQueryAsync();
            }

            // 2 Updating the book state to "Borrowed"
            var updateBookQuery = @"
                    UPDATE kitabkhana.book
                    SET state = 'Borrowed'
                    WHERE id = @book_id;";

            await using (var updateCmd = new NpgsqlCommand(updateBookQuery, conn, transaction))
            {
                updateCmd.Parameters.AddWithValue("@book_id", bookId);
                await updateCmd.ExecuteNonQueryAsync();
            }

            // Commit both queries
            await transaction.CommitAsync();
        }
        catch
        {
            await transaction.RollbackAsync();
            throw; // rethrow to be caught by gRPC service
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