using DTOs.Reservation;
using Entities;
using Moq;
using RepositoryContracts;

namespace Server.Tests;

public class ReservationRepositoryTests
{
    [Fact]
    public async Task CreateReservationAsync_ShouldReturnReservationDTO_WhenReservationIsCreated()
    {
        // Arrange
        var mockRepository = new Mock<IReservationRepository>();
        var reservation = new Reservation
        {
            BookId = 1,
            Username = "testuser",
            ReservationDate = DateTime.Now
        };

        var expectedReservationDTO = new ReservationDTO
        {
            ReservationId = 1,
            BookId = 1,
            Username = "testuser",
            ReservationDate = reservation.ReservationDate
        };

        mockRepository.Setup(repo => repo.CreateReservationAsync(It.IsAny<Reservation>()))
            .ReturnsAsync(expectedReservationDTO);

        // Act
        var result = await mockRepository.Object.CreateReservationAsync(reservation);

        // Assert
        Assert.NotNull(result);
        Assert.Equal(1, result.ReservationId);
        Assert.Equal(1, result.BookId);
        Assert.Equal("testuser", result.Username);
        Assert.Equal(reservation.ReservationDate, result.ReservationDate);
        mockRepository.Verify(repo => repo.CreateReservationAsync(It.IsAny<Reservation>()), Times.Once);
    }

    [Fact]
    public async Task GetReservationCountByIsbnAsync_ShouldReturnCount_WhenReservationsExist()
    {
        // Arrange
        var mockRepository = new Mock<IReservationRepository>();
        var isbn = "978-3-16-148410-0";
        mockRepository.Setup(repo => repo.GetReservationCountByIsbnAsync(isbn))
            .ReturnsAsync(3);

        // Act
        var result = await mockRepository.Object.GetReservationCountByIsbnAsync(isbn);

        // Assert
        Assert.Equal(3, result);
        mockRepository.Verify(repo => repo.GetReservationCountByIsbnAsync(isbn), Times.Once);
    }

    [Fact]
    public async Task GetReservationCountByIsbnAsync_ShouldReturnZero_WhenNoReservationsExist()
    {
        // Arrange
        var mockRepository = new Mock<IReservationRepository>();
        var isbn = "978-3-16-148410-0";
        mockRepository.Setup(repo => repo.GetReservationCountByIsbnAsync(isbn))
            .ReturnsAsync(0);

        // Act
        var result = await mockRepository.Object.GetReservationCountByIsbnAsync(isbn);

        // Assert
        Assert.Equal(0, result);
        mockRepository.Verify(repo => repo.GetReservationCountByIsbnAsync(isbn), Times.Once);
    }

    [Fact]
    public async Task GetReservationsByIsbnAsync_ShouldReturnReservations_WhenReservationsExist()
    {
        // Arrange
        var mockRepository = new Mock<IReservationRepository>();
        var isbn = "978-3-16-148410-0";
        var expectedReservations = new List<ReservationDTO>
        {
            new ReservationDTO
            {
                ReservationId = 1,
                BookId = 1,
                Username = "user1",
                ReservationDate = DateTime.Now
            },
            new ReservationDTO
            {
                ReservationId = 2,
                BookId = 1,
                Username = "user2",
                ReservationDate = DateTime.Now.AddHours(-1)
            },
            new ReservationDTO
            {
                ReservationId = 3,
                BookId = 1,
                Username = "user3",
                ReservationDate = DateTime.Now.AddHours(-2)
            }
        };

        mockRepository.Setup(repo => repo.GetReservationsByIsbnAsync(isbn))
            .ReturnsAsync(expectedReservations);

        // Act
        var result = await mockRepository.Object.GetReservationsByIsbnAsync(isbn);

        // Assert
        Assert.NotNull(result);
        Assert.Equal(3, result.Count);
        Assert.Contains(result, r => r.Username == "user1");
        Assert.Contains(result, r => r.Username == "user2");
        Assert.Contains(result, r => r.Username == "user3");
        Assert.All(result, r => Assert.Equal(1, r.BookId));
        mockRepository.Verify(repo => repo.GetReservationsByIsbnAsync(isbn), Times.Once);
    }

    [Fact]
    public async Task GetReservationsByIsbnAsync_ShouldReturnEmpty_WhenNoReservationsExist()
    {
        // Arrange
        var mockRepository = new Mock<IReservationRepository>();
        var isbn = "978-3-16-148410-0";
        mockRepository.Setup(repo => repo.GetReservationsByIsbnAsync(isbn))
            .ReturnsAsync(new List<ReservationDTO>());

        // Act
        var result = await mockRepository.Object.GetReservationsByIsbnAsync(isbn);

        // Assert
        Assert.NotNull(result);
        Assert.Empty(result);
        mockRepository.Verify(repo => repo.GetReservationsByIsbnAsync(isbn), Times.Once);
    }

