using System;
using System.Text.Json.Serialization;

namespace DTOs.Loan;

public class LoanDTO
{
    [JsonPropertyName("id")]
    public int LoanId { get; set; }

    [JsonPropertyName("borrowDate")]
    public DateTime BorrowDate { get; set; }

    [JsonPropertyName("dueDate")]
    public DateTime DueDate { get; set; }

    [JsonPropertyName("username")]
    public string? Username { get; set; }

    [JsonPropertyName("bookId")]
    public int BookId { get; set; }
}

