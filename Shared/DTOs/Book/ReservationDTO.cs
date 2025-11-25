using System;
using System.Text.Json.Serialization;

namespace DTOs.Book
{
    public class ReservationDTO
    {
        [JsonPropertyName("Isbn")]
        public string Isbn { get; set; } = string.Empty;

        [JsonPropertyName("bookId")]
        public Guid BookId { get; set; }

        [JsonPropertyName("username")]
        public string Username { get; set; } = string.Empty;

        [JsonPropertyName("createdAt")]
        public DateTime CreatedAt { get; set; }

        [JsonPropertyName("expiresAt")]
        public DateTime? ExpiresAt { get; set; }

        [JsonPropertyName("status")]
        public string Status { get; set; } = string.Empty;

        [JsonPropertyName("queuePosition")]
        public int QueuePosition { get; set; }
    }
}