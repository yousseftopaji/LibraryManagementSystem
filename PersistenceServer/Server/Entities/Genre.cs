using System;

namespace Entities;

public class Genre
{
    public required string Name { get; set; }
    public required List<Book> Books { get; set; }
}
