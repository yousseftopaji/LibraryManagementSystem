using GrpcService.Protos;
using Grpc.Core;
using RepositoryContracts;
using Entities;

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
            response.Loan = new DTOLoan
            {
                Id = createdLoan.LoanId,
                BorrowDate = createdLoan.BorrowDate.ToString("yyyy-MM-dd"),
                DueDate = createdLoan.DueDate.ToString("yyyy-MM-dd"),
                Username = createdLoan.Username ?? string.Empty,
                BookId = createdLoan.BookId
            };
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
