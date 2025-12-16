using EFCDatabaseRepositories.DBContext;
using EFCDatabaseRepositories.Repositories;
using Entities;
using Microsoft.EntityFrameworkCore;

namespace Server.Tests;

public class EfcLoanRepositoryTests
{
    private LibraryDbContext CreateInMemoryContext()
    {
        var options = new DbContextOptionsBuilder<LibraryDbContext>()
            .UseInMemoryDatabase(databaseName: Guid.NewGuid().ToString())
            .Options;

        return new LibraryDbContext(options);
    }

    [Fact]
    public async Task CreateLoanAsync_ShouldReturnLoanDTO_WhenLoanIsCreated()
    {
        // Arrange
        await using var context = CreateInMemoryContext();
        var repository = new EfcLoanRepository(context);

        var loan = new Loan
        {
            BookId = 1,
            Username = "testuser",
            BorrowDate = DateTime.Now,
            DueDate = DateTime.Now.AddDays(14),
            NumberOfExtensions = 0
        };

        // Act
        var result = await repository.CreateLoanAsync(loan);

        // Assert
        Assert.NotNull(result);
        Assert.Equal(1, result.BookId);
        Assert.Equal("testuser", result.Username);
        Assert.Equal(0, result.NumberOfExtensions);
        Assert.True(result.LoanId > 0);
    }

    [Fact]
    public async Task GetLoanByUsernameAsync_ShouldReturnLoanDTO_WhenLoanExists()
    {
        // Arrange
        await using var context = CreateInMemoryContext();
        var repository = new EfcLoanRepository(context);

        var loan = new Loan
        {
            BookId = 1,
            Username = "testuser",
            BorrowDate = DateTime.Now,
            DueDate = DateTime.Now.AddDays(14),
            NumberOfExtensions = 0
        };

        await context.Loan.AddAsync(loan);
        await context.SaveChangesAsync();

        // Act
        var result = await repository.GetLoanByUsernameAsync("testuser", 1);

        // Assert
        Assert.NotNull(result);
        Assert.Equal("testuser", result.Username);
        Assert.Equal(1, result.BookId);
    }

    [Fact]
    public async Task GetLoanByUsernameAsync_ShouldReturnNull_WhenLoanDoesNotExist()
    {
        // Arrange
        await using var context = CreateInMemoryContext();
        var repository = new EfcLoanRepository(context);

        // Act
        var result = await repository.GetLoanByUsernameAsync("nonexistent", 999);

        // Assert
        Assert.Null(result);
    }

    [Fact]
    public async Task UpdateLoanAsync_ShouldUpdateLoan_WhenLoanExists()
    {
        // Arrange
        await using var context = CreateInMemoryContext();
        var repository = new EfcLoanRepository(context);

        var loan = new Loan
        {
            BookId = 1,
            Username = "testuser",
            BorrowDate = DateTime.Now,
            DueDate = DateTime.Now.AddDays(14),
            NumberOfExtensions = 0
        };

        await context.Loan.AddAsync(loan);
        await context.SaveChangesAsync();

        // Act
        loan.DueDate = DateTime.Now.AddDays(21);
        loan.NumberOfExtensions = 1;
        var result = await repository.UpdateLoanAsync(loan);

        // Assert
        Assert.NotNull(result);
        Assert.Equal(1, result.NumberOfExtensions);
        Assert.Equal(loan.DueDate, result.DueDate);
    }

    [Fact]
    public async Task UpdateLoanAsync_ShouldThrowException_WhenLoanDoesNotExist()
    {
        // Arrange
        await using var context = CreateInMemoryContext();
        var repository = new EfcLoanRepository(context);

        var loan = new Loan
        {
            Id = 999,
            BookId = 1,
            Username = "testuser",
            BorrowDate = DateTime.Now,
            DueDate = DateTime.Now.AddDays(14),
            NumberOfExtensions = 0
        };

        // Act & Assert
        await Assert.ThrowsAsync<ArgumentException>(() => repository.UpdateLoanAsync(loan));
    }

    [Fact]
    public async Task GetLoansByIsbnAsync_ShouldReturnLoans_WhenLoansExist()
    {
        // Arrange
        await using var context = CreateInMemoryContext();
        var repository = new EfcLoanRepository(context);

        var book = new Book
        {
            ISBN = "978-3-16-148410-0",
            Author = "Test Author",
            Title = "Test Book",
            State = "Available",
            Genre = new List<Genre>(),
            Loans = new List<Loan>()
        };

        await context.Book.AddAsync(book);
        await context.SaveChangesAsync();

        var loan1 = new Loan
        {
            BookId = book.Id,
            Username = "user1",
            BorrowDate = DateTime.Now,
            DueDate = DateTime.Now.AddDays(14),
            NumberOfExtensions = 0
        };

        var loan2 = new Loan
        {
            BookId = book.Id,
            Username = "user2",
            BorrowDate = DateTime.Now,
            DueDate = DateTime.Now.AddDays(14),
            NumberOfExtensions = 0
        };

        await context.Loan.AddAsync(loan1);
        await context.Loan.AddAsync(loan2);
        await context.SaveChangesAsync();

        // Act
        var result = await repository.GetLoansByIsbnAsync("978-3-16-148410-0");

        // Assert
        Assert.NotNull(result);
        var loans = result.ToList();
        Assert.Equal(2, loans.Count);
        Assert.All(loans, loan => Assert.Equal(book.Id, loan.BookId));
    }

    [Fact]
    public async Task GetLoansByIsbnAsync_ShouldReturnEmpty_WhenNoLoansExist()
    {
        // Arrange
        await using var context = CreateInMemoryContext();
        var repository = new EfcLoanRepository(context);

        var book = new Book
        {
            ISBN = "978-3-16-148410-0",
            Author = "Test Author",
            Title = "Test Book",
            State = "Available",
            Genre = new List<Genre>(),
            Loans = new List<Loan>()
        };

        await context.Book.AddAsync(book);
        await context.SaveChangesAsync();

        // Act
        var result = await repository.GetLoansByIsbnAsync("978-3-16-148410-0");

        // Assert
        Assert.NotNull(result);
        Assert.Empty(result);
    }

    [Fact]
    public async Task GetLoanByIdAsync_ShouldReturnLoanDTO_WhenLoanExists()
    {
        // Arrange
        await using var context = CreateInMemoryContext();
        var repository = new EfcLoanRepository(context);

        var loan = new Loan
        {
            BookId = 1,
            Username = "testuser",
            BorrowDate = DateTime.Now,
            DueDate = DateTime.Now.AddDays(14),
            NumberOfExtensions = 0
        };

        await context.Loan.AddAsync(loan);
        await context.SaveChangesAsync();

        // Act
        var result = await repository.GetLoanByIdAsync(loan.Id);

        // Assert
        Assert.NotNull(result);
        Assert.Equal(loan.Id, result.LoanId);
        Assert.Equal("testuser", result.Username);
    }

    [Fact]
    public async Task GetLoanByIdAsync_ShouldReturnNull_WhenLoanDoesNotExist()
    {
        // Arrange
        await using var context = CreateInMemoryContext();
        var repository = new EfcLoanRepository(context);

        // Act
        var result = await repository.GetLoanByIdAsync(999);

        // Assert
        Assert.Null(result);
    }
}

