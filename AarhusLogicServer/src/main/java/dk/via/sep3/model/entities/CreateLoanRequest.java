package dk.via.sep3.model.entities;

public class CreateLoanRequest
{
  private String username;
  private String bookId;
  private int loanDurationDays;

  public CreateLoanRequest()
  {
    this.loanDurationDays = 30; // default 30 days
  }

  public CreateLoanRequest(String username, String bookId, int loanDurationDays)
  {
    this.username = username;
    this.bookId = bookId;
    this.loanDurationDays = loanDurationDays;
  }

  public String getUsername()
  {
    return username;
  }

  public void setUsername(String username)
  {
    this.username = username;
  }

  public String getBookId()
  {
    return bookId;
  }

  public void setBookId(String bookId)
  {
    this.bookId = bookId;
  }

  public int getLoanDurationDays()
  {
    return loanDurationDays;
  }

  public void setLoanDurationDays(int loanDurationDays)
  {
    this.loanDurationDays = loanDurationDays;
  }
}

