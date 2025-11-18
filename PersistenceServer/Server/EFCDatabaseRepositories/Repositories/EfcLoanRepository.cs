using DTOs.Loan;
using EFCDatabaseRepositories.DBContext;
using Entities;
using Microsoft.EntityFrameworkCore;
using RepositoryContracts;

namespace EFCDatabaseRepositories;

public class EfcLoanRepository : ILoanRepository
{
    private readonly LibraryDbContext context;

    public EfcLoanRepository(LibraryDbContext context)
    {
        this.context = context;
    }

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
