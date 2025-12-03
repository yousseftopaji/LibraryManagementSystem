package dk.via.sep3.shared.mapper;

import dk.via.sep3.DTOBook;
import dk.via.sep3.model.domain.Book;
import dk.via.sep3.shared.book.BookDTO;

public interface BookMapper
{
  BookDTO toDto(Book book);
  Book toDomain(BookDTO bookDTO);
  Book toDomain(DTOBook dtoBook);
  dk.via.sep3.DTOBook toProto(Book book);
}
