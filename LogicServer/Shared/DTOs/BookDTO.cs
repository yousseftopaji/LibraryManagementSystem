using System;
using System.Text.Json.Serialization;

namespace DTOs;

public class BookDTO
{
    [JsonPropertyName("id")]
    public string? BookId { get; set; }

    [JsonPropertyName("isbn")]
    public string? ISBN { get; set; }

    [JsonPropertyName("title")]
    public string? Title { get; set; }

    [JsonPropertyName("author")]
    public string? Author { get; set; }

    [JsonPropertyName("noOfCopies")]
    public int NoOfCopies { get; set; }

    [JsonPropertyName("state")]
    public required string State { get; set; }

}
