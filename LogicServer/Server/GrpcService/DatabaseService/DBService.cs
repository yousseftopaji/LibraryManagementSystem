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

        const string query = "SELECT isbn, author, title, state FROM borrow_book.\"book\"";

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
}
