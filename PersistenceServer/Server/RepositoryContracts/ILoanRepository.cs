using System;
using DTOs.Loan;
using Entities;

namespace RepositoryContracts;

public interface ILoanRepository
{
    Task<LoanDTO> CreateLoanAsync(Loan loan);
}
