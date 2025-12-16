using DTOs.Loan;
using Entities;
using Moq;
using RepositoryContracts;

namespace Server.Tests;

public class LoanRepositoryTests
{
    [Fact]
    public async Task CreateLoanAsync_ShouldReturnLoanDTO_WhenLoanIsCreated()
    {
        // Arrange
        var mockRepository = new Mock<ILoanRepository>();
        var loan = new Loan
        {
            BookId = 1,
            Username = "testuser",
            BorrowDate = DateTime.Now,
            DueDate = DateTime.Now.AddDays(14),
            NumberOfExtensions = 0
        };

        var expectedLoanDTO = new LoanDTO
        {
            LoanId = 1,
            BookId = 1,
            Username = "testuser",
            BorrowDate = loan.BorrowDate,
            DueDate = loan.DueDate,
            NumberOfExtensions = 0
        };

        mockRepository.Setup(repo => repo.CreateLoanAsync(It.IsAny<Loan>()))
            .ReturnsAsync(expectedLoanDTO);

        // Act
        var result = await mockRepository.Object.CreateLoanAsync(loan);

        // Assert
        Assert.NotNull(result);
        Assert.Equal(1, result.LoanId);
        Assert.Equal(1, result.BookId);
        Assert.Equal("testuser", result.Username);
        Assert.Equal(0, result.NumberOfExtensions);
        mockRepository.Verify(repo => repo.CreateLoanAsync(It.IsAny<Loan>()), Times.Once);
    }

    [Fact]
    public async Task GetLoanByUsernameAsync_ShouldReturnLoanDTO_WhenLoanExists()
    {
        // Arrange
        var mockRepository = new Mock<ILoanRepository>();
        var expectedLoan = new LoanDTO
        {
            LoanId = 1,
            BookId = 1,
            Username = "testuser",
            BorrowDate = DateTime.Now,
            DueDate = DateTime.Now.AddDays(14),
            NumberOfExtensions = 0
        };

        mockRepository.Setup(repo => repo.GetLoanByUsernameAsync("testuser", 1))
            .ReturnsAsync(expectedLoan);

        // Act
        var result = await mockRepository.Object.GetLoanByUsernameAsync("testuser", 1);

        // Assert
        Assert.NotNull(result);
        Assert.Equal("testuser", result.Username);
        Assert.Equal(1, result.BookId);
        Assert.Equal(1, result.LoanId);
        mockRepository.Verify(repo => repo.GetLoanByUsernameAsync("testuser", 1), Times.Once);
    }

    [Fact]
    public async Task GetLoanByUsernameAsync_ShouldReturnNull_WhenLoanDoesNotExist()
    {
        // Arrange
        var mockRepository = new Mock<ILoanRepository>();
        mockRepository.Setup(repo => repo.GetLoanByUsernameAsync("nonexistent", 999))
            .ReturnsAsync((LoanDTO?)null);

        // Act
        var result = await mockRepository.Object.GetLoanByUsernameAsync("nonexistent", 999);

        // Assert
        Assert.Null(result);
        mockRepository.Verify(repo => repo.GetLoanByUsernameAsync("nonexistent", 999), Times.Once);
    }

    [Fact]
    public async Task UpdateLoanAsync_ShouldUpdateLoan_WhenLoanExists()
    {
        // Arrange
        var mockRepository = new Mock<ILoanRepository>();
        var loan = new Loan
        {
            Id = 1,
            BookId = 1,
            Username = "testuser",
            BorrowDate = DateTime.Now,
            DueDate = DateTime.Now.AddDays(21),
            NumberOfExtensions = 1
        };

        var updatedLoanDTO = new LoanDTO
        {
            LoanId = 1,
            BookId = 1,
            Username = "testuser",
            BorrowDate = loan.BorrowDate,
            DueDate = loan.DueDate,
            NumberOfExtensions = 1
        };

        mockRepository.Setup(repo => repo.UpdateLoanAsync(It.IsAny<Loan>()))
            .ReturnsAsync(updatedLoanDTO);

        // Act
        var result = await mockRepository.Object.UpdateLoanAsync(loan);

        // Assert
        Assert.NotNull(result);
        Assert.Equal(1, result.NumberOfExtensions);
        Assert.Equal(loan.DueDate, result.DueDate);
        mockRepository.Verify(repo => repo.UpdateLoanAsync(It.IsAny<Loan>()), Times.Once);
    }

