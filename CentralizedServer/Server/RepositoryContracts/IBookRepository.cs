using Entities;

namespace RepositoryContracts;

public interface IBookRepository
{
    Task<Book> AddAsync(Book book);
    Task UpdateAsync(Book book);
    Task DeleteAsync(int id);
    Task<Book> GetSingleAsync(int id);
    IQueryable<Book> GetMany();
}
