package dk.via.sep3.shared.book;
import dk.via.sep3.model.domain.Genre;

import java.util.List;

public class BookDTO
{
  private int id;
  private String title;
  private String author;
  private String isbn;
  private String state;
  private List<GenreDTO> genres;

  public BookDTO()
  {}

  public BookDTO(int id, String title, String author, String isbn, String state, List<GenreDTO> genres)
  {
    this.id = id;
    this.title = title;
    this.author = author;
    this.isbn = isbn;
    this.state = state;
    this.genres = genres;
  }

  public int getId()
  {
    return id;
  }

  public void setId(int id)
  {
    this.id = id;
  }

  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

  public String getAuthor()
  {
    return author;
  }

  public void setAuthor(String author)
  {
    this.author = author;
  }

  public String getIsbn()
  {
    return isbn;
  }

  public void setIsbn(String isbn)
  {
    this.isbn = isbn;
  }

  public String getState()
  {
    return state;
  }

  public void setState(String state)
  {
    this.state = state;
  }

  public List<GenreDTO> getGenres()
  {
    return genres;
  }

  public void setGenres(List<GenreDTO> genres)
  {
    this.genres = genres;
  }
}

