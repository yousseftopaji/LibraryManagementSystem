using System;
using DTOs.User;

namespace DTOs.Loan;

public class CreateLoanDTO
{
    public required string BookISBN { get; set; }
    public required string Username { get; set; }
}
