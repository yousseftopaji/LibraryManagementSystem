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
        _logger.LogInformation($"Received request to create loan for ISBN: {request.BookISBN}, Username: {request.Username}");

        try
        {
            var loanFromDb = await dbService.CreateLoanAsync(request.BookISBN, request.Username);

            CreateLoanResponse response = new CreateLoanResponse
            {
                LoanId = loanFromDb.LoanId ?? string.Empty,
                BookId = loanFromDb.BookId ?? string.Empty,
                Isbn = loanFromDb.ISBN ?? string.Empty,
                UserId = loanFromDb.UserId ?? string.Empty,
                LoanDate = loanFromDb.LoanDate.ToString("o"), // ISO 8601 format
                DueDate = loanFromDb.DueDate.ToString("o")
            };

            _logger.LogInformation($"Loan created successfully with ID: {response.LoanId}");
            return response;
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, $"Error creating loan for ISBN: {request.BookISBN}");
            throw new RpcException(new Status(StatusCode.Internal, $"Failed to create loan: {ex.Message}"));
        }
    }
}

