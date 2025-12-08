using System;
using System.Text.Json.Serialization;

namespace DTOs.Loan;

public class LoanDTO
{
    [JsonPropertyName("id")]
    [JsonNumberHandling(JsonNumberHandling.AllowReadingFromString)]
    public int LoanId { get; set; }

    [JsonPropertyName("borrowDate")]
    public DateTime BorrowDate { get; set; }

    [JsonPropertyName("dueDate")]
    public DateTime DueDate { get; set; }

    [JsonPropertyName("username")]
    public required string Username { get; set; }

    [JsonPropertyName("bookId")]
    [JsonNumberHandling(JsonNumberHandling.AllowReadingFromString)]
    public int BookId { get; set; }

    [JsonPropertyName("numberOfExtensions")]
    public int NumberOfExtensions { get; set; }
    
    [JsonPropertyName("isReturned")]
    public bool IsReturned { get; set; }
}
