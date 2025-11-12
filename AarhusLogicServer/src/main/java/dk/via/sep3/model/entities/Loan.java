package dk.via.sep3.model.entities;

import java.time.LocalDateTime;

public class Loan
{
  private String loanId;
  private String bookId;
  private String isbn;
  private String userId;
  private LocalDateTime loanDate;
  private LocalDateTime dueDate;

  public Loan(String loanId, String bookId, String isbn, String userId, LocalDateTime loanDate, LocalDateTime dueDate)
  {
    this.loanId = loanId;
    this.bookId = bookId;
    this.isbn = isbn;
    this.userId = userId;
    this.loanDate = loanDate;
    this.dueDate = dueDate;
  }

  public String getLoanId()
  {
    return loanId;
  }

  public void setLoanId(String loanId)
  {
    this.loanId = loanId;
  }

  public String getBookId()
  {
    return bookId;
  }

  public void setBookId(String bookId)
  {
    this.bookId = bookId;
  }

  public String getIsbn()
  {
    return isbn;
  }

  public void setIsbn(String isbn)
  {
    this.isbn = isbn;
  }

  public String getUserId()
  {
    return userId;
  }

  public void setUserId(String userId)
  {
    this.userId = userId;
  }

  public LocalDateTime getLoanDate()
  {
    return loanDate;
  }

  public void setLoanDate(LocalDateTime loanDate)
  {
    this.loanDate = loanDate;
  }

  public LocalDateTime getDueDate()
  {
    return dueDate;
  }

  public void setDueDate(LocalDateTime dueDate)
  {
    this.dueDate = dueDate;
  }
}

