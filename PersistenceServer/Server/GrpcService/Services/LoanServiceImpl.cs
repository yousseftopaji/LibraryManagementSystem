using GrpcService.Protos;
using Grpc.Core;
using RepositoryContracts;
using Entities;

namespace GrpcService.Services;

public class LoanServiceImpl : LoanService.LoanServiceBase
{
    private readonly ILoanRepository loanRepository;
    private readonly IBookRepository bookRepository;

   public LoanServiceImpl(ILoanRepository loanRepository, IBookRepository bookRepository)
    {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
    }

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
//    public override async Task<ExtendLoanResponse> ExtendLoan(ExtendLoanRequest request, ServerCallContext context)
//     {
//         var response = new ExtendLoanResponse();
//         try
//         {
//             var loan = await bookRepository.GetBookAsync(request.LoanId);
//             if (loan == null)
//             {
//                 response.Success = false;
//                 response.Message = "Loan not found.";
//                 return response;
//             }

//             // 2. Check username matches
//             if (!loan.Username.Equals(request.Username, StringComparison.OrdinalIgnoreCase))
//             {
//                 response.Success = false;
//                 response.Message = "Loan does not belong to this user.";
//                 return response;
//             }

//             if (!string.IsNullOrEmpty(loan.State) && loan.State.Equals("reserved", StringComparison.OrdinalIgnoreCase))
//             {
//                 response.Success = false;
//                 response.Message = "Cannot extend loan: book is reserved.";
//                 return response;
//             }

//             var loanDto = await loanRepository.GetLoanByUsernameAsync(request.Username, request.BookId);
//             if (loanDto == null)
//             {
//                 response.Success = false;
//                 response.Message = "Loan not found for user and book.";
//                 return response;
//             }

//             // Map DTO back to entity for update (only NumberOfExtensions and DueDate are relevant here)
//             var extensionPeriod = TimeSpan.FromDays(30); // Extend by 30 days
//             var loanEntity = new Loan
//             {
//                 Id = loanDto.LoanId,
//                 BorrowDate = loanDto.BorrowDate,
//                 DueDate = loanDto.DueDate.Add(extensionPeriod),
//                 Username = loanDto.Username ?? string.Empty,
//                 BookId = loanDto.BookId,
//                 NumberOfExtensions   = loanDto.NumberOfExtensions + 1
//             };

//             var updated = await loanRepository.UpdateLoanAsync(loanEntity);

//             response.Loan = new DTOLoan
//             {
//                 Id = updated.LoanId,
//                 BorrowDate = updated.BorrowDate.ToString("yyyy-MM-dd"),
//                 DueDate = updated.DueDate.ToString("yyyy-MM-dd"),
//                 Username = updated.Username ?? string.Empty,
//                 BookId = updated.BookId,
//                 NumberOfExtensions = updated.NumberOfExtensions
//             };
//             response.Success = true;
//             response.Message = "Loan extended successfully.";
//         }
//         catch (Exception ex)
//         {
//             response.Success = false;
//             response.Message = $"Error extending loan: {ex.Message}";
//         }
//         return response;
//     }


public override async Task<ExtendLoanResponse> ExtendLoan(ExtendLoanRequest request, ServerCallContext context)
{
    var response = new ExtendLoanResponse();

    try
    {
        // 1. Get loan DTO by LoanId
        var loanDto = await loanRepository.GetLoanByIdAsync(request.LoanId);
        if (loanDto == null)
        {
            response.Success = false;
            response.Message = "Loan not found.";
            return response;
        }

        // 2. Check username matches
        if (!loanDto.Username.Equals(request.Username, StringComparison.OrdinalIgnoreCase))
        {
            response.Success = false;
            response.Message = "Loan does not belong to this user.";
            return response;
        }

        // 3. Get book entity
        var book = await bookRepository.GetBookAsync(loanDto.BookId);
        if (book.State.Equals("reserved", StringComparison.OrdinalIgnoreCase))
        {
            response.Success = false;
            response.Message = "Book is reserved and cannot be extended.";
            return response;
        }

        // 4. Map DTO to entity for update
        var loanEntity = new Loan
        {
            Id = loanDto.LoanId,
            BorrowDate = loanDto.BorrowDate,
            DueDate = loanDto.DueDate, // extend by 30 days
            Username = loanDto.Username,
            BookId = loanDto.BookId,
            NumberOfExtensions = loanDto.NumberOfExtensions
        };

        // 5. Update loan via repository
        var updatedDto = await loanRepository.UpdateLoanAsync(loanEntity);

        // 6. Map updated DTO to response
        response.Loan = new DTOLoan
        {
            Id = updatedDto.LoanId,
            BorrowDate = updatedDto.BorrowDate.ToString("yyyy-MM-dd"),
            DueDate = updatedDto.DueDate.ToString("yyyy-MM-dd"),
            Username = updatedDto.Username,
            BookId = updatedDto.BookId,
            NumberOfExtensions = updatedDto.NumberOfExtensions
        };

        response.Success = true;
        response.Message = "Loan extended successfully.";
    }
    catch (Exception ex)
    {
        response.Success = false;
        response.Message = $"Error extending loan: {ex.Message}";
    }

    return response;
}


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
            }));

            response.Success = true;
            response.Message = "Loans retrieved successfully.";
        }
        catch (Exception ex)
        {
            response.Loans.Clear();
            response.Success = false;
            response.Message = $"Error retrieving loans: {ex.Message}";
        }

        return response;
    }

public override async Task<GetLoanByIdResponse> GetLoanById(GetLoanByIdRequest request, ServerCallContext context)
{
    var response = new GetLoanByIdResponse();

    try
    {
        var loanDto = await loanRepository.GetLoanByIdAsync(request.Id);

        if (loanDto == null)
        {
            response.Success = false;
            response.Message = "Loan not found.";
            return response;
        }

        response.Loan = new DTOLoan
        {
            Id = loanDto.LoanId,
            BorrowDate = loanDto.BorrowDate.ToString("yyyy-MM-dd"),
            DueDate = loanDto.DueDate.ToString("yyyy-MM-dd"),
            Username = loanDto.Username,
            BookId = loanDto.BookId,
            NumberOfExtensions = loanDto.NumberOfExtensions,
            // IsReturned = loanDto.IsReturned
        };

        response.Success = true;
        response.Message = "Loan retrieved successfully.";
    }
    catch (Exception ex)
    {
        response.Success = false;
        response.Message = $"Error retrieving loan: {ex.Message}";
    }

    return response;
}

}