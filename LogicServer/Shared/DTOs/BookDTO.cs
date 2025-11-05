using System;

namespace DTOs;

public class BookDTO
{
 public int BookId { get; set; }
    public string? ISBN { get; set; }
    public string? Title { get; set; }
    public string? Author { get; set; }
    public int NoOfCopies { get; set; }
    public required string State { get; set; }

}
