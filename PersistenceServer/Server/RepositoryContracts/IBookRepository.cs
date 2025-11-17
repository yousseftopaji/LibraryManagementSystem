using DTOs;

namespace RepositoryContracts;

public interface IBookRepository
{
    Task<BookDTO?> GetBookAsync(int id);
    Task<IEnumerable<BookDTO>> GetAllBooksAsync();
    Task<IEnumerable<BookDTO>> GetBooksByIsbnAsync(string isbn);
    Task UpdateAsync(int id, string newState);
}
