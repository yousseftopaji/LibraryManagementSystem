using DTOs;

namespace BlazorApp.Services;

public interface IBookService
{
    public Task<BookDTO> GetBookAsync (BookDTO book);
    public Task<List<BookDTO>> GetBooksAsync();
}
