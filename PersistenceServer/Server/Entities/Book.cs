namespace Entities;

public class Book
{
    public int Id { get; set; }
    public required string ISBN { get; set; }
    public required string Author { get; set; }
    public required string Title { get; set; }
    public required string State { get; set; }
    public required List<Genre> Genre { get; set; }
    public required List<Loan> Loans { get; set; }
}
