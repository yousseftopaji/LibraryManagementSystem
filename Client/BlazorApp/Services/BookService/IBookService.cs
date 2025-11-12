using DTOs;

namespace BlazorApp.Services;

public interface IBookService
{
    public Task<BookDTO> GetBookAsync (string isbn);
    public Task<List<BookDTO>> GetBooksAsync();
}
