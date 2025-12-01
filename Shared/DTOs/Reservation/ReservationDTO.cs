using System;
using System.Text.Json.Serialization;
namespace DTOs.Reservation;
public class ReservationDTO
{
    [JsonPropertyName("id")]
    [JsonNumberHandling(JsonNumberHandling.AllowReadingFromString)]
    public int ReservationId { get; set; }
    [JsonPropertyName("reservationDate")]
    public DateTime ReservationDate { get; set; }
    [JsonPropertyName("username")]
    public string? Username { get; set; }
    [JsonPropertyName("bookId")]
    [JsonNumberHandling(JsonNumberHandling.AllowReadingFromString)]
    public int BookId { get; set; }
}
