using System;
namespace DTOs.Reservation;
public class CreateReservationDTO
{
    public DateTime ReservationDate { get; set; }
    public string? Username { get; set; }
    public int BookId { get; set; }
}
