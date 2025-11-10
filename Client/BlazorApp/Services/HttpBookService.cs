using System;
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

    public async Task<BookDTO> GetBookAsync(BookDTO book)
    {
        throw new NotImplementedException();
    }

    public async Task<List<BookDTO>> GetBooksAsync()
    {
        HttpResponseMessage httpResponse = await client.GetAsync("books");
        string response = await httpResponse.Content.ReadAsStringAsync();
        if (!httpResponse.IsSuccessStatusCode)
        {
            throw new Exception(response);
        }
        Console.WriteLine(response);
        return JsonSerializer.Deserialize<List<BookDTO>>(response, JsonOptions())!;
    }

    private JsonSerializerOptions? JsonOptions()
    {
        return new JsonSerializerOptions { PropertyNameCaseInsensitive = true };
    }
}
