using DTOs.Loan;
using EFCDatabaseRepositories.DBContext;
using Entities;
using RepositoryContracts;
using Microsoft.EntityFrameworkCore;

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
            DueDate = entityEntry.Entity.DueDate,
            NumberOfExtensions = entityEntry.Entity.NumberOfExtensions
        };
    }

    public async Task<LoanDTO?> GetLoanByUsernameAsync(string username, int bookId)
    {
        var loan = await context.Loan
            .FirstOrDefaultAsync(l => l.Username == username && l.BookId == bookId);

        if (loan == null) return null;

        return new LoanDTO
        {
            LoanId = loan.Id,
            BookId = loan.BookId,
            Username = loan.Username,
            BorrowDate = loan.BorrowDate,
            DueDate = loan.DueDate,
            NumberOfExtensions = loan.NumberOfExtensions
        };
    }
  
    public async Task<LoanDTO> UpdateLoanAsync(Loan loan)
    {
        var existing = await context.Loan.FindAsync(loan.Id);
        if (existing == null) throw new ArgumentException("Loan not found");

        // update fields that can be updated
        existing.DueDate = loan.DueDate;
        existing.NumberOfExtensions = loan.NumberOfExtensions;

        await context.SaveChangesAsync();

        return new LoanDTO
        {
            LoanId = existing.Id,
            BookId = existing.BookId,
            Username = existing.Username,
            BorrowDate = existing.BorrowDate,
            DueDate = existing.DueDate,
            NumberOfExtensions = existing.NumberOfExtensions
        };
    }

     public async Task<IEnumerable<LoanDTO>> GetLoansByIsbnAsync(string isbn)
    {
        var loanDTOs = await (from loan in context.Loan
                              join book in context.Book
                              on loan.BookId equals book.Id
                              where book.ISBN == isbn
                              select new LoanDTO
                              {
                                  LoanId = loan.Id,
                                  BookId = loan.BookId,
                                  Username = loan.Username,
                                  BorrowDate = loan.BorrowDate,
                                  DueDate = loan.DueDate,
                                  NumberOfExtensions = loan.NumberOfExtensions,
                                  IsReturned = loan.IsReturned
                              })
                              .ToListAsync();
        return loanDTOs;
    }

    public async Task<LoanDTO?> GetLoanByIdAsync(int loanId)
{
    var loan = await context.Loan.FindAsync(loanId);

    if (loan == null) return null;

    return new LoanDTO
    {
        LoanId = loan.Id,
        BookId = loan.BookId,
        Username = loan.Username,
        BorrowDate = loan.BorrowDate,
        DueDate = loan.DueDate,
        NumberOfExtensions = loan.NumberOfExtensions
    };
}
   
    public Task UpdateLoanAsync(LoanDTO loan)
    {
        throw new NotImplementedException();
    }


    
    public async Task<IEnumerable<LoanDTO>> GetActiveLoansByUsername(string username)
    {
        var loans = await context.Loan
            .Where(l => l.Username == username && !l.IsReturned)
            .ToListAsync();

        return loans.Select(loan => new LoanDTO
        {
            LoanId = loan.Id,
            BookId = loan.BookId,
            Username = loan.Username,
            BorrowDate = loan.BorrowDate,
            DueDate = loan.DueDate,
            NumberOfExtensions = loan.NumberOfExtensions,
            IsReturned = loan.IsReturned
        });
    }
}
