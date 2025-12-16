using DTOs;
using DTOs.Book;
using Moq;
using RepositoryContracts;

namespace Server.Tests;

public class BookRepositoryTests
{
    [Fact]
    public async Task GetBookAsync_ShouldReturnBookDTO_WhenBookExists()
    {
        // Arrange
        var mockRepository = new Mock<IBookRepository>();
        var expectedBook = new BookDTO
        {
            BookId = 1,
            ISBN = "978-3-16-148410-0",
            Author = "Test Author",
            Title = "Test Book",
            State = "Available",
            Genre = new List<GenreDTO>
            {
                new GenreDTO { Name = "Fiction" },
                new GenreDTO { Name = "Adventure" }
            }
        };

        mockRepository.Setup(repo => repo.GetBookAsync(1))
            .ReturnsAsync(expectedBook);

        // Act
        var result = await mockRepository.Object.GetBookAsync(1);

        // Assert
        Assert.NotNull(result);
        Assert.Equal(1, result.BookId);
        Assert.Equal("978-3-16-148410-0", result.ISBN);
        Assert.Equal("Test Author", result.Author);
        Assert.Equal("Test Book", result.Title);
        Assert.Equal("Available", result.State);
        Assert.Equal(2, result.Genre.Count);
        Assert.Contains(result.Genre, g => g.Name == "Fiction");
        Assert.Contains(result.Genre, g => g.Name == "Adventure");
        mockRepository.Verify(repo => repo.GetBookAsync(1), Times.Once);
    }

    [Fact]
    public async Task GetBookAsync_ShouldReturnNull_WhenBookDoesNotExist()
    {
        // Arrange
        var mockRepository = new Mock<IBookRepository>();
        mockRepository.Setup(repo => repo.GetBookAsync(999))
            .ReturnsAsync((BookDTO?)null);

        // Act
        var result = await mockRepository.Object.GetBookAsync(999);

        // Assert
        Assert.Null(result);
        mockRepository.Verify(repo => repo.GetBookAsync(999), Times.Once);
    }

    [Fact]
    public async Task GetAllBooksAsync_ShouldReturnAllBooks_WhenBooksExist()
    {
        // Arrange
        var mockRepository = new Mock<IBookRepository>();
        var expectedBooks = new List<BookDTO>
        {
            new BookDTO
            {
                BookId = 1,
                ISBN = "978-3-16-148410-0",
                Author = "Author 1",
                Title = "Book 1",
                State = "Available",
                Genre = new List<GenreDTO> { new GenreDTO { Name = "Fiction" } }
            },
            new BookDTO
            {
                BookId = 2,
                ISBN = "978-3-16-148410-1",
                Author = "Author 2",
                Title = "Book 2",
                State = "Borrowed",
                Genre = new List<GenreDTO> { new GenreDTO { Name = "Fiction" } }
            }
        };

        mockRepository.Setup(repo => repo.GetAllBooksAsync())
            .ReturnsAsync(expectedBooks);

        // Act
        var result = await mockRepository.Object.GetAllBooksAsync();

        // Assert
        Assert.NotNull(result);
        var books = result.ToList();
        Assert.Equal(2, books.Count);
        Assert.Contains(books, b => b.Title == "Book 1");
        Assert.Contains(books, b => b.Title == "Book 2");
        mockRepository.Verify(repo => repo.GetAllBooksAsync(), Times.Once);
    }

    [Fact]
    public async Task GetAllBooksAsync_ShouldReturnEmpty_WhenNoBooksExist()
    {
        // Arrange
        var mockRepository = new Mock<IBookRepository>();
        mockRepository.Setup(repo => repo.GetAllBooksAsync())
            .ReturnsAsync(new List<BookDTO>());

        // Act
        var result = await mockRepository.Object.GetAllBooksAsync();

        // Assert
        Assert.NotNull(result);
        Assert.Empty(result);
        mockRepository.Verify(repo => repo.GetAllBooksAsync(), Times.Once);
    }

    [Fact]
    public async Task GetBooksByIsbnAsync_ShouldReturnBooks_WhenBooksExist()
    {
        // Arrange
        var mockRepository = new Mock<IBookRepository>();
        var isbn = "978-3-16-148410-0";
        var expectedBooks = new List<BookDTO>
        {
            new BookDTO
            {
                BookId = 1,
                ISBN = isbn,
                Author = "Test Author",
                Title = "Test Book Copy 1",
                State = "Available",
                Genre = new List<GenreDTO> { new GenreDTO { Name = "Fiction" } }
            },
            new BookDTO
            {
                BookId = 2,
                ISBN = isbn,
                Author = "Test Author",
                Title = "Test Book Copy 2",
                State = "Borrowed",
                Genre = new List<GenreDTO> { new GenreDTO { Name = "Fiction" } }
            }
        };

        mockRepository.Setup(repo => repo.GetBooksByIsbnAsync(isbn))
            .ReturnsAsync(expectedBooks);

        // Act
        var result = await mockRepository.Object.GetBooksByIsbnAsync(isbn);

        // Assert
        Assert.NotNull(result);
        var books = result.ToList();
        Assert.Equal(2, books.Count);
        Assert.All(books, b => Assert.Equal(isbn, b.ISBN));
        Assert.Contains(books, b => b.Title == "Test Book Copy 1");
        Assert.Contains(books, b => b.Title == "Test Book Copy 2");
        mockRepository.Verify(repo => repo.GetBooksByIsbnAsync(isbn), Times.Once);
    }

