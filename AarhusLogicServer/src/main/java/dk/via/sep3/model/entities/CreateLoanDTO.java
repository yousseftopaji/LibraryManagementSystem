package dk.via.sep3.model.entities;

public class CreateLoanDTO
{
  private String bookISBN;
  private String username;

  public CreateLoanDTO()
  {
  }

  public CreateLoanDTO(String bookISBN, String username)
  {
    this.bookISBN = bookISBN;
    this.username = username;
  }

  public String getBookISBN()
  {
    return bookISBN;
  }

  public void setBookISBN(String bookISBN)
  {
    this.bookISBN = bookISBN;
  }

  public String getUsername()
  {
    return username;
  }

  public void setUsername(String username)
  {
    this.username = username;
  }
}

