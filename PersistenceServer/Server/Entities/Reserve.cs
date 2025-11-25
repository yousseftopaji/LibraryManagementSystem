using System;
using System.ComponentModel.DataAnnotations.Schema;

namespace Entities;

public class Reserve
{
    public int Id { get; set; }
    public required DateTime ReserveDate { get; set; }
    public required string Username { get; set; }
    public required int BookId { get; set; }
    public required string Status { get; set; }

    [ForeignKey("Username")]
    public User? User { get; set; }
    public Book? Book { get; set; }
}

