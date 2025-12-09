using DTOs.User;
using Entities;

namespace RepositoryContracts;

public interface IUserRepository
{
    Task<UserDTO> GetUserAsync(string username);
    Task<UserDTO> CreateUserAsync(User user);
}