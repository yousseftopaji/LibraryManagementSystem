package dk.via.sep3.model.domain;

import java.sql.Date;

public class Loan
{
  private int loanId;
  private int bookId;
  private String username;
  private String bookISBN;
  private Date borrowDate;
  private Date dueDate;
  private boolean isReturned;
  private int numberOfExtensions;

  public Loan()
  {

  }

  public Loan(int loanId, int bookId, String username, Date borrowDate, Date dueDate, boolean isReturned, int numberOfExtensions)
  {
    this.loanId = loanId;
    this.bookId = bookId;
    this.username = username;
    this.borrowDate = borrowDate;
    this.dueDate = dueDate;
    this.isReturned = isReturned;
    this.numberOfExtensions = numberOfExtensions;
  }

  public int getLoanId()
  {
    return loanId;
  }

  public void setLoanId(int loanId)
  {
    this.loanId = loanId;
  }

  public int getBookId()
  {
    return bookId;
  }

  public void setBookId(int bookId)
  {
    this.bookId = bookId;
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

  public Date getBorrowDate()
  {
    return borrowDate;
  }

  public void setBorrowDate(Date borrowDate)
  {
    this.borrowDate = borrowDate;
  }

  public Date getDueDate()
  {
    return dueDate;
  }

  public void setDueDate(Date dueDate)
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
}
