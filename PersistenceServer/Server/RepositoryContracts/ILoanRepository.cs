using System;
using DTOs.Loan;
using Entities;

namespace RepositoryContracts;

public interface ILoanRepository
{
    Task<LoanDTO> CreateLoanAsync(Loan loan);

    // Get a loan by username and book id (returns null if not found)
    Task<LoanDTO?> GetLoanByUsernameAsync(string username, int bookId);

    // Update an existing loan (used for extending)
    Task<LoanDTO> UpdateLoanAsync(Loan loan);
}
