package dk.via.sep3.mapper.bookMapper;

import dk.via.sep3.DTOBook;
import dk.via.sep3.DTOGenre;
import dk.via.sep3.DTOs.book.BookDTO;
import dk.via.sep3.application.domain.Book;
import dk.via.sep3.application.domain.Genre;
import dk.via.sep3.application.domain.State;
import dk.via.sep3.DTOs.book.GenreDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BookMapperImpl implements BookMapper
{
  @Override
  public BookDTO toDto(Book book)
  {
    return new BookDTO(book.getId(), book.getTitle(), book.getAuthor(),
        book.getIsbn(), book.getState().toString(), mapDomainToGenreDto(book.getGenres()));
  }

  @Override
  public Book toDomain(BookDTO bookDTO)
  {
    State state = State.valueOf(bookDTO.getState().toUpperCase());
    Book book = new Book();
    book.setId(bookDTO.getId());
    book.setIsbn(bookDTO.getIsbn());
    book.setTitle(bookDTO.getTitle());
    book.setAuthor(bookDTO.getAuthor());
    book.setState(state);
    book.setGenres(mapGenreDtoToDomain(bookDTO.getGenres()));
    return book;
  }

  @Override
  public Book toDomain(DTOBook dtoBook)
  {
    return new Book(dtoBook.getId(), dtoBook.getIsbn(), dtoBook.getTitle(),
        dtoBook.getAuthor(), State.valueOf(dtoBook.getState().toUpperCase()),
        mapDTOGenreToDomain(dtoBook.getGenresList()));
  }

  @Override
  public DTOBook toProto(Book book)
  {
    DTOBook.Builder builder = DTOBook.newBuilder().setId(book.getId())
        .setIsbn(book.getIsbn()).setTitle(book.getTitle())
        .setAuthor(book.getAuthor()).setState(book.getState().name());
    builder.addAllGenres(mapDomainToDTOGenre(book.getGenres()));
    return builder.build();
  }

  private List<GenreDTO> mapDomainToGenreDto(List<Genre> genres)
  {
    List<GenreDTO> genreDTOs = new ArrayList<>();
    for (Genre genre : genres)
    {
      genreDTOs.add(new GenreDTO(genre.getName()));
    }
    return genreDTOs;
  }

  private List<Genre> mapDTOGenreToDomain(List<DTOGenre> dtoGenres)
  {
    List<Genre> genres = new ArrayList<>();
    for (DTOGenre dtoGenre : dtoGenres)
    {
      genres.add(new Genre(dtoGenre.getName()));
    }
    return genres;
  }

  private List<DTOGenre> mapDomainToDTOGenre(List<Genre> genres)
  {
    List<DTOGenre> dtoGenres = new ArrayList<>();
    for (Genre genre : genres)
    {
      DTOGenre dtoGenre = DTOGenre.newBuilder()
          .setName(genre.getName()).build();
      dtoGenres.add(dtoGenre);
    }
    return dtoGenres;
  }

  private List<Genre> mapGenreDtoToDomain(List<GenreDTO> genreDTOs)
  {
    List<Genre> genres = new ArrayList<>();
    for (GenreDTO genreDTO : genreDTOs)
    {
      genres.add(new Genre(genreDTO.getName()));
    }
    return genres;
  }
}