using Entities;

namespace RepositoryContracts;

public interface IUserRepository
{
    Task<User> GetUserAsync(string username);
}