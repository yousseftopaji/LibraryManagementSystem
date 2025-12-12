using System;
namespace DTOs.Reservation;

public class CreateReservationDTO
{
    public required string Username { get; set; }
    public required string BookISBN { get; set; }
}