using DTOs.Loan;
using EFCDatabaseRepositories.DBContext;
using Entities;
using RepositoryContracts;

namespace EFCDatabaseRepositories.Repositories;

public class EfcLoanRepository(LibraryDbContext context) : ILoanRepository
{
    public async Task<LoanDTO> CreateLoanAsync(Loan loan)
    {
        var entityEntry = await context.Loan.AddAsync(loan);
        await context.SaveChangesAsync();
        
        return new LoanDTO
        {
            LoanId = entityEntry.Entity.Id,
            BookId = entityEntry.Entity.BookId,
            Username = entityEntry.Entity.Username,
            BorrowDate = entityEntry.Entity.BorrowDate,
            DueDate = entityEntry.Entity.DueDate
        };
    }
}
