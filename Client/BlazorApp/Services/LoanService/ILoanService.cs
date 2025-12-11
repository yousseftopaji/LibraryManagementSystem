using System;
using DTOs.Loan;

namespace BlazorApp.Services.LoanService;

public interface ILoanService
{
    public Task<LoanDTO> CreateLoanAsync(CreateLoanDTO createLoanDTO);
    public Task<LoanDTO> ExtendLoanAsync(ExtendLoanDTO extendLoanDTO);
}
