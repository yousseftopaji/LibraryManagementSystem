using System;
using System.ComponentModel.DataAnnotations.Schema;

namespace Entities;

public class Loan
{
    public int Id { get; set; }
    public required DateTime BorrowDate { get; set; }
    public required DateTime DueDate { get; set; }
    public required bool IsReturned { get; set; }
    public required int NumberOfExtensions { get; set; }
    public required string Username { get; set; }
    public required int BookId { get; set; }

    [ForeignKey("Username")]
    public User? User { get; set; }
    public Book? Book { get; set; }
}
