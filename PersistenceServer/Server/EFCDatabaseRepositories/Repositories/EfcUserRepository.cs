using DTOs.User;
using EFCDatabaseRepositories.DBContext;
using Entities;
using RepositoryContracts;

namespace EFCDatabaseRepositories.Repositories;

public class EfcUserRepository(LibraryDbContext context) : IUserRepository
{
    public async Task<UserDTO> GetUserAsync(string username)
    {
        var user = await context.User.FindAsync(username);
        if (user == null)
        {
            throw new Exception($"User with username {username} not found.");
        }
        
        return new UserDTO()
        {
            Username = user.Username,
            PasswordHash = user.PasswordHash,
            Role = user.Role,
            Name = user.Name,
            PhoneNumber = user.PhoneNumber,
            Email = user.Email
        };
    }

    public async Task<UserDTO> CreateUserAsync(User user)
    {
        context.User.Add(user);
        await context.SaveChangesAsync();
        
        return new UserDTO()
        {
            Username = user.Username,
            PasswordHash = user.PasswordHash,
            Role = user.Role,
            Name = user.Name,
            PhoneNumber = user.PhoneNumber,
            Email = user.Email
        };
    }
}