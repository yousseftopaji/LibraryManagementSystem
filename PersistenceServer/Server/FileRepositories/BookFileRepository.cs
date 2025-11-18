using System;
using System.Text.Json;
using DTOs;
using Entities;
using RepositoryContracts;

namespace FileRepositories;

public class BookFileRepository : IBookRepository
{
    public Task<BookDTO?> GetBookAsync(int id)
    {
        throw new NotImplementedException();
    }

    public Task<IEnumerable<BookDTO>> GetAllBooksAsync()
    {
        throw new NotImplementedException();
    }

    public Task<IEnumerable<BookDTO>> GetBooksByIsbnAsync(string isbn)
    {
        throw new NotImplementedException();
    }

    public Task UpdateAsync(int id, string newState)
    {
        throw new NotImplementedException();
    }
}
