using DTOs.Reservation;
using EFCDatabaseRepositories.DBContext;
using Entities;
using Microsoft.EntityFrameworkCore;
using RepositoryContracts;

namespace EFCDatabaseRepositories.Repositories;

public class EfcReservationRepository(LibraryDbContext context) : IReservationRepository
{
    public async Task<ReservationDTO> CreateReservationAsync(Reservation reservation)
    {
        var entityEntry = await context.Reservation.AddAsync(reservation);
        await context.SaveChangesAsync();
        
        return new ReservationDTO
        {
            ReservationId = entityEntry.Entity.Id,
            BookId = entityEntry.Entity.BookId,
            Username = entityEntry.Entity.Username,
            ReservationDate = entityEntry.Entity.ReservationDate
        };
    }

    public async Task<int> GetReservationCountByIsbnAsync(string isbn)
    {
        var count = await context.Reservation
            .Where(r => r.Book != null && r.Book.ISBN == isbn)
            .CountAsync();
        
        return count;
    }
}

