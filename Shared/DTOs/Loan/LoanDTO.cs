using System;
using System.Text.Json.Serialization;

namespace DTOs.Loan;

public class LoanDTO
{
    [JsonPropertyName("id")]
    public string? LoanId { get; set; }

    [JsonPropertyName("borrowDate")]
    public DateTime BorrowDate { get; set; }

    [JsonPropertyName("dueDate")]
    public DateTime DueDate { get; set; }

    [JsonPropertyName("isReturned")]
    public bool IsReturned { get; set; }

    [JsonPropertyName("numberOfExtensions")]
    public int NumberOfExtensions { get; set; }

    [JsonPropertyName("username")]
    public string? Username { get; set; }

    [JsonPropertyName("bookId")]
    public string? BookId { get; set; }
}

