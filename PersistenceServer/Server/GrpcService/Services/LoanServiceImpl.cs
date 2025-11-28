using GrpcService.Protos;
using Grpc.Core;
using RepositoryContracts;
using Entities;

namespace GrpcService.Services;

public class LoanServiceImpl(ILoanRepository loanRepository, IBookRepository bookRepository) : LoanService.LoanServiceBase
{
    private readonly ILoanRepository _loanRepository = loanRepository;
    private readonly IBookRepository _bookRepository = bookRepository;

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
            var createdLoan = await _loanRepository.CreateLoanAsync(loan);

            // Populate response
            response.Loan = new DTOLoan
            {
                Id = createdLoan.LoanId,
                BorrowDate = createdLoan.BorrowDate.ToString("yyyy-MM-dd"),
                DueDate = createdLoan.DueDate.ToString("yyyy-MM-dd"),
                Username = createdLoan.Username ?? string.Empty,
                BookId = createdLoan.BookId,
                NumberOfExtensions = createdLoan.NumberOfExtensions
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

<<<<<<< Updated upstream
    public override async Task<ExtendLoanResponse> ExtendLoan(ExtendLoanRequest request, ServerCallContext context)
    {
        var response = new ExtendLoanResponse();
        try
        {
            var book = await _bookRepository.GetBookAsync(request.BookId);
            if (book == null)
            {
                response.Success = false;
                response.Message = "Book not found.";
                return response;
            }

            if (!string.IsNullOrEmpty(book.State) && book.State.Equals("reserved", StringComparison.OrdinalIgnoreCase))
            {
                response.Success = false;
                response.Message = "Cannot extend loan: book is reserved.";
                return response;
            }

            var loanDto = await _loanRepository.GetLoanByUsernameAsync(request.Username, request.BookId);
            if (loanDto == null)
            {
                response.Success = false;
                response.Message = "Loan not found for user and book.";
                return response;
            }

            // Map DTO back to entity for update (only NumberOfExtensions and DueDate are relevant here)
            var extensionPeriod = TimeSpan.FromDays(30); // Extend by 30 days
            var loanEntity = new Loan
            {
                Id = loanDto.LoanId,
                BorrowDate = loanDto.BorrowDate,
                DueDate = loanDto.DueDate.Add(extensionPeriod),
                Username = loanDto.Username ?? string.Empty,
                BookId = loanDto.BookId,
                NumberOfExtensions = loanDto.NumberOfExtensions + 1
            };

            var updated = await _loanRepository.UpdateLoanAsync(loanEntity);

            response.Loan = new DTOLoan
            {
                Id = updated.LoanId,
                BorrowDate = updated.BorrowDate.ToString("yyyy-MM-dd"),
                DueDate = updated.DueDate.ToString("yyyy-MM-dd"),
                Username = updated.Username ?? string.Empty,
                BookId = updated.BookId,
                NumberOfExtensions = updated.NumberOfExtensions
            };
            response.Success = true;
            response.Message = "Loan extended successfully.";
        }
        catch (Exception ex)
        {
            response.Success = false;
            response.Message = $"Error extending loan: {ex.Message}";
=======
     public override async Task<GetLoansByISBNResponse> GetLoansByISBN(GetLoansByISBNRequest request, ServerCallContext context)
    {
        var response = new GetLoansByISBNResponse();

        try
        {
            var loans = await loanRepository.GetLoansByIsbnAsync(request.Isbn);

            response.Loans.AddRange(loans.Select(l => new DTOLoan
            {
                Id = l.Id,
                BorrowDate = l.BorrowDate.ToString("yyyy-MM-dd"),
                DueDate = l.DueDate.ToString("yyyy-MM-dd"),
                Username = l.Username ?? string.Empty,
                BookId = l.BookId
                // IsReturned = l.IsReturned
            }));

            response.Success = true;
            response.Message = "Loans retrieved successfully.";
        }
        catch (Exception ex)
        {
            response.Loans.Clear();
            response.Success = false;
            response.Message = $"Error retrieving loans: {ex.Message}";
>>>>>>> Stashed changes
        }

        return response;
    }
}
