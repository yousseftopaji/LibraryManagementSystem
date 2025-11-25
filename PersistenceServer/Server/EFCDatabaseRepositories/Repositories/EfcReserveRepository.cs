using DTOs.Reserve;
using EFCDatabaseRepositories.DBContext;
using Entities;
using RepositoryContracts;

namespace EFCDatabaseRepositories.Repositories;

public class EfcReserveRepository(LibraryDbContext context) : IReserveRepository
{
    public async Task<ReserveDTO> CreateReserveAsync(Reserve reserve)
    {
        var entityEntry = await context.Reserve.AddAsync(reserve);
        await context.SaveChangesAsync();
        
        return new ReserveDTO
        {
            ReserveId = entityEntry.Entity.Id,
            BookId = entityEntry.Entity.BookId,
            Username = entityEntry.Entity.Username,
            ReserveDate = entityEntry.Entity.ReserveDate,
            Status = entityEntry.Entity.Status
        };
    }
}

