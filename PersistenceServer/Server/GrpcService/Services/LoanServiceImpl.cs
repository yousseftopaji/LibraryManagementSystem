using GrpcService.Protos;
using Grpc.Core;
using RepositoryContracts;
using Entities;

namespace GrpcService.Services;

public class LoanServiceImpl(ILoanRepository loanRepository)
    : LoanService.LoanServiceBase
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
                Username = createdLoan.Username,
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
    
public override async Task<ExtendLoanResponse> ExtendLoan(ExtendLoanRequest request, ServerCallContext context)
{
    var response = new ExtendLoanResponse();

    try
    {
        //Map DTOLoan to Loan entity
        var loan = new Loan
        {
            Id = request.Loan.Id,
            BorrowDate = DateTime.Parse(request.Loan.BorrowDate),
            DueDate = DateTime.Parse(request.Loan.DueDate),
            Username = request.Loan.Username,
            BookId = request.Loan.BookId,
            NumberOfExtensions = request.Loan.NumberOfExtensions,
            IsReturned = request.Loan.IsReturned
        };
        
        var loanDto = await loanRepository.UpdateLoanAsync(loan);
        
        //Map from LoanDTO back to DTOLoan
        response.Loan = new DTOLoan
        {
            Id = loanDto.LoanId,
            BorrowDate = loanDto.BorrowDate.ToString("yyyy-MM-dd"),
            DueDate = loanDto.DueDate.ToString("yyyy-MM-dd"),
            Username = loanDto.Username,
            BookId = loanDto.BookId,
            NumberOfExtensions = loanDto.NumberOfExtensions,
            IsReturned = loanDto.IsReturned
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
                Id = l.LoanId,
                BorrowDate = l.BorrowDate.ToString("yyyy-MM-dd"),
                DueDate = l.DueDate.ToString("yyyy-MM-dd"),
                Username = l.Username,
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
            IsReturned = loanDto.IsReturned
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

 public override async Task<GetActiveLoansByUsernameResponse> GetActiveLoansByUsername(GetActiveLoansByUsernameRequest request, ServerCallContext context)
    {
        var response = new GetActiveLoansByUsernameResponse();

        try
        {
            var activeLoans = await loanRepository.GetActiveLoansByUsername(request.Username);

            response.ActiveLoans.AddRange(activeLoans.Select(l => new DTOLoan
            {
                Id = l.LoanId,
                BorrowDate = l.BorrowDate.ToString("yyyy-MM-dd"),
                DueDate = l.DueDate.ToString("yyyy-MM-dd"),
                Username = l.Username,
                BookId = l.BookId,
                NumberOfExtensions = l.NumberOfExtensions,
                IsReturned = l.IsReturned
            }));

            response.Success = true;
            response.Message = "Active loans retrieved successfully.";
        }
        catch (Exception ex)
        {
            response.Success = false;
            response.Message = $"Error retrieving active loans: {ex.Message}";
        }

        return response;
    }
}