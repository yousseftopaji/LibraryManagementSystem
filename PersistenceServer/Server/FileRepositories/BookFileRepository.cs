using System;
using System.Text.Json;
using Entities;
using RepositoryContracts;

namespace FileRepositories;

public class BookFileRepository : IBookRepository
{
    private readonly string filePath = "book.json";

    public BookFileRepository()
    {
        if (!File.Exists(filePath) || new FileInfo(filePath).Length == 0)
        {
            File.WriteAllText(filePath, "[]");

            // Initialize with dummy books
            var initialBooks = new List<Book>
            {
                new Book { BookId = 1, ISBN = "978-0132350884", Title = "Clean Code", Author = "Robert C. Martin", NoOfCopies = 5, State = "Available" },
                new Book { BookId = 2, ISBN = "978-0201633610", Title = "Design Patterns", Author = "Erich Gamma", NoOfCopies = 3, State = "Available" },
                new Book { BookId = 3, ISBN = "978-0131103627", Title = "The C Programming Language", Author = "Kernighan & Ritchie", NoOfCopies = 2, State = "Available" }
            };


            File.WriteAllText(filePath, JsonSerializer.Serialize(initialBooks, new JsonSerializerOptions { WriteIndented = true }));
        }
    }

    public async Task<Book> AddAsync(Book book)
    {
        string bookAsJson = await File.ReadAllTextAsync(filePath);
        List<Book> books = JsonSerializer.Deserialize<List<Book>>(bookAsJson)!;

        int maxId = books.Count > 0 ? books.Max(b => b.BookId) : 0;
        book.BookId = maxId + 1;

        books.Add(book);
        bookAsJson = JsonSerializer.Serialize(books);

        await File.WriteAllTextAsync(filePath, bookAsJson);

        return book;
    }

    public async Task DeleteAsync(int id)
    {
        string bookAsJson = await File.ReadAllTextAsync(filePath);
        List<Book> books = JsonSerializer.Deserialize<List<Book>>(bookAsJson)!;

        Book? bookToRemove = books.SingleOrDefault(b => b.BookId == id);

        if (bookToRemove is null)
        {
            throw new InvalidOperationException(
                $"Book with ID '{id}' not found"
            );
        }

        books.Remove(bookToRemove);
        bookAsJson = JsonSerializer.Serialize(books);
        await File.WriteAllTextAsync(filePath, bookAsJson);
        return;
    }

    public IQueryable<Book> GetMany()
    {
        string bookAsJson = File.ReadAllTextAsync(filePath).Result;
        List<Book> books = JsonSerializer.Deserialize<List<Book>>(bookAsJson)!;

        return books.AsQueryable();
    }

    public async Task<Book> GetSingleAsync(int id)
    {
        string bookAsJson = await File.ReadAllTextAsync(filePath);
        List<Book> books = JsonSerializer.Deserialize<List<Book>>(bookAsJson)!;

        Book? book = books.SingleOrDefault(b => b.BookId == id);

        if (book is null)
        {
            throw new InvalidOperationException(
                $"Book with ID'{id}' not found"
            );
        }
        return book;
    }

    public async Task UpdateAsync(Book book)
    {
        string bookAsJson = await File.ReadAllTextAsync(filePath);
        List<Book> books = JsonSerializer.Deserialize<List<Book>>(bookAsJson)!;

        Book? existingBook = books.SingleOrDefault(b => b.BookId == book.BookId);
        if (existingBook is null)
        {
            throw new InvalidOperationException(
                     $"Book with ID {book.BookId} not found");
        }

        books.Remove(existingBook);
        books.Add(book);

        bookAsJson = JsonSerializer.Serialize(books);
        await File.WriteAllTextAsync(filePath, bookAsJson);
        return;
    }
}
