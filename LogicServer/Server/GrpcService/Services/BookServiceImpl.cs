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


}
