using System;

namespace DTOs.Reserve;

public class CreateReserveDTO
{
    public DateTime ReserveDate { get; set; }
    public string? Username { get; set; }
    public int BookId { get; set; }
}

