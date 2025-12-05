using DTOs;
using DTOs.Book;
using EFCDatabaseRepositories.DBContext;
using Entities;
using Microsoft.EntityFrameworkCore;
using RepositoryContracts;

namespace EFCDatabaseRepositories.Repositories;

public class EfcBookRepository(LibraryDbContext context) : IBookRepository
{
    public async Task<BookDTO?> GetBookAsync(int id)
    {
        var book = await context.Book
            .Include(b => b.Genre)
            .FirstOrDefaultAsync(b => b.Id == id);
        
        if (book == null) return null;

        return new BookDTO
        {
            BookId = book.Id,
            ISBN = book.ISBN,
            Title = book.Title,
            Author = book.Author,
            State = book.State,
            Genre = book.Genre.Select(g => new GenreDTO()
            {
                Name = g.Name
            }).ToList()
        };
    }

    public async Task<IEnumerable<BookDTO>> GetAllBooksAsync()
    {
        var books = await context.Book
            .Include(b => b.Genre)
            .ToListAsync();
        return books.Select(b => new BookDTO
        {
            BookId = b.Id,
            ISBN = b.ISBN,
            Title = b.Title,
            Author = b.Author,
            State = b.State,
            Genre = b.Genre.Select(g => new GenreDTO()
            {
            Name = g.Name
        }).ToList()
        });
    }

    public async Task<IEnumerable<BookDTO>> GetBooksByIsbnAsync(string isbn)
    {
        var books = await context.Book
            .Where(b => b.ISBN == isbn)
            .Include(b => b.Genre)
            .ToListAsync();

        return books.Select(b => new BookDTO
        {
            BookId = b.Id,
            ISBN = b.ISBN,
            Title = b.Title,
            Author = b.Author,
            State = b.State,
            Genre = b.Genre.Select(g => new GenreDTO()
            {
                Name = g.Name
            }).ToList()
        });
    }

    public async Task<BookDTO> UpdateBookStateAsync(int id, string newState)
    {
        var book = await context.Book
            .Include(b => b.Genre)
            .FirstOrDefaultAsync(b => b.Id == id);
        if (book == null) throw new ArgumentException("Book not found");

        book.State = newState;
        await context.SaveChangesAsync();

        return new BookDTO
        {
            BookId = book.Id,
            ISBN = book.ISBN,
            Title = book.Title,
            Author = book.Author,
            State = book.State,
            Genre = book.Genre.Select(g => new GenreDTO()
            {
                Name = g.Name
            }).ToList()
        };
    }
}
