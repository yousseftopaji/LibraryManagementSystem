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
            Id = b.BookId ?? string.Empty,
            Title = b.Title ?? string.Empty,
            Author = b.Author ?? string.Empty,
            Isbn = b.ISBN ?? string.Empty,
            State = b.State ?? string.Empty
        }));
        return response;
    }

    public async override Task<GetBookByIsbnResponse> GetBookByIsbn(GetBookByIsbnRequest request, ServerCallContext context)
    {
        _logger.LogInformation($"Received request to get book by ISBN: {request.Isbn}");

        var bookFromDb = await dbService.GetBookByIsbnAsync(request.Isbn);

        GetBookByIsbnResponse response = new GetBookByIsbnResponse();

        if (bookFromDb != null)
        {
            response.Book = new DTOBook
            {
                Id = bookFromDb.BookId ?? string.Empty,
                Title = bookFromDb.Title ?? string.Empty,
                Author = bookFromDb.Author ?? string.Empty,
                Isbn = bookFromDb.ISBN ?? string.Empty,
                State = bookFromDb.State ?? string.Empty
            };
        }

        return response;
    }
}
