using Entities;


public class EfcUserRepository(LibraryDbContext context) : IUserRepository
{
    public async Task<User> GetUserAsync(string username)
    {
    public async Task<User> GetUserAsync(string username)
        if (user == null)
        {
            throw new Exception($"User with username {username} not found.");
        }
            throw new Exception($"User with username {username} not found.");
        return new User

