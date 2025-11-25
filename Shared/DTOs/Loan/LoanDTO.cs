using System;
using System.Text.Json.Serialization;

namespace DTOs.Loan;

public class LoanDTO
{
    [JsonPropertyName("id")]
    [JsonNumberHandling(JsonNumberHandling.AllowReadingFromString)]
    public Guid LoanId { get; set; }

    [JsonPropertyName("borrowDate")] public DateTime BorrowDate { get; set; }

    [JsonPropertyName("dueDate")] public DateTime DueDate { get; set; }

    [JsonPropertyName("username")] public string? Username { get; set; }

    [JsonPropertyName("bookId")]
    [JsonNumberHandling(JsonNumberHandling.AllowReadingFromString)]
    public Guid BookId { get; set; }

    [JsonPropertyName("extensionCount")] public int ExtensionCount { get; set; }

    [JsonPropertyName("status")] public string Status { get; set; } = string.Empty;
}


