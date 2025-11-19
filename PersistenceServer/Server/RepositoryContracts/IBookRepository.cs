using DTOs;
using DTOs.Book;
using Entities;

namespace RepositoryContracts;

public interface IBookRepository
{
    Task<BookDTO?> GetBookAsync(int id);
    Task<IEnumerable<BookDTO>> GetAllBooksAsync();
    Task<IEnumerable<BookDTO>> GetBooksByIsbnAsync(string isbn);
    Task<BookDTO> UpdateBookStateAsync(int id, string newState);
}
