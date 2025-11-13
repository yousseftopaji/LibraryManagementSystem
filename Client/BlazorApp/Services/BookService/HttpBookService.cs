using System;
using System.Net.Http.Json;
using System.Text.Json;
using DTOs;

namespace BlazorApp.Services;

public class HttpBookService : IBookService
{
    private readonly HttpClient client;

    public HttpBookService(HttpClient client)
    {
        this.client = client;
    }

    public async Task<BookDTO> GetBookAsync(string isbn)
    {
        HttpResponseMessage httpResponse = await client.GetAsync($"books/{isbn}");
        string response = await httpResponse.Content.ReadAsStringAsync();
        if (!httpResponse.IsSuccessStatusCode)
        {
            throw new Exception(response);
        }

        // Server returns a List<BookDTO>, so deserialize as list first
        var bookList = JsonSerializer.Deserialize<List<BookDTO>>(response, JsonOptions());

        if (bookList == null || bookList.Count == 0)
        {
            throw new Exception($"No book found with ISBN: {isbn}");
        }

        // Get the first book as representative and count available copies
        var representativeBook = bookList[0];
        var availableCount = bookList.Count(b => b.State.Equals("Available", StringComparison.OrdinalIgnoreCase));

        // Update the noOfCopies to reflect available copies
        representativeBook.NoOfCopies = availableCount;

        return representativeBook;
    }

    public async Task<List<BookDTO>> GetBooksAsync()
    {
        HttpResponseMessage httpResponse = await client.GetAsync("books");
        string response = await httpResponse.Content.ReadAsStringAsync();
        if (!httpResponse.IsSuccessStatusCode)
        {
            throw new Exception(response);
        }
        return JsonSerializer.Deserialize<List<BookDTO>>(response, JsonOptions())!;
    }

    private JsonSerializerOptions? JsonOptions()
    {
        return new JsonSerializerOptions { PropertyNameCaseInsensitive = true };
    }
}
