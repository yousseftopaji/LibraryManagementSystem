package dk.via.sep3.model.domain;

import java.sql.Date;

public class Loan
{
  Book book;
  User user;
  Date loanDate;
  Date dueDate;
  boolean isReturned;
  int numberOfExtensions;

  public Loan(Book book, User user, Date loanDate, Date dueDate, boolean isReturned, int numberOfExtensions)
  {
    this.book = book;
    this.user = user;
    this.loanDate = loanDate;
    this.dueDate = dueDate;
    this.isReturned = isReturned;
    this.numberOfExtensions = numberOfExtensions;
  }

  public Book getBook()
  {
    return book;
  }

  public void setBook(Book book)
  {
    this.book = book;
  }

  public User getUser()
  {
    return user;
  }

  public void setUser(User user)
  {
    this.user = user;
  }

  public Date getLoanDate()
  {
    return loanDate;
  }

  public void setLoanDate(Date loanDate)
  {
    this.loanDate = loanDate;
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
