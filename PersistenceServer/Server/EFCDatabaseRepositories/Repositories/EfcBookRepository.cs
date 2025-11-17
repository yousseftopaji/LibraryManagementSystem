using DTOs;
using Microsoft.EntityFrameworkCore;
using RepositoryContracts;

namespace EFCDatabaseRepositories;

public class EfcBookRepository : IBookRepository
{
    private readonly LibraryDbContext context;

    public EfcBookRepository(LibraryDbContext context)
    {
        this.context = context;
    }

    public async Task<BookDTO?> GetBookAsync(int id)
    {
        var book = await context.Book.FindAsync(id);
        if (book == null) return null;

        return new BookDTO
        {
            BookId = book.Id,
            ISBN = book.ISBN,
            Title = book.Title,
            Author = book.Author,
            State = book.State
        };
    }

    public async Task<IEnumerable<BookDTO>> GetAllBooksAsync()
    {
        var books = await context.Book.ToListAsync();
        return books.Select(b => new BookDTO
        {
            BookId = b.Id,
            ISBN = b.ISBN,
            Title = b.Title,
            Author = b.Author,
            State = b.State
        });
    }

    public async Task<IEnumerable<BookDTO>> GetBooksByIsbnAsync(string isbn)
    {
        var books = await context.Book
            .Where(b => b.ISBN == isbn)
            .ToListAsync();

        return books.Select(b => new BookDTO
        {
            BookId = b.Id,
            ISBN = b.ISBN,
            Title = b.Title,
            Author = b.Author,
            State = b.State
        });
    }

    public async Task UpdateAsync(int id, string newState)
    {
        var book = await context.Book.FindAsync(id);
        if (book != null)
        {
            book.State = newState;
            await context.SaveChangesAsync();
        }
    }
}