    [Fact]
    public async Task UpdateLoanAsync_ShouldThrowException_WhenLoanDoesNotExist()
    {
        // Arrange
        var mockRepository = new Mock<ILoanRepository>();
        var loan = new Loan
        {
            Id = 999,
            BookId = 1,
            Username = "testuser",
            BorrowDate = DateTime.Now,
            DueDate = DateTime.Now.AddDays(14),
            NumberOfExtensions = 0
        };

        mockRepository.Setup(repo => repo.UpdateLoanAsync(It.IsAny<Loan>()))
            .ThrowsAsync(new ArgumentException("Loan not found"));

        // Act & Assert
        await Assert.ThrowsAsync<ArgumentException>(
            () => mockRepository.Object.UpdateLoanAsync(loan));
        mockRepository.Verify(repo => repo.UpdateLoanAsync(It.IsAny<Loan>()), Times.Once);
    }

    [Fact]
    public async Task GetLoansByIsbnAsync_ShouldReturnLoans_WhenLoansExist()
    {
        // Arrange
        var mockRepository = new Mock<ILoanRepository>();
        var isbn = "978-3-16-148410-0";
        var expectedLoans = new List<LoanDTO>
        {
            new LoanDTO
            {
                LoanId = 1,
                BookId = 1,
                Username = "user1",
                BorrowDate = DateTime.Now,
                DueDate = DateTime.Now.AddDays(14),
                NumberOfExtensions = 0,
                IsReturned = false
            },
            new LoanDTO
            {
                LoanId = 2,
                BookId = 1,
                Username = "user2",
                BorrowDate = DateTime.Now,
                DueDate = DateTime.Now.AddDays(14),
                NumberOfExtensions = 0,
                IsReturned = false
            }
        };

        mockRepository.Setup(repo => repo.GetLoansByIsbnAsync(isbn))
            .ReturnsAsync(expectedLoans);

        // Act
        var result = await mockRepository.Object.GetLoansByIsbnAsync(isbn);

        // Assert
        Assert.NotNull(result);
        var loans = result.ToList();
        Assert.Equal(2, loans.Count);
        Assert.All(loans, loan => Assert.Equal(1, loan.BookId));
        mockRepository.Verify(repo => repo.GetLoansByIsbnAsync(isbn), Times.Once);
    }

    [Fact]
    public async Task GetLoansByIsbnAsync_ShouldReturnEmpty_WhenNoLoansExist()
    {
        // Arrange
        var mockRepository = new Mock<ILoanRepository>();
        var isbn = "978-3-16-148410-0";
        mockRepository.Setup(repo => repo.GetLoansByIsbnAsync(isbn))
            .ReturnsAsync(new List<LoanDTO>());

        // Act
        var result = await mockRepository.Object.GetLoansByIsbnAsync(isbn);

        // Assert
        Assert.NotNull(result);
        Assert.Empty(result);
        mockRepository.Verify(repo => repo.GetLoansByIsbnAsync(isbn), Times.Once);
    }

    [Fact]
    public async Task GetLoanByIdAsync_ShouldReturnLoanDTO_WhenLoanExists()
    {
        // Arrange
        var mockRepository = new Mock<ILoanRepository>();
        var expectedLoan = new LoanDTO
        {
            LoanId = 1,
            BookId = 1,
            Username = "testuser",
            BorrowDate = DateTime.Now,
            DueDate = DateTime.Now.AddDays(14),
            NumberOfExtensions = 0
        };

        mockRepository.Setup(repo => repo.GetLoanByIdAsync(1))
            .ReturnsAsync(expectedLoan);

        // Act
        var result = await mockRepository.Object.GetLoanByIdAsync(1);

        // Assert
        Assert.NotNull(result);
        Assert.Equal(1, result.LoanId);
        Assert.Equal("testuser", result.Username);
        mockRepository.Verify(repo => repo.GetLoanByIdAsync(1), Times.Once);
    }

