using System;
using DTOs.Loan;

namespace BlazorApp.Services.LoanService;

public interface ILoanService
{
    public Task<LoanDTO> CreateLoanAsync(CreateLoanDTO createLoanDTO);
    Task<bool> ExtendLoanAsync(int loanId);
}
