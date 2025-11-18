using GrpcService.Protos;
using Grpc.Core;
using RepositoryContracts;
using Entities;
using DTOs.Loan;

namespace GrpcService.Services;

public class LoanServiceImpl(ILoanRepository loanRepository) : LoanService.LoanServiceBase
{
    public override async Task<CreateLoanResponse> CreateLoan(CreateLoanRequest request, ServerCallContext context)
    {
        var response = new CreateLoanResponse();

        try
        {
            var loan = new Loan
            {
                BorrowDate = DateTime.Parse(request.BorrowDate),
                DueDate = DateTime.Parse(request.DueDate),
                Username = request.Username,
                BookId = request.BookId
            };
            // Create loan using repository
            var createdLoan = await loanRepository.CreateLoanAsync(loan);

            // Populate response
            response.Loan.Id = createdLoan.LoanId;
            response.Loan.BorrowDate = createdLoan.BorrowDate.ToString("yyyy-MM-dd");
            response.Loan.DueDate = createdLoan.DueDate.ToString("yyyy-MM-dd");
            response.Loan.Username = createdLoan.Username ?? string.Empty;
            response.Loan.BookId = createdLoan.BookId;
            response.Success = true;
            response.Message = "Loan created successfully.";
        }
        catch (Exception ex)
        {
            response.Loan = null;
            response.Success = false;
            response.Message = $"Error creating loan: {ex.Message}";
        }

        return response;
    }
}

