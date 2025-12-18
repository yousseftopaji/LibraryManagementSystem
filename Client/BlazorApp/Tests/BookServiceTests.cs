using System.Net;
using System.Net.Http.Json;
using System.Text.Json;
using BlazorApp.Services;
using DTOs.Book;
using Xunit;

public class BookServiceTests
{
    [Fact]
    public async Task GetBooksAsync_Returns_List()
    {
        var books = new List<BookDTO> {
            new BookDTO { BookId = 1, Title = "A", Author = "Auth", ISBN = "isbn1", State = "Available", Genre = new List<DTOs.Book.GenreDTO>() }
        };

        var handler = new FakeHttpMessageHandler(req =>
        {
            if (req.RequestUri!.AbsolutePath.EndsWith("/books") || req.RequestUri!.AbsolutePath.EndsWith("books"))
            {
                return new HttpResponseMessage(HttpStatusCode.OK)
                {
                    Content = JsonContent.Create(books, options: new JsonSerializerOptions { PropertyNameCaseInsensitive = true })
                };
            }
            return new HttpResponseMessage(HttpStatusCode.NotFound);
        });

        var client = new HttpClient(handler) { BaseAddress = new Uri("http://localhost/") };
        var auth = TestAuthHelpers.CreateAuthProvider();
        var service = new HttpBookService(client, auth);

        var result = await service.GetBooksAsync();

        Assert.NotNull(result);
        Assert.Single(result);
        Assert.Equal("isbn1", result[0].ISBN);
    }

    [Fact]
    public async Task GetBookAsync_Returns_Book()
    {
        var book = new BookDTO { BookId = 2, Title = "B", Author = "AuthB", ISBN = "isbn2", State = "Available", Genre = new List<DTOs.Book.GenreDTO>() };

        var handler = new FakeHttpMessageHandler(req =>
        {
            if (req.RequestUri!.AbsolutePath.EndsWith("/books/isbn2") || req.RequestUri!.AbsolutePath.EndsWith("books/isbn2"))
            {
                return new HttpResponseMessage(HttpStatusCode.OK)
                {
                    Content = JsonContent.Create(book, options: new JsonSerializerOptions { PropertyNameCaseInsensitive = true })
                };
            }
            return new HttpResponseMessage(HttpStatusCode.NotFound);
        });

        var client = new HttpClient(handler) { BaseAddress = new Uri("http://localhost/") };
        var auth = TestAuthHelpers.CreateAuthProvider();
        var service = new HttpBookService(client, auth);

        var result = await service.GetBookAsync("isbn2");

        Assert.NotNull(result);
        Assert.Equal("isbn2", result.ISBN);
    }
}

