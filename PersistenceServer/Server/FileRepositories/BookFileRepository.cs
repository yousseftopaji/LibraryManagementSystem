using System;
using System.Text.Json;
using DTOs;
using Entities;

namespace FileRepositories;

public class BookFileRepository 
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

    public Task UpdateStatAsync(int id, string newState)
    {
        throw new NotImplementedException();
    }
}
