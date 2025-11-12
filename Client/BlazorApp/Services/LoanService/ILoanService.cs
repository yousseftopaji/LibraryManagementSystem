using System;
using DTOs.Loan;

namespace BlazorApp.Services.LoanService;

public interface ILoanService
{
    public Task<LoanResponseDTO> CreateLoanAsync(CreateLoanDTO createLoanDTO);
}
