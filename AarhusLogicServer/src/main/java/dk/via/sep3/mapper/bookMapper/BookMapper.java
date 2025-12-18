package dk.via.sep3.mapper.bookMapper;

import dk.via.sep3.DTOBook;
import dk.via.sep3.application.domain.Book;
import dk.via.sep3.DTOs.book.BookDTO;

public interface BookMapper
{
  BookDTO toDto(Book book);
  Book toDomain(BookDTO bookDTO);
  Book toDomain(DTOBook dtoBook);
  DTOBook toProto(Book book);
}
