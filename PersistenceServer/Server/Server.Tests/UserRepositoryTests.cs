using DTOs.User;
using Entities;
using Moq;
using RepositoryContracts;

namespace Server.Tests;

public class UserRepositoryTests
{
    [Fact]
    public async Task GetUserAsync_ShouldReturnUserDTO_WhenUserExists()
    {
        // Arrange
        var mockRepository = new Mock<IUserRepository>();
        var expectedUser = new UserDTO
        {
            Username = "testuser",
            PasswordHash = "hashed_password",
            Role = "User",
            Name = "Test User",
            PhoneNumber = "1234567890",
            Email = "test@example.com"
        };

        mockRepository.Setup(repo => repo.GetUserAsync("testuser"))
            .ReturnsAsync(expectedUser);

        // Act
        var result = await mockRepository.Object.GetUserAsync("testuser");

        // Assert
        Assert.NotNull(result);
        Assert.Equal("testuser", result.Username);
        Assert.Equal("hashed_password", result.PasswordHash);
        Assert.Equal("User", result.Role);
        Assert.Equal("Test User", result.Name);
        Assert.Equal("1234567890", result.PhoneNumber);
        Assert.Equal("test@example.com", result.Email);
        mockRepository.Verify(repo => repo.GetUserAsync("testuser"), Times.Once);
    }

    [Fact]
    public async Task GetUserAsync_ShouldReturnNull_WhenUserDoesNotExist()
    {
        // Arrange
        var mockRepository = new Mock<IUserRepository>();
        mockRepository.Setup(repo => repo.GetUserAsync("nonexistent"))
            .ReturnsAsync((UserDTO?)null);

        // Act
        var result = await mockRepository.Object.GetUserAsync("nonexistent");

        // Assert
        Assert.Null(result);
        mockRepository.Verify(repo => repo.GetUserAsync("nonexistent"), Times.Once);
    }

    [Fact]
    public async Task CreateUserAsync_ShouldReturnUserDTO_WhenUserIsCreated()
    {
        // Arrange
        var mockRepository = new Mock<IUserRepository>();
        var newUser = new User
        {
            Username = "newuser",
            PasswordHash = "hashed_password",
            Role = "User",
            Name = "New User",
            PhoneNumber = "9876543210",
            Email = "newuser@example.com"
        };

        var expectedUserDTO = new UserDTO
        {
            Username = "newuser",
            PasswordHash = "hashed_password",
            Role = "User",
            Name = "New User",
            PhoneNumber = "9876543210",
            Email = "newuser@example.com"
        };

        mockRepository.Setup(repo => repo.CreateUserAsync(It.IsAny<User>()))
            .ReturnsAsync(expectedUserDTO);

        // Act
        var result = await mockRepository.Object.CreateUserAsync(newUser);

        // Assert
        Assert.NotNull(result);
        Assert.Equal("newuser", result.Username);
        Assert.Equal("hashed_password", result.PasswordHash);
        Assert.Equal("User", result.Role);
        Assert.Equal("New User", result.Name);
        Assert.Equal("9876543210", result.PhoneNumber);
        Assert.Equal("newuser@example.com", result.Email);
        mockRepository.Verify(repo => repo.CreateUserAsync(It.IsAny<User>()), Times.Once);
    }

    [Fact]
    public async Task CreateUserAsync_ShouldCreateLibrarian_WhenRoleIsLibrarian()
    {
        // Arrange
        var mockRepository = new Mock<IUserRepository>();
        var newLibrarian = new User
        {
            Username = "librarian1",
            PasswordHash = "hashed_password",
            Role = "Librarian",
            Name = "Librarian User",
            PhoneNumber = "1111111111",
            Email = "librarian@example.com"
        };

        var expectedUserDTO = new UserDTO
        {
            Username = "librarian1",
            PasswordHash = "hashed_password",
            Role = "Librarian",
            Name = "Librarian User",
            PhoneNumber = "1111111111",
            Email = "librarian@example.com"
        };

        mockRepository.Setup(repo => repo.CreateUserAsync(It.IsAny<User>()))
            .ReturnsAsync(expectedUserDTO);

        // Act
        var result = await mockRepository.Object.CreateUserAsync(newLibrarian);

        // Assert
        Assert.NotNull(result);
        Assert.Equal("librarian1", result.Username);
        Assert.Equal("Librarian", result.Role);
        mockRepository.Verify(repo => repo.CreateUserAsync(It.IsAny<User>()), Times.Once);
    }

