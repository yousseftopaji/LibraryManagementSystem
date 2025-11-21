namespace Entities;

public class Reserve
{
    public int Id { get; set; }
    public required DateTime ReserveDate { get; set; }
    public required string Username { get; set; }
    public required int BookId { get; set; }

    public User? User { get; set; }
    public Book? Book { get; set; }
}