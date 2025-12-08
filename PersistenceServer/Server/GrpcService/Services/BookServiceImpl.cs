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

        response.Books.AddRange(booksFromDb.Select(b =>
        {
            var dto = new DTOBook
            {
                Id = b.BookId,
                Title = b.Title,
                Author = b.Author,
                Isbn = b.ISBN,
                State = b.State,
            };
            dto.Genres.AddRange(b.Genre.Select(g => new DTOGenre
            {
                Name = g.Name
            }));
            return dto;
        }));
        response.Success = booksFromDb.Count > 0;
        response.Message = booksFromDb.Count > 0 ? "Books retrieved successfully." : "No books found.";
        return response;
    }

    public override async Task<GetBooksByIsbnResponse> GetBooksByIsbn(GetBooksByIsbnRequest request, ServerCallContext context)
    {
        var booksFromDb = (await bookRepository.GetBooksByIsbnAsync(request.Isbn)).ToList();
        var response = new GetBooksByIsbnResponse();
        response.Books.AddRange(booksFromDb.Select(b =>
        {
            var dto = new DTOBook
            {
                Id = b.BookId,
                Title = b.Title,
                Author = b.Author,
                Isbn = b.ISBN,
                State = b.State,
            };
            
            dto.Genres.AddRange(b.Genre.Select(g => new DTOGenre
            {
                Name = g.Name
            }));
            return dto;
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
            var dto = new DTOBook
            {
                Id = bookFromDb.BookId,
                Title = bookFromDb.Title,
                Author = bookFromDb.Author,
                Isbn = bookFromDb.ISBN,
                State = bookFromDb.State
            };

                dto.Genres.AddRange(bookFromDb.Genre.Select(g => new DTOGenre { Name = g.Name}));

            response.Book = dto;
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

            var dto = new DTOBook
            {
                Id = updatedBook.BookId,
                Title = updatedBook.Title,
                Author = updatedBook.Author,
                Isbn = updatedBook.ISBN,
                State = updatedBook.State
            };
                dto.Genres.AddRange(updatedBook.Genre.Select(g => new DTOGenre { Name = g.Name }));

            response.Book = dto;
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
