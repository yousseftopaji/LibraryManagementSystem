using System;
using DTOs.Reservation;
using Entities;

namespace RepositoryContracts;

public interface IReservationRepository
{
    Task<ReservationDTO> CreateReservationAsync(Reservation reservation);
    Task<int> GetReservationCountByIsbnAsync(string isbn);
    Task<List<ReservationDTO>> GetReservationsByIsbnAsync(string isbn);
}

