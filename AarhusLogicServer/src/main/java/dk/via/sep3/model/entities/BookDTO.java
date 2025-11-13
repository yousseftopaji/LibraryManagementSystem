package dk.via.sep3.model.entities;

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
  private String state;
  private int noOfCopies;

  public BookDTO()
  {
  }

  public BookDTO(String id, String title, String author, String isbn, String state)
  {
    this.id = id;
    this.title = title;
    this.author = author;
    this.isbn = isbn;
    this.state = state;
    this.noOfCopies = 0;
  }

  public BookDTO(String id, String title, String author, String isbn, String state, int noOfCopies)
  {
    this.id = id;
    this.title = title;
    this.author = author;
    this.isbn = isbn;
    this.state = state;
    this.noOfCopies = noOfCopies;
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

  public String getState()
  {
    return state;
  }

  public void setState(String state)
  {
    this.state = state;
  }

  public int getNoOfCopies()
  {
    return noOfCopies;
  }

  public void setNoOfCopies(int noOfCopies)
  {
    this.noOfCopies = noOfCopies;
  }
}

