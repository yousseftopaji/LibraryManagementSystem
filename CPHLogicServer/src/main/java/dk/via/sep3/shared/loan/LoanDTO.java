package dk.via.sep3.shared.loan;

public class LoanDTO
{
  private String id;
  private String borrowDate;
  private String dueDate;
  private boolean isReturned;
  private int numberOfExtensions;
  private String username;
  private int bookId;

  public LoanDTO()
  {
  }

  public LoanDTO(String id, String borrowDate, String dueDate, boolean isReturned,
                 int numberOfExtensions, String username, int bookId)
  {
    this.id = id;
    this.borrowDate = borrowDate;
    this.dueDate = dueDate;
    this.isReturned = isReturned;
    this.numberOfExtensions = numberOfExtensions;
    this.username = username;
    this.bookId = bookId;
  }

  public String getId()
  {
    return id;
  }

  public void setId(String id)
  {
    this.id = id;
  }

  public String getBorrowDate()
  {
    return borrowDate;
  }

  public void setBorrowDate(String borrowDate)
  {
    this.borrowDate = borrowDate;
  }

  public String getDueDate()
  {
    return dueDate;
  }

  public void setDueDate(String dueDate)
  {
    this.dueDate = dueDate;
  }

  public boolean isReturned()
  {
    return isReturned;
  }

  public void setReturned(boolean returned)
  {
    isReturned = returned;
  }

  public int getNumberOfExtensions()
  {
    return numberOfExtensions;
  }

  public void setNumberOfExtensions(int numberOfExtensions)
  {
    this.numberOfExtensions = numberOfExtensions;
  }

  public String getUsername()
  {
    return username;
  }

  public void setUsername(String username)
  {
    this.username = username;
  }

  public int getBookId()
  {
    return bookId;
  }

  public void setBookId(int bookId)
  {
    this.bookId = bookId;
  }
}
