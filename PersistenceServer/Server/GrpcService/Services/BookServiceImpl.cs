using GrpcService.Protos;
using Grpc.Core;
using GrpcService.DatabaseService;
using DTOs;

namespace GrpcService.Services;

public class BookServiceImpl : BookService.BookServiceBase
{
    private readonly ILogger<BookServiceImpl> _logger;
    private readonly DBService dbService;

    public BookServiceImpl(ILogger<BookServiceImpl> logger, DBService dbService)
    {
        _logger = logger;
        this.dbService = dbService;
    }


    public async override Task<GetAllBooksResponse> GetAllBooks(GetAllBooksRequest request, ServerCallContext context)
    {
        _logger.LogInformation("Received request to get all books.");

        var booksFromDb = await dbService.GetAllBooksAsync();
       
        if (booksFromDb == null)
        {
            throw new RpcException(new Status(StatusCode.NotFound, $"Books not found."));
        }

        var response = new GetAllBooksResponse();

        response.Books.AddRange(booksFromDb.Select(b => new DTOBook
        {
            Title = b.Title ?? string.Empty,
            Author = b.Author ?? string.Empty,
            Isbn = b.ISBN ?? string.Empty,
            State = b.State ?? string.Empty
        }));

        return response;
    }

    public async override Task<GetIsbnBooksResponse> GetBooksByIsbn(GetIsbnBooksRequest request, ServerCallContext context)
    {
        _logger.LogInformation($"Received request to get book by ISBN {request.Isbn}.");

        var booksFromDb = await dbService.GetBooksByIsbnAsync(request.Isbn);

        if (booksFromDb == null)
        {
            throw new RpcException(new Status(StatusCode.NotFound, $"Books with ISBN {request.Isbn} not found."));
        }

        var response = new GetIsbnBooksResponse();

        response.Books.AddRange(booksFromDb.Select(b => new DTOBook
        {
            Title = b.Title ?? string.Empty,
            Author = b.Author ?? string.Empty,
            Isbn = b.ISBN ?? string.Empty,
            State = b.State ?? string.Empty
        }));

        return response;
    }

    public async override Task<CreateLoanResponse> CreateLoan(CreateLoanRequest request, ServerCallContext context)
    {
        _logger.LogInformation("Creating loan for book {BookId}", request.BookId);

        await dbService.CreateLoanAsync(request.BookId);

        return new CreateLoanResponse
        {
            Success = true,
            Message = $"Loan created successfully. Return in 30 days from now."
        };

    }
}
