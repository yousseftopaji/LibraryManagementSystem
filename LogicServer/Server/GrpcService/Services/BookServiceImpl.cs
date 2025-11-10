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

    public async override Task<GetBookResponse> GetBookById(GetBookRequest request, ServerCallContext context)
    {
        _logger.LogInformation("Received request to get book by id.");

        var bookFromDb = await dbService.GetBookByIdAsync(request.Id);

        if (bookFromDb == null)
        {
            throw new RpcException(new Status(StatusCode.NotFound, $"Book with id {request.Id} not found."));
        }

        var dtoBook = new DTOBook
        {
            Title = bookFromDb.Title ?? string.Empty,
            Author = bookFromDb.Author ?? string.Empty,
            Isbn = bookFromDb.ISBN ?? string.Empty,
            State = bookFromDb.State ?? string.Empty
        };

        return new GetBookResponse { Book = dtoBook };
    }

    public async override Task<CreateLoanResponse> CreateLoan(CreateLoanRequest request, ServerCallContext context)
    {
        _logger.LogInformation("Creating loan for book {BookId}", request.BookId);

        await dbService.CreateLoanAsync(request.BookId);

        return new CreateLoanResponse
        {
            Message = $"Loan created successfully. Return in 30 days from now."
        };

    }
}
