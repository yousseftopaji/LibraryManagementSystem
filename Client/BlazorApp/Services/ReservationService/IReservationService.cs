using System;
using DTOs.Reservation;

namespace BlazorApp.Services.ReservationService;

public interface IReservationService
{
public Task<ReservationDTO> CreateReservationAsync(CreateReservationDTO createReservationDTO);
}
