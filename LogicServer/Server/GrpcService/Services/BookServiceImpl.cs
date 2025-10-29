using GrpcService.Protos;
using Grpc.Core;

namespace GrpcService.Services;

public class BookServiceImpl : BookService.BookServiceBase
{
    private readonly ILogger<BookServiceImpl> _logger;

    public BookServiceImpl(ILogger<BookServiceImpl> logger)
    {
        _logger = logger;
    }

    public override Task<GetAllBooksResponse> GetAllBooks(GetAllBooksRequest request, ServerCallContext context)
    {
        _logger.LogInformation("Received request to get all books.");

        // Example data. Replace with your actual data source.
        var books = new List<DTOBook>
        {
            new() { Title = "Book 1", Author = "Author 1", Isbn = "1234567890", State = "Available" },
            new() { Title = "Book 2", Author = "Author 2", Isbn = "0987654321", State = "CheckedOut" }
        };

        var response = new GetAllBooksResponse();
        response.Books.AddRange(books);

        return Task.FromResult(response);
    }
}
