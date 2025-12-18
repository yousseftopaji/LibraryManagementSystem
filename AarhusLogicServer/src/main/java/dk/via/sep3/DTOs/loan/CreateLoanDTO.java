package dk.via.sep3.DTOs.loan;

public class CreateLoanDTO
{
  private String username;
  private String bookISBN;

  public CreateLoanDTO(String username, String bookISBN)
  {
    this.username = username;
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

  public String getBookISBN()
  {
    return bookISBN;
  }

  public void setBookISBN(String bookISBN)
  {
    this.bookISBN = bookISBN;
  }
}
