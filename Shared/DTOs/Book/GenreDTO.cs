using System.Text.Json.Serialization;

namespace DTOs.Book;

public class GenreDTO
{
    [JsonPropertyName("name")]
    public required string Name { get; set; }
}