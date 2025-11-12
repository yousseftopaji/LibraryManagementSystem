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

        // Log books with loan status
        _logger.LogInformation("=================================================");
        _logger.LogInformation($"Total books in database: {booksFromDb.Count}");
        foreach (var book in booksFromDb)
        {
            _logger.LogInformation($"Book ID: {book.BookId}, ISBN: {book.ISBN}, Title: {book.Title}, State: {book.State}");
        }
        _logger.LogInformation("=================================================");

        GetAllBooksResponse response = new GetAllBooksResponse();

        response.Books.AddRange(booksFromDb.Select(b => new DTOBook
        {
            Id = b.BookId ?? string.Empty,
            Title = b.Title ?? string.Empty,
            Author = b.Author ?? string.Empty,
            Isbn = b.ISBN ?? string.Empty,
            State = b.State ?? string.Empty
        }));
        return response;
    }

    public async override Task<GetBookResponse> GetBook(GetBookRequest request, ServerCallContext context)
    {
        _logger.LogInformation($"Received request to get book with ISBN: {request.Isbn}");

        var bookFromDb = await dbService.GetBookByISBNAsync(request.Isbn);

        if (bookFromDb == null)
        {
            throw new RpcException(new Status(StatusCode.NotFound, $"Book with ISBN {request.Isbn} not found"));
        }

        GetBookResponse response = new GetBookResponse
        {
            Book = new DTOBook
            {
                Id = bookFromDb.BookId ?? string.Empty,
                Title = bookFromDb.Title ?? string.Empty,
                Author = bookFromDb.Author ?? string.Empty,
                Isbn = bookFromDb.ISBN ?? string.Empty,
                State = bookFromDb.State ?? string.Empty
            }
        };

        return response;
    }
}
