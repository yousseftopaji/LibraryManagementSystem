package dk.via.sep3.shared.book;

/**
 * Simple POJO for REST API responses
 * This is separate from the gRPC DTOBook to avoid serialization issues
 */
public class BookDTO
{
  private String id;
  private String title;
  private String author;
  private String isbn;
  private State state;

  public BookDTO()
  {
  }

  public BookDTO(String id, String title, String author, String isbn, State state)
  {
    this.id = id;
    this.title = title;
    this.author = author;
    this.isbn = isbn;
    this.state = state;
  }

  public String getId()
  {
    return id;
  }

  public void setId(String id)
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

  public State getState()
  {
    return state;
  }

  public void setState(State state)
  {
    this.state = state;
  }
}

