using System;
using System.Text.Json.Serialization;
namespace DTOs.Reserve;
public class ReserveDTO
{
    [JsonPropertyName("id")]
    [JsonNumberHandling(JsonNumberHandling.AllowReadingFromString)]
    public int ReserveId { get; set; }
    [JsonPropertyName("reserveDate")]
    public DateTime ReserveDate { get; set; }
    [JsonPropertyName("username")]
    public string? Username { get; set; }
    [JsonPropertyName("bookId")]
    [JsonNumberHandling(JsonNumberHandling.AllowReadingFromString)]
    public int BookId { get; set; }
    [JsonPropertyName("status")]
    public string? Status { get; set; }
}
