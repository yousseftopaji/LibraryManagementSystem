package dk.via.sep3.application.domain;

import java.util.List;

public class Book
{
  private int id;
  private String isbn;
  private String title;
  private String author;
  private State state;
  private List<Genre> genres;

  public Book()
  {
  }

  public Book(String isbn, String title, String author, State state, List<Genre> genres)
  {
    this.isbn = isbn;
    this.title = title;
    this.author = author;
    this.state = state;
    this.genres = genres;
  }

  public Book(int id, String isbn, String title, String author, State state, List<Genre> genres)
  {
    this.id = id;
    this.isbn = isbn;
    this.title = title;
    this.author = author;
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

  public String getIsbn()
  {
    return isbn;
  }

  public void setIsbn(String isbn)
  {
    this.isbn = isbn;
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

  public State getState()
  {
    return state;
  }

  public void setState(State state)
  {
    this.state = state;
  }

  public List<Genre> getGenres()
  {
    return genres;
  }

  public void setGenres(List<Genre> genres)
  {
    this.genres = genres;
  }

  public void addGenre(Genre genre)
  {
    this.genres.add(genre);
  }

  public boolean isAvailable()
  {
    return this.state == State.AVAILABLE;
  }
}