    [Fact]
    public async Task GetBooksByIsbnAsync_ShouldReturnEmpty_WhenNoBooksExist()
    {
        // Arrange
        var mockRepository = new Mock<IBookRepository>();
        var isbn = "978-3-16-148410-0";
        mockRepository.Setup(repo => repo.GetBooksByIsbnAsync(isbn))
            .ReturnsAsync(new List<BookDTO>());

        // Act
        var result = await mockRepository.Object.GetBooksByIsbnAsync(isbn);

        // Assert
        Assert.NotNull(result);
        Assert.Empty(result);
        mockRepository.Verify(repo => repo.GetBooksByIsbnAsync(isbn), Times.Once);
    }

    [Fact]
    public async Task UpdateBookStateAsync_ShouldUpdateState_WhenBookExists()
    {
        // Arrange
        var mockRepository = new Mock<IBookRepository>();
        var updatedBook = new BookDTO
        {
            BookId = 1,
            ISBN = "978-3-16-148410-0",
            Author = "Test Author",
            Title = "Test Book",
            State = "Borrowed",
            Genre = new List<GenreDTO> { new GenreDTO { Name = "Fiction" } }
        };

        mockRepository.Setup(repo => repo.UpdateBookStateAsync(1, "Borrowed"))
            .ReturnsAsync(updatedBook);

        // Act
        var result = await mockRepository.Object.UpdateBookStateAsync(1, "Borrowed");

        // Assert
        Assert.NotNull(result);
        Assert.Equal("Borrowed", result.State);
        Assert.Equal(1, result.BookId);
        mockRepository.Verify(repo => repo.UpdateBookStateAsync(1, "Borrowed"), Times.Once);
    }

    [Fact]
    public async Task UpdateBookStateAsync_ShouldThrowException_WhenBookDoesNotExist()
    {
        // Arrange
        var mockRepository = new Mock<IBookRepository>();
        mockRepository.Setup(repo => repo.UpdateBookStateAsync(999, "Borrowed"))
            .ThrowsAsync(new ArgumentException("Book not found"));

        // Act & Assert
        await Assert.ThrowsAsync<ArgumentException>(
            () => mockRepository.Object.UpdateBookStateAsync(999, "Borrowed"));
        mockRepository.Verify(repo => repo.UpdateBookStateAsync(999, "Borrowed"), Times.Once);
    }

    [Fact]
    public async Task UpdateBookStateAsync_ShouldUpdateToReserved_WhenValidState()
    {
        // Arrange
        var mockRepository = new Mock<IBookRepository>();
        var updatedBook = new BookDTO
        {
            BookId = 1,
            ISBN = "978-3-16-148410-0",
            Author = "Test Author",
            Title = "Test Book",
            State = "Reserved",
            Genre = new List<GenreDTO> { new GenreDTO { Name = "Fiction" } }
        };

        mockRepository.Setup(repo => repo.UpdateBookStateAsync(1, "Reserved"))
            .ReturnsAsync(updatedBook);

        // Act
        var result = await mockRepository.Object.UpdateBookStateAsync(1, "Reserved");

        // Assert
        Assert.NotNull(result);
        Assert.Equal("Reserved", result.State);
        mockRepository.Verify(repo => repo.UpdateBookStateAsync(1, "Reserved"), Times.Once);
    }

    [Fact]
    public async Task GetBookAsync_ShouldReturnBookWithEmptyGenres_WhenBookHasNoGenres()
    {
        // Arrange
        var mockRepository = new Mock<IBookRepository>();
        var expectedBook = new BookDTO
        {
            BookId = 1,
            ISBN = "978-3-16-148410-0",
            Author = "Test Author",
            Title = "Test Book",
            State = "Available",
            Genre = new List<GenreDTO>()
        };

        mockRepository.Setup(repo => repo.GetBookAsync(1))
            .ReturnsAsync(expectedBook);

        // Act
        var result = await mockRepository.Object.GetBookAsync(1);

        // Assert
        Assert.NotNull(result);
        Assert.NotNull(result.Genre);
        Assert.Empty(result.Genre);
        mockRepository.Verify(repo => repo.GetBookAsync(1), Times.Once);
    }

    [Fact]
    public async Task GetBooksByIsbnAsync_ShouldReturnBooksWithMultipleGenres()
    {
        // Arrange
        var mockRepository = new Mock<IBookRepository>();
        var isbn = "978-3-16-148410-0";
        var expectedBooks = new List<BookDTO>
        {
            new BookDTO
            {
                BookId = 1,
                ISBN = isbn,
                Author = "Test Author",
                Title = "Test Book",
                State = "Available",
                Genre = new List<GenreDTO>
                {
                    new GenreDTO { Name = "Fiction" },
                    new GenreDTO { Name = "Mystery" },
                    new GenreDTO { Name = "Thriller" }
                }
            }
        };

        mockRepository.Setup(repo => repo.GetBooksByIsbnAsync(isbn))
            .ReturnsAsync(expectedBooks);

        // Act
        var result = await mockRepository.Object.GetBooksByIsbnAsync(isbn);

        // Assert
        Assert.NotNull(result);
        var books = result.ToList();
        Assert.Single(books);
        Assert.Equal(3, books[0].Genre.Count);
        mockRepository.Verify(repo => repo.GetBooksByIsbnAsync(isbn), Times.Once);
    }
}

