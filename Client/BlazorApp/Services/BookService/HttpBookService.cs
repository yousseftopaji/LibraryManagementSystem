using System;
using System.Net.Http.Json;
using System.Text.Json;
using DTOs;
using DTOs.Book;

namespace BlazorApp.Services;

public class HttpBookService : IBookService
{
    private readonly HttpClient client;
    private readonly AuthProvider authProvider;
    public HttpBookService(HttpClient  client, AuthProvider authProvider)
    {
        this.client = client;
        this.authProvider = authProvider;
    }

    public async Task<BookDTO> GetBookAsync(string isbn)    
    {
        authProvider.AttachToken(client);
        HttpResponseMessage httpResponse = await client.GetAsync($"books/{isbn}");
        string response = await httpResponse.Content.ReadAsStringAsync();
        if (!httpResponse.IsSuccessStatusCode)
        {
            throw new Exception(response);
        }

        // Server returns a single BookDTO
        var book = JsonSerializer.Deserialize<BookDTO>(response, JsonOptions());

        if (book == null)
        {
            throw new Exception($"No book found with ISBN: {isbn}");
        }

        return book;
    }

    public async Task<List<BookDTO>> GetBooksAsync()
    {   
        authProvider.AttachToken(client);
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