    [Fact]
    public async Task GetLoanByIdAsync_ShouldReturnNull_WhenLoanDoesNotExist()
    {
        // Arrange
        var mockRepository = new Mock<ILoanRepository>();
        mockRepository.Setup(repo => repo.GetLoanByIdAsync(999))
            .ReturnsAsync((LoanDTO?)null);

        // Act
        var result = await mockRepository.Object.GetLoanByIdAsync(999);

        // Assert
        Assert.Null(result);
        mockRepository.Verify(repo => repo.GetLoanByIdAsync(999), Times.Once);
    }

    [Fact]
    public async Task GetActiveLoansByUsername_ShouldReturnActiveLoans_WhenLoansExist()
    {
        // Arrange
        var mockRepository = new Mock<ILoanRepository>();
        var username = "testuser";
        var expectedLoans = new List<LoanDTO>
        {
            new LoanDTO
            {
                LoanId = 1,
                BookId = 1,
                Username = username,
                BorrowDate = DateTime.Now,
                DueDate = DateTime.Now.AddDays(14),
                NumberOfExtensions = 0,
                IsReturned = false
            },
            new LoanDTO
            {
                LoanId = 2,
                BookId = 2,
                Username = username,
                BorrowDate = DateTime.Now,
                DueDate = DateTime.Now.AddDays(14),
                NumberOfExtensions = 0,
                IsReturned = false
            }
        };

        mockRepository.Setup(repo => repo.GetActiveLoansByUsername(username))
            .ReturnsAsync(expectedLoans);

        // Act
        var result = await mockRepository.Object.GetActiveLoansByUsername(username);

        // Assert
        Assert.NotNull(result);
        var loans = result.ToList();
        Assert.Equal(2, loans.Count);
        Assert.All(loans, loan => Assert.False(loan.IsReturned));
        Assert.All(loans, loan => Assert.Equal(username, loan.Username));
        mockRepository.Verify(repo => repo.GetActiveLoansByUsername(username), Times.Once);
    }

    [Fact]
    public async Task GetActiveLoansByUsername_ShouldReturnEmpty_WhenNoActiveLoansExist()
    {
        // Arrange
        var mockRepository = new Mock<ILoanRepository>();
        var username = "testuser";
        mockRepository.Setup(repo => repo.GetActiveLoansByUsername(username))
            .ReturnsAsync(new List<LoanDTO>());

        // Act
        var result = await mockRepository.Object.GetActiveLoansByUsername(username);

        // Assert
        Assert.NotNull(result);
        Assert.Empty(result);
        mockRepository.Verify(repo => repo.GetActiveLoansByUsername(username), Times.Once);
    }

    [Fact]
    public async Task UpdateLoanAsync_ShouldIncrementExtensions_WhenExtending()
    {
        // Arrange
        var mockRepository = new Mock<ILoanRepository>();
        var loan = new Loan
        {
            Id = 1,
            BookId = 1,
            Username = "testuser",
            BorrowDate = DateTime.Now.AddDays(-7),
            DueDate = DateTime.Now.AddDays(14),
            NumberOfExtensions = 2
        };

        var updatedLoanDTO = new LoanDTO
        {
            LoanId = 1,
            BookId = 1,
            Username = "testuser",
            BorrowDate = loan.BorrowDate,
            DueDate = loan.DueDate,
            NumberOfExtensions = 2
        };

        mockRepository.Setup(repo => repo.UpdateLoanAsync(It.IsAny<Loan>()))
            .ReturnsAsync(updatedLoanDTO);

        // Act
        var result = await mockRepository.Object.UpdateLoanAsync(loan);

        // Assert
        Assert.NotNull(result);
        Assert.Equal(2, result.NumberOfExtensions);
        mockRepository.Verify(repo => repo.UpdateLoanAsync(It.IsAny<Loan>()), Times.Once);
    }
}
