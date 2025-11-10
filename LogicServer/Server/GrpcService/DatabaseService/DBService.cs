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