using DTOs.User;
using Entities;

namespace RepositoryContracts;

public interface IUserRepository
{
    Task<User> GetUserAsync(string username);
    Task<User> CreateUserAsync(User user);
}