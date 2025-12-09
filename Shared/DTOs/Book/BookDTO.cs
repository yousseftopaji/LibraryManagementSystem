using System.Text.Json.Serialization;

namespace DTOs.Book;

public class BookDTO
{
    
    [JsonPropertyName("id")]
    [JsonNumberHandling(JsonNumberHandling.AllowReadingFromString)]
    public int BookId { get; set; }

    [JsonPropertyName("title")]
    public required string Title { get; set; }

    [JsonPropertyName("author")]
    public required string Author { get; set; }

    [JsonPropertyName("isbn")]
    public required string ISBN { get; set; }

    [JsonPropertyName("state")]
    public required string State { get; set; }
    
    [JsonPropertyName("genres")]
    public required List<GenreDTO> Genre { get; set; }
}
