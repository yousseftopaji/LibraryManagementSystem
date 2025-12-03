using System.Text.Json.Serialization;

namespace DTOs.Reservation;

public class ReservationDTO
{
    [JsonPropertyName("id")]
    public int ReservationId { get; set; }

    [JsonPropertyName("reservationDate")]
    public DateTime ReservationDate { get; set; }

    [JsonPropertyName("username")]
    public string? Username { get; set; }

    [JsonPropertyName("bookId")]
    public int BookId { get; set; }
}