    [Fact]
    public async Task CreateReservationAsync_ShouldHandleMultipleReservationsForSameBook()
    {
        // Arrange
        var mockRepository = new Mock<IReservationRepository>();
        var reservation1 = new Reservation
        {
            BookId = 1,
            Username = "user1",
            ReservationDate = DateTime.Now
        };

        var reservation2 = new Reservation
        {
            BookId = 1,
            Username = "user2",
            ReservationDate = DateTime.Now.AddMinutes(5)
        };

        var expectedReservationDTO1 = new ReservationDTO
        {
            ReservationId = 1,
            BookId = 1,
            Username = "user1",
            ReservationDate = reservation1.ReservationDate
        };

        var expectedReservationDTO2 = new ReservationDTO
        {
            ReservationId = 2,
            BookId = 1,
            Username = "user2",
            ReservationDate = reservation2.ReservationDate
        };

        mockRepository.Setup(repo => repo.CreateReservationAsync(It.Is<Reservation>(r => r.Username == "user1")))
            .ReturnsAsync(expectedReservationDTO1);

        mockRepository.Setup(repo => repo.CreateReservationAsync(It.Is<Reservation>(r => r.Username == "user2")))
            .ReturnsAsync(expectedReservationDTO2);

        // Act
        var result1 = await mockRepository.Object.CreateReservationAsync(reservation1);
        var result2 = await mockRepository.Object.CreateReservationAsync(reservation2);

        // Assert
        Assert.NotNull(result1);
        Assert.NotNull(result2);
        Assert.Equal(1, result1.ReservationId);
        Assert.Equal(2, result2.ReservationId);
        Assert.Equal("user1", result1.Username);
        Assert.Equal("user2", result2.Username);
        mockRepository.Verify(repo => repo.CreateReservationAsync(It.IsAny<Reservation>()), Times.Exactly(2));
    }

    [Fact]
    public async Task GetReservationCountByIsbnAsync_ShouldReturnCorrectCount_WithMultipleReservations()
    {
        // Arrange
        var mockRepository = new Mock<IReservationRepository>();
        var isbn = "978-3-16-148410-0";
        mockRepository.Setup(repo => repo.GetReservationCountByIsbnAsync(isbn))
            .ReturnsAsync(5);

        // Act
        var result = await mockRepository.Object.GetReservationCountByIsbnAsync(isbn);

        // Assert
        Assert.Equal(5, result);
        mockRepository.Verify(repo => repo.GetReservationCountByIsbnAsync(isbn), Times.Once);
    }

    [Fact]
    public async Task GetReservationsByIsbnAsync_ShouldReturnReservationsOrderedByDate()
    {
        // Arrange
        var mockRepository = new Mock<IReservationRepository>();
        var isbn = "978-3-16-148410-0";
        var now = DateTime.Now;
        var expectedReservations = new List<ReservationDTO>
        {
            new ReservationDTO
            {
                ReservationId = 1,
                BookId = 1,
                Username = "user1",
                ReservationDate = now.AddDays(-2)
            },
            new ReservationDTO
            {
                ReservationId = 2,
                BookId = 1,
                Username = "user2",
                ReservationDate = now.AddDays(-1)
            },
            new ReservationDTO
            {
                ReservationId = 3,
                BookId = 1,
                Username = "user3",
                ReservationDate = now
            }
        };

        mockRepository.Setup(repo => repo.GetReservationsByIsbnAsync(isbn))
            .ReturnsAsync(expectedReservations);

        // Act
        var result = await mockRepository.Object.GetReservationsByIsbnAsync(isbn);

        // Assert
        Assert.NotNull(result);
        Assert.Equal(3, result.Count);
        Assert.Equal("user1", result[0].Username);
        Assert.Equal("user2", result[1].Username);
        Assert.Equal("user3", result[2].Username);
        mockRepository.Verify(repo => repo.GetReservationsByIsbnAsync(isbn), Times.Once);
    }

    [Fact]
    public async Task CreateReservationAsync_ShouldPreserveReservationDate()
    {
        // Arrange
        var mockRepository = new Mock<IReservationRepository>();
        var specificDate = new DateTime(2025, 12, 16, 10, 30, 0);
        var reservation = new Reservation
        {
            BookId = 1,
            Username = "dateuser",
            ReservationDate = specificDate
        };

        var expectedReservationDTO = new ReservationDTO
        {
            ReservationId = 1,
            BookId = 1,
            Username = "dateuser",
            ReservationDate = specificDate
        };

        mockRepository.Setup(repo => repo.CreateReservationAsync(It.IsAny<Reservation>()))
            .ReturnsAsync(expectedReservationDTO);

        // Act
        var result = await mockRepository.Object.CreateReservationAsync(reservation);

        // Assert
        Assert.NotNull(result);
        Assert.Equal(specificDate, result.ReservationDate);
        mockRepository.Verify(repo => repo.CreateReservationAsync(It.IsAny<Reservation>()), Times.Once);
    }

    [Fact]
    public async Task GetReservationsByIsbnAsync_ShouldHandleDifferentBooks()
    {
        // Arrange
        var mockRepository = new Mock<IReservationRepository>();
        var isbn1 = "978-3-16-148410-0";
        var isbn2 = "978-3-16-148410-1";

        var reservationsIsbn1 = new List<ReservationDTO>
        {
            new ReservationDTO { ReservationId = 1, BookId = 1, Username = "user1", ReservationDate = DateTime.Now }
        };

        var reservationsIsbn2 = new List<ReservationDTO>
        {
            new ReservationDTO { ReservationId = 2, BookId = 2, Username = "user2", ReservationDate = DateTime.Now },
            new ReservationDTO { ReservationId = 3, BookId = 2, Username = "user3", ReservationDate = DateTime.Now }
        };

        mockRepository.Setup(repo => repo.GetReservationsByIsbnAsync(isbn1))
            .ReturnsAsync(reservationsIsbn1);

        mockRepository.Setup(repo => repo.GetReservationsByIsbnAsync(isbn2))
            .ReturnsAsync(reservationsIsbn2);

        // Act
        var result1 = await mockRepository.Object.GetReservationsByIsbnAsync(isbn1);
        var result2 = await mockRepository.Object.GetReservationsByIsbnAsync(isbn2);

        // Assert
        Assert.Single(result1);
        Assert.Equal(2, result2.Count);
        mockRepository.Verify(repo => repo.GetReservationsByIsbnAsync(isbn1), Times.Once);
        mockRepository.Verify(repo => repo.GetReservationsByIsbnAsync(isbn2), Times.Once);
    }
}

