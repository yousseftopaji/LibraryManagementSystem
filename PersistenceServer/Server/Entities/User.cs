using System;

namespace Entities;

public class User
{
    public required string Username { get; set; }
    public required string PasswordHash { get; set; }
    public required string Role { get; set; }
    public required string Name { get; set; }
    public required string PhoneNumber { get; set; }
    public required List<Loan> Loans { get; set; }
}
