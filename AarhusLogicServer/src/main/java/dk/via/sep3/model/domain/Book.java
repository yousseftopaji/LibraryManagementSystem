package dk.via.sep3.model.domain;

import dk.via.sep3.model.domain.state.BookState;

public class Book
{
  private BookState currentState;
  private String Isbn;
  private int id;

  public Book(String currentState, String isbn, int id)
  {
    this.Isbn = isbn;
    this.id = id;
    if (currentState.equalsIgnoreCase("available"))
      this.currentState = new dk.via.sep3.model.domain.state.AvailableState();
    else if (currentState.equalsIgnoreCase("borrowed"))
      this.currentState = new dk.via.sep3.model.domain.state.BorrowedState();
    else if (currentState.equalsIgnoreCase("reserved"))
      this.currentState = new dk.via.sep3.model.domain.state.ReservedState();
  }
  public void setId(int id)
  {
    this.id = id;
  }

  public void setIsbn(String isbn)
  {
    Isbn = isbn;
  }

  public int getId()
  {
    return id;
  }

  public String getIsbn()
  {
    return Isbn;
  }

  public void borrowBook()
  {
    currentState.borrowBook(this);
  }

  public void reserveBook()
  {
    currentState.reserveBook(this);
  }

  public void returnBook()
  {
    currentState.returnBook(this);
  }

  public BookState getCurrentState()
  {
    return currentState;
  }

  public void setCurrentState(BookState currentState)
  {
    this.currentState = currentState;
  }

  public String getCurrentStateName()
  {
    return currentState.getStateName();
  }
}
