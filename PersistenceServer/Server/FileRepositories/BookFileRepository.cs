using System;
using System.Text.Json;
using DTOs;
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
            };


            File.WriteAllText(filePath, JsonSerializer.Serialize(initialBooks, new JsonSerializerOptions { WriteIndented = true }));
        }
    }

    public async Task<Book> AddAsync(Book book)
    {
        string bookAsJson = await File.ReadAllTextAsync(filePath);
        List<Book> books = JsonSerializer.Deserialize<List<Book>>(bookAsJson)!;

        int maxId = books.Count > 0 ? books.Max(b => b.Id) : 0;
        book.Id = maxId + 1;
    
        books.Add(book);
        bookAsJson = JsonSerializer.Serialize(books);

        await File.WriteAllTextAsync(filePath, bookAsJson);

        return book;
    }

    public async Task DeleteAsync(int id)
    {
        string bookAsJson = await File.ReadAllTextAsync(filePath);
        List<Book> books = JsonSerializer.Deserialize<List<Book>>(bookAsJson)!;

        Book? bookToRemove = books.SingleOrDefault(b => b.Id == id);

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

    public Task<IEnumerable<BookDTO>> GetAllAsync()
    {
        throw new NotImplementedException();
    }

    public Task<IEnumerable<BookDTO>> GetBooksByIsbnAsync(string isbn)
    {
        throw new NotImplementedException();
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

        Book? book = books.SingleOrDefault(b => b.Id == id);

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

        Book? existingBook = books.SingleOrDefault(b => b.Id == book.Id);
        if (existingBook is null)
        {
            throw new InvalidOperationException(
                     $"Book with ID {book.Id} not found");
        }

        books.Remove(existingBook);
        books.Add(book);

        bookAsJson = JsonSerializer.Serialize(books);
        await File.WriteAllTextAsync(filePath, bookAsJson);
        return;
    }

    public Task UpdateAsync(int id, string newState)
    {
        throw new NotImplementedException();
    }

    Task<BookDTO?> IBookRepository.GetSingleAsync(int id)
    {
        throw new NotImplementedException();
    }
}
