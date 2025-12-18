using System;
using System.Collections.Generic;
using DTOs.Loan;

namespace BlazorApp.Services.LoanService;

public interface ILoanService
{
    public Task<LoanDTO> CreateLoanAsync(CreateLoanDTO createLoanDto);
    Task ExtendLoanAsync(int loanId);
    Task<List<LoanDTO>> GetActiveLoansAsync();
}