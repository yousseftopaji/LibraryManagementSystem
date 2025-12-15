using System;
using DTOs.Loan;
using Entities;

namespace RepositoryContracts;

public interface ILoanRepository
{
    Task<LoanDTO> CreateLoanAsync(Loan loan);

    // Get a loan by username and loan id (returns null if not found)
    Task<LoanDTO?> GetLoanByUsernameAsync(string username, int loanId);
    Task<LoanDTO?> GetLoanByIdAsync(int loanId);
    // Update an existing loan (used for extending)
    Task<LoanDTO> UpdateLoanAsync(Loan loan);
    Task<IEnumerable<LoanDTO>> GetLoansByIsbnAsync(string isbn);
    Task<IEnumerable<LoanDTO>> GetActiveLoansByUsername(string username);

}
