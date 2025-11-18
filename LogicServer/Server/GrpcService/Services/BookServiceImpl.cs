using GrpcService.Protos;
using Grpc.Core;
using GrpcService.DatabaseService;

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

        GetAllBooksResponse response = new GetAllBooksResponse();

        response.Books.AddRange(booksFromDb.Select(b => new DTOBook
        {
            Id = int.TryParse(b.BookId, out var id) ? id : 0,
            Title = b.Title ?? string.Empty,
            Author = b.Author ?? string.Empty,
            Isbn = b.ISBN ?? string.Empty,
            State = b.State ?? string.Empty
        }));
        return response;
    }

    public async override Task<GetBooksByIsbnResponse> GetBooksByIsbn(GetBooksByIsbnRequest request, ServerCallContext context)
    {
        _logger.LogInformation($"Received request to get books by ISBN: {request.Isbn}");

        var booksFromDb = await dbService.GetBooksByIsbnAsync(request.Isbn);

        GetBooksByIsbnResponse response = new GetBooksByIsbnResponse();

        if (booksFromDb != null && booksFromDb.Any())
        {
            response.Books.AddRange(booksFromDb.Select(b => new DTOBook
            {
                Id = int.TryParse(b.BookId, out var id) ? id : 0,
                Title = b.Title ?? string.Empty,
                Author = b.Author ?? string.Empty,
                Isbn = b.ISBN ?? string.Empty,
                State = b.State ?? string.Empty
            }));
            response.Success = true;
            response.Message = $"Found {booksFromDb.Count()} book(s) with ISBN {request.Isbn}";
        }
        else
        {
            response.Success = false;
            response.Message = $"No books found with ISBN {request.Isbn}";
        }

        return response;
    }
}
