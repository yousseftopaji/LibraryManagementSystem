using GrpcService.Protos;
using Grpc.Core;
using RepositoryContracts;

namespace GrpcService.Services;

public class BookServiceImpl(IBookRepository bookRepository) : BookService.BookServiceBase
{
    public override async Task<GetAllBooksResponse> GetAllBooks(GetAllBooksRequest request, ServerCallContext context)
    {
        var booksFromDb = (await bookRepository.GetAllBooksAsync()).ToList();
        var response = new GetAllBooksResponse();
        response.Books.AddRange(booksFromDb.Select(b => new DTOBook
        {
            Id = int.Parse(b.BookId),
            Title = b.Title ?? string.Empty,
            Author = b.Author ?? string.Empty,
            Isbn = b.ISBN ?? string.Empty,
            State = b.State
        }));
        response.Success = booksFromDb.Count > 0;
        response.Message = booksFromDb.Count > 0 ? "Books retrieved successfully." : "No books found.";
        return response;
    }

    public override async Task<GetBooksByIsbnResponse> GetBooksByIsbn(GetBooksByIsbnRequest request, ServerCallContext context)
    {
        var booksFromDb = (await bookRepository.GetBooksByIsbnAsync(request.Isbn)).ToList();
        var response = new GetBooksByIsbnResponse();
        response.Books.AddRange(booksFromDb.Select(b => new DTOBook
        {
            Id = int.Parse(b.BookId),
            Title = b.Title ?? string.Empty,
            Author = b.Author ?? string.Empty,
            Isbn = b.ISBN ?? string.Empty,
            State = b.State
        }));
        response.Success = booksFromDb.Count > 0;
        response.Message = booksFromDb.Count > 0 ? $"Books with ISBN {request.Isbn} retrieved successfully." : $"No books found with ISBN {request.Isbn}.";
        return response;
    }

    public override async Task<GetBookByIdResponse> GetBookById(GetBookByIdRequest request, ServerCallContext context)
    {
        var bookFromDb = await bookRepository.GetBookAsync(request.Id);
        var response = new GetBookByIdResponse();
        if (bookFromDb != null)
        {
            response.Book = new DTOBook
            {
                Id = int.Parse(bookFromDb.BookId),
                Title = bookFromDb.Title,
                Author = bookFromDb.Author,
                Isbn = bookFromDb.ISBN,
                State = bookFromDb.State
            };
            response.Success = true;
            response.Message = $"Book with ID {request.Id} retrieved successfully.";
        }
        else
        {
            response.Book = null;
            response.Success = false;
            response.Message = $"Book with ID {request.Id} not found.";
        }
        return response;
    }
    
    public override async Task<UpdateBookStateResponse> UpdateBookState(UpdateBookStateRequest request, ServerCallContext context)
    {
        var response = new UpdateBookStateResponse();
        try
        {
            var updatedBook = await bookRepository.UpdateBookStateAsync(request.Id, request.State);
           
                response.Book = new DTOBook
                {
                    Id = int.Parse(updatedBook.BookId),
                    Title = updatedBook.Title ?? string.Empty,
                    Author = updatedBook.Author ?? string.Empty,
                    Isbn = updatedBook.ISBN ?? string.Empty,
                    State = updatedBook.State
                };
                response.Success = true;
                response.Message = $"Book state updated successfully for ID {request.Id}.";
        }
        catch (Exception ex)
        {
            response.Book = null;
            response.Success = false;
            response.Message = $"Error updating book state: {ex.Message}";
        }
        return response;
    }
}