    [Fact]
    public async Task GetUserAsync_ShouldReturnUserWithCorrectEmail_WhenUserHasEmail()
    {
        // Arrange
        var mockRepository = new Mock<IUserRepository>();
        var expectedUser = new UserDTO
        {
            Username = "emailuser",
            PasswordHash = "hashed_password",
            Role = "User",
            Name = "Email User",
            PhoneNumber = "5555555555",
            Email = "email@test.com"
        };

        mockRepository.Setup(repo => repo.GetUserAsync("emailuser"))
            .ReturnsAsync(expectedUser);

        // Act
        var result = await mockRepository.Object.GetUserAsync("emailuser");

        // Assert
        Assert.NotNull(result);
        Assert.Equal("email@test.com", result.Email);
        mockRepository.Verify(repo => repo.GetUserAsync("emailuser"), Times.Once);
    }

    [Fact]
    public async Task CreateUserAsync_ShouldHandleUserWithLongName()
    {
        // Arrange
        var mockRepository = new Mock<IUserRepository>();
        var newUser = new User
        {
            Username = "longname",
            PasswordHash = "hashed_password",
            Role = "User",
            Name = "User With A Very Long Name That Should Still Be Handled Correctly",
            PhoneNumber = "1234567890",
            Email = "longname@example.com"
        };

        var expectedUserDTO = new UserDTO
        {
            Username = "longname",
            PasswordHash = "hashed_password",
            Role = "User",
            Name = "User With A Very Long Name That Should Still Be Handled Correctly",
            PhoneNumber = "1234567890",
            Email = "longname@example.com"
        };

        mockRepository.Setup(repo => repo.CreateUserAsync(It.IsAny<User>()))
            .ReturnsAsync(expectedUserDTO);

        // Act
        var result = await mockRepository.Object.CreateUserAsync(newUser);

        // Assert
        Assert.NotNull(result);
        Assert.Equal("User With A Very Long Name That Should Still Be Handled Correctly", result.Name);
        mockRepository.Verify(repo => repo.CreateUserAsync(It.IsAny<User>()), Times.Once);
    }

    [Fact]
    public async Task GetUserAsync_ShouldReturnUserWithCorrectPhoneNumber()
    {
        // Arrange
        var mockRepository = new Mock<IUserRepository>();
        var expectedUser = new UserDTO
        {
            Username = "phoneuser",
            PasswordHash = "hashed_password",
            Role = "User",
            Name = "Phone User",
            PhoneNumber = "+45 12 34 56 78",
            Email = "phone@example.com"
        };

        mockRepository.Setup(repo => repo.GetUserAsync("phoneuser"))
            .ReturnsAsync(expectedUser);

        // Act
        var result = await mockRepository.Object.GetUserAsync("phoneuser");

        // Assert
        Assert.NotNull(result);
        Assert.Equal("+45 12 34 56 78", result.PhoneNumber);
        mockRepository.Verify(repo => repo.GetUserAsync("phoneuser"), Times.Once);
    }

    [Fact]
    public async Task CreateUserAsync_ShouldPreservePasswordHash()
    {
        // Arrange
        var mockRepository = new Mock<IUserRepository>();
        var hashedPassword = "very_secure_hashed_password_123456";
        var newUser = new User
        {
            Username = "secureuser",
            PasswordHash = hashedPassword,
            Role = "User",
            Name = "Secure User",
            PhoneNumber = "9999999999",
            Email = "secure@example.com"
        };

        var expectedUserDTO = new UserDTO
        {
            Username = "secureuser",
            PasswordHash = hashedPassword,
            Role = "User",
            Name = "Secure User",
            PhoneNumber = "9999999999",
            Email = "secure@example.com"
        };

        mockRepository.Setup(repo => repo.CreateUserAsync(It.IsAny<User>()))
            .ReturnsAsync(expectedUserDTO);

        // Act
        var result = await mockRepository.Object.CreateUserAsync(newUser);

        // Assert
        Assert.NotNull(result);
        Assert.Equal(hashedPassword, result.PasswordHash);
        mockRepository.Verify(repo => repo.CreateUserAsync(It.IsAny<User>()), Times.Once);
    }
}
