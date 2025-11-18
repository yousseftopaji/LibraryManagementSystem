using System;

namespace DTOs.Loan;

public class LoanResponseDTO
{
    public string? LoanId { get; set; }
    public string? BookId { get; set; }
    public string? Isbn { get; set; }
    public string? UserId { get; set; }
    public DateTime LoanDate { get; set; }
    public DateTime DueDate { get; set; }
}
