using DTOs.Loan;
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
        // Get user and book entities for the relationship
        var user = await context.User.FindAsync(loan.Username);
        var book = await context.Book.FindAsync(loan.BookId);
        if (user == null)
            throw new KeyNotFoundException($"User with username '{loan.Username}' not found.");
        if (book == null)
            throw new KeyNotFoundException($"Book with ID {loan.BookId} not found.");

        var entityEntry = await context.Loan.AddAsync(loan);
        await context.SaveChangesAsync();

        return new LoanDTO
        {
            LoanId = entityEntry.Entity.Id.ToString(),
            BorrowDate = entityEntry.Entity.BorrowDate,
            DueDate = entityEntry.Entity.DueDate,
            IsReturned = entityEntry.Entity.IsReturned,
            NumberOfExtensions = entityEntry.Entity.NumberOfExtensions,
            Username = entityEntry.Entity.Username,
            BookId = entityEntry.Entity.BookId.ToString()
        };
    }
}
