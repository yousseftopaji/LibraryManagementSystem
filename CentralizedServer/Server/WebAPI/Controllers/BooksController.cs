using Microsoft.AspNetCore.Mvc;
using RepositoryContracts;
using Entities;
using DTOs;

namespace WebAPI.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class BooksController : ControllerBase
    {
        private readonly IBookRepository bookRepo;

        public BooksController(IBookRepository bookRepo)
        {
            this.bookRepo = bookRepo;
        }

        //  Get many Books
        [HttpGet]

        public async Task<ActionResult<List<BookDTO>>> GetManyBooks()
        {
            IQueryable<Book> books = bookRepo.GetMany();

            List<BookDTO> dtos = books.Select(b => new BookDTO
            {
                BookId = b.BookId,
                ISBN = b.ISBN ?? string.Empty,
                Title = b.Title ?? string.Empty,
                Author = b.Author ?? string.Empty,
                NoOfCopies = b.NoOfCopies,
                State = b.State
            }).ToList();

            return Ok(dtos);
        }
    }
}
