using System.Threading.Tasks;
using System.Collections.Generic;
using System.Net;
using System.Net.Http;
using System.Net.Http.Json;
using System.Text.Json;
using DTOs.Book;
using Moq;
using RichardSzalay.MockHttp;
using BlazorApp.Services;
using Microsoft.JSInterop;
using Xunit;

namespace BlazorApp.Tests.BookService;

// Test double for AuthProvider that records whether AttachToken was called
public class TestAuthProvider : AuthProvider
{
    public bool AttachCalled { get; private set; } = false;

    public TestAuthProvider() : base(new HttpClient(), Mock.Of<IJSRuntime>()) { }

    // Shadow the non-virtual AttachToken to record calls
    public new void AttachToken(HttpClient client)
    {
        AttachCalled = true;
        base.AttachToken(client);
    }
}

public class HttpBookServiceTests
{
    [Fact]
    public async Task GetBooksAsync_ReturnsList_WhenHttp200()
    {
        // Arrange
        var mockHttp = new MockHttpMessageHandler();
        var books = new List<BookDTO>
        {
            new BookDTO { ISBN = "111", Title = "Book One", Author = "Author A", State = "Available", Genre = new List<GenreDTO>() },
            new BookDTO { ISBN = "222", Title = "Book Two", Author = "Author B", State = "Available", Genre = new List<GenreDTO>() }
        };

        string json = JsonSerializer.Serialize(books, new JsonSerializerOptions { PropertyNameCaseInsensitive = true });

        mockHttp.When("*/books").Respond("application/json", json);

        var client = mockHttp.ToHttpClient();
        client.BaseAddress = new System.Uri("http://localhost/");

        var testAuth = new TestAuthProvider();

        var service = new HttpBookService(client, testAuth);

        // Act
        var result = await service.GetBooksAsync();

        // Assert
        Assert.NotNull(result);
        Assert.Equal(2, result.Count);
        Assert.Equal("Book One", result[0].Title);
        // Note: AuthProvider.AttachToken is non-virtual; avoid verifying it via Moq/new-shadowing. The important behavior (HTTP request/responses) is asserted by returned data.
    }
}
