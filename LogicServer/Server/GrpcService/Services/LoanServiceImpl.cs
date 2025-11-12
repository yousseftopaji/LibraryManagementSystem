using GrpcService.Protos;
using Grpc.Core;
using GrpcService.DatabaseService;

namespace GrpcService.Services;

public class LoanServiceImpl : LoanService.LoanServiceBase
{
    private readonly ILogger<LoanServiceImpl> _logger;
    private readonly DBService dbService;

    public LoanServiceImpl(ILogger<LoanServiceImpl> logger, DBService dbService)
    {
        _logger = logger;
        this.dbService = dbService;
    }

    public async override Task<CreateLoanResponse> CreateLoan(CreateLoanRequest request, ServerCallContext context)
    {
        _logger.LogInformation($"Received request to create loan for user: {request.Username}, bookId: {request.BookId}");

        try
        {
            var loanFromDb = await dbService.CreateLoanAsync(request.Username, request.BookId, request.LoanDurationDays);

            CreateLoanResponse response = new CreateLoanResponse();

            if (loanFromDb != null)
            {
                response.Loan = new DTOLoan
                {
                    Id = loanFromDb.LoanId ?? string.Empty,
                    BorrowDate = loanFromDb.BorrowDate.ToString("yyyy-MM-dd"),
                    DueDate = loanFromDb.DueDate.ToString("yyyy-MM-dd"),
                    IsReturned = loanFromDb.IsReturned,
                    NumberOfExtensions = loanFromDb.NumberOfExtensions,
                    Username = loanFromDb.Username ?? string.Empty,
                    BookId = loanFromDb.BookId ?? string.Empty
                };
                response.Success = true;
                response.Message = "Loan created successfully";
            }
            else
            {
                response.Success = false;
                response.Message = "Book is not available for loan";
            }

            return response;
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error creating loan");
            return new CreateLoanResponse
            {
                Success = false,
                Message = $"Error creating loan: {ex.Message}"
            };
        }
    }
}